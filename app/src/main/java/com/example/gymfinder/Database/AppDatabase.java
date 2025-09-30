// FileName: AppDatabase.java
package com.example.gymfinder.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gymfinder.DAO.GymDao;
import com.example.gymfinder.DAO.MiscDao;
import com.example.gymfinder.DAO.QuestionDao;
import com.example.gymfinder.DAO.UserDao;
import com.example.gymfinder.DAO.UserResponseDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Gym.class, User.class, UserResponse.class, Question.class,
        GymClassType.class, TrainerType.class, Equipment.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract GymDao gymDao();
    public abstract UserDao userDao();
    public abstract QuestionDao questionDao();
    public abstract UserResponseDao userResponseDao();
    public abstract MiscDao miscDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "GYM_GURU")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Get DAO instances
                GymDao gymDao = INSTANCE.gymDao();
                MiscDao miscDao = INSTANCE.miscDao();

                // 1. Populate TrainerType Table (10 records)
                miscDao.insertTrainerType(
                        new TrainerType("Cardio Specialist"), new TrainerType("Strength Coach"),
                        new TrainerType("Yoga Instructor"), new TrainerType("Pilates Instructor"),
                        new TrainerType("CrossFit L1 Trainer"), new TrainerType("Nutritionist"),
                        new TrainerType("Boxing Coach"), new TrainerType("HIIT Specialist"),
                        new TrainerType("Senior Fitness Coach"), new TrainerType("Sports Physiotherapist")
                );

                // 2. Populate GymClassType Table (10 records)
                miscDao.insertGymClassType(
                        new GymClassType("Spin Class"), new GymClassType("Zumba"),
                        new GymClassType("Yoga"), new GymClassType("Pilates Reformer"),
                        new GymClassType("CrossFit WOD"), new GymClassType("HIIT Circuit"),
                        new GymClassType("Boxing Fundamentals"), new GymClassType("Aqua Aerobics"),
                        new GymClassType("BodyPump"), new GymClassType("Seniors Mobility")
                );

                // 3. Populate Equipment Table (10 records)
                miscDao.insertEquipment(
                        new Equipment("Free Weights"), new Equipment("Cardio Machines"),
                        new Equipment("Resistance Machines"), new Equipment("Kettlebells"),
                        new Equipment("Rowing Machines"), new Equipment("TRX Suspension Trainers"),
                        new Equipment("Battle Ropes"), new Equipment("Plyometric Boxes"),
                        new Equipment("Smith Machine"), new Equipment("Cable Crossover")
                );

                // 4. Populate Gym Table (10 records)
                gymDao.insertGym(new Gym("Fitness Center", "Main Street", 123, "A modern fitness center", 50, "6:00 AM - 10:00 PM"));
                gymDao.insertGym(new Gym("Power Gym", "Oak Avenue", 456, "Strength training focused gym", 60, "5:00 AM - 11:00 PM"));
                gymDao.insertGym(new Gym("Wellness Studio", "Park Road", 789, "Yoga and wellness focused", 40, "7:00 AM - 9:00 PM"));
                gymDao.insertGym(new Gym("Iron Paradise", "Heugh Road", 101, "For serious bodybuilders", 75, "24 Hours"));
                gymDao.insertGym(new Gym("The Fitness Hub", "Cape Road", 212, "All-in-one family fitness", 55, "5:30 AM - 10:00 PM"));
                gymDao.insertGym(new Gym("Bayfront Boxing Club", "Marine Drive", 333, "Boxing and combat sports", 65, "10:00 AM - 9:00 PM"));
                gymDao.insertGym(new Gym("PE CrossFit", "Walmer Boulevard", 450, "High-Intensity Interval Training", 80, "6:00 AM - 8:00 PM"));
                gymDao.insertGym(new Gym("Zen Yoga & Pilates", "Stanley Street", 500, "Mind and body wellness", 45, "8:00 AM - 7:00 PM"));
                gymDao.insertGym(new Gym("Family Active Centre", "Buffelsfontein Road", 610, "Gym with childcare facilities", 50, "7:00 AM - 8:00 PM"));
                gymDao.insertGym(new Gym("Ultimate Performance", "Circular Drive", 720, "Elite athlete training facility", 90, "By Appointment"));

            });
        }
    };


}