package com.example.gymfinder.HelperMethods;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gymfinder.DAO.*;
import com.example.gymfinder.Database.*;
import com.example.gymfinder.HelperMethods.LocationUtils;
import com.example.gymfinder.HelperMethods.UserPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchViewModel extends AndroidViewModel {

    private final UserDao userDao;
    private final UserResponseDao userResponseDao;
    private final GymDao gymDao;
    private final MatchDao matchDao;
    private final MiscDao miscDao;
    private final QuestionDao questionDao;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MatchViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(getApplication());

        userDao = db.userDao();
        userResponseDao = db.userResponseDao();
        gymDao = db.gymDao();
        matchDao = db.matchDao();
        miscDao = db.miscDao();
        questionDao = db.questionDao();
    }

    // This is the public LiveData the Activity will observe
    public LiveData<List<MatchWithGym>> getMatches(int userId) {
        return matchDao.getMatchesWithGym(userId);
    }

    public void generateMatches(int userId) {
        executor.execute(() -> {
            User user = userDao.getUserById(userId);
            if (user == null || user.latitude == 0.0) return;

            List<UserResponse> userResponses = userResponseDao.getResponsesForUser(userId);
            UserPreferences prefs = new UserPreferences();

            // --- Find Question IDs dynamically ---
            int BUDGET_QUESTION_ID = 0;
            int CLASSES_QUESTION_ID = 0;
            int TRAINER_QUESTION_ID = 0;
            int FREE_WEIGHTS_ID = 0;
            
            try {
                BUDGET_QUESTION_ID = questionDao.getQuestionIdByTag("budget");
                CLASSES_QUESTION_ID = questionDao.getQuestionIdByTag("classes");
                TRAINER_QUESTION_ID = questionDao.getQuestionIdByTag("trainer");
                FREE_WEIGHTS_ID = questionDao.getQuestionIdByTag("free_weights");
            } catch (Exception e) {
                // Questions not found, continue with default values
                e.printStackTrace();
            }

            for (UserResponse res : userResponses) {
                try {
                    if (res.queID == BUDGET_QUESTION_ID) {
                        prefs.budget = Double.parseDouble(res.answer);
                    } else if (res.queID == CLASSES_QUESTION_ID) {
                        prefs.wantsClasses = res.answer.equalsIgnoreCase("Yes");
                    } else if (res.queID == TRAINER_QUESTION_ID) {
                        prefs.wantsTrainer = res.answer.equalsIgnoreCase("Yes");
                    } else if (res.queID == FREE_WEIGHTS_ID) {
                        prefs.wantsFreeWeights = res.answer.equalsIgnoreCase("Yes");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            List<Gym> allGyms = gymDao.getAllGymsSync();
            if (allGyms == null || allGyms.isEmpty()) {
                return; // No gyms to match against
            }
            
            List<Match> newMatches = new ArrayList<>();
            final double maxDistanceKm = 15.0;

            for (Gym gym : allGyms) {
                double matchScore = 100.0;

                double distance = LocationUtils.calculateDistance(
                        user.latitude, user.longitude,
                        gym.latitude, gym.longitude
                );
                if (distance > maxDistanceKm) continue;
                if (gym.price > prefs.budget) continue;

                matchScore -= (distance * 5);

                try {
                    if (prefs.wantsClasses && miscDao.getClassesForGym(gym.gymCode).isEmpty()) {
                        matchScore -= 20;
                    }
                    if (prefs.wantsTrainer && miscDao.getTrainersForGym(gym.gymCode).isEmpty()) {
                        matchScore -= 20;
                    }
                    if (prefs.wantsFreeWeights) {
                        Equipment freeWeights = miscDao.findEquipmentByName("Free Weights");
                        if (freeWeights != null) {
                            List<Equipment> gymEquipment = miscDao.getEquipmentForGym(gym.gymCode);
                            boolean hasIt = false;
                            for(Equipment e : gymEquipment) {
                                if (e.equipID == freeWeights.equipID) {
                                    hasIt = true;
                                    break;
                                }
                            }
                            if (!hasIt) matchScore -= 15;
                        }
                    }
                } catch (Exception e) {
                    // Handle database errors gracefully
                    e.printStackTrace();
                }

                if (matchScore > 0) {
                    Match match = new Match();
                    match.userId = userId;
                    match.gymId = gym.gymCode;
                    match.matchScore = matchScore;
                    match.distanceKm = distance;
                    newMatches.add(match);
                }
            }
            try {
                matchDao.clearAndInsertNewMatches(userId, newMatches);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}