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
        GymClassType.class, TrainerType.class, Equipment.class,
         GymEquipmentCrossRef.class,GymClassCrossRef.class,GymTrainerCrossRef.class},
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


                // 1. Populate TrainerType Table and get IDs
                long tt1 = miscDao.insertTrainerType(new TrainerType("Cardio Specialist"));
                long tt2 = miscDao.insertTrainerType(new TrainerType("Strength Coach"));
                long tt3 = miscDao.insertTrainerType(new TrainerType("Yoga Instructor"));
                long tt4 = miscDao.insertTrainerType(new TrainerType("Pilates Instructor"));
                long tt5 = miscDao.insertTrainerType(new TrainerType("CrossFit L1 Trainer"));
                long tt6 = miscDao.insertTrainerType(new TrainerType("Nutritionist"));
                long tt7 = miscDao.insertTrainerType(new TrainerType("Boxing Coach"));
                long tt8 = miscDao.insertTrainerType(new TrainerType("HIIT Specialist"));
                long tt9 = miscDao.insertTrainerType(new TrainerType("Senior Fitness Coach"));
                long tt10 = miscDao.insertTrainerType(new TrainerType("Sports Physiotherapist"));

                // 2. Populate GymClassType Table and get IDs
                long gct1 = miscDao.insertGymClassType(new GymClassType("Spin Class"));
                long gct2 = miscDao.insertGymClassType(new GymClassType("Zumba"));
                long gct3 = miscDao.insertGymClassType(new GymClassType("Yoga"));
                long gct4 = miscDao.insertGymClassType(new GymClassType("Pilates Reformer"));
                long gct5 = miscDao.insertGymClassType(new GymClassType("CrossFit WOD"));
                long gct6 = miscDao.insertGymClassType(new GymClassType("HIIT Circuit"));
                long gct7 = miscDao.insertGymClassType(new GymClassType("Boxing Fundamentals"));
                long gct8 = miscDao.insertGymClassType(new GymClassType("Aqua Aerobics"));
                long gct9 = miscDao.insertGymClassType(new GymClassType("BodyPump"));
                long gct10 = miscDao.insertGymClassType(new GymClassType("Seniors Mobility"));

                // 3. Populate Equipment Table and get IDs
                long eq1 = miscDao.insertEquipment(new Equipment("Free Weights"));
                long eq2 = miscDao.insertEquipment(new Equipment("Cardio Machines"));
                long eq3 = miscDao.insertEquipment(new Equipment("Resistance Machines"));
                long eq4 = miscDao.insertEquipment(new Equipment("Kettlebells"));
                long eq5 = miscDao.insertEquipment(new Equipment("Rowing Machines"));
                long eq6 = miscDao.insertEquipment(new Equipment("TRX Suspension Trainers"));
                long eq7 = miscDao.insertEquipment(new Equipment("Battle Ropes"));
                long eq8 = miscDao.insertEquipment(new Equipment("Plyometric Boxes"));
                long eq9 = miscDao.insertEquipment(new Equipment("Smith Machine"));
                long eq10 = miscDao.insertEquipment(new Equipment("Cable Crossover"));

                // 4. Populate Gym Table and get IDs
                long gym1 = gymDao.insertGym(new Gym("Fitness Center", 123, "Main Street", "A modern fitness center", 50.0, null, "6:00 AM", "10:00 PM"));
                long gym2 = gymDao.insertGym(new Gym("Power Gym", 456, "Oak Avenue", "Strength training focused gym", 60.0, null, "5:00 AM", "11:00 PM"));
                long gym3 = gymDao.insertGym(new Gym("Wellness Studio", 789, "Park Road", "Yoga and wellness focused", 40.0, null, "7:00 AM", "9:00 PM"));
                long gym4 = gymDao.insertGym(new Gym("Iron Paradise", 101, "Heugh Road", "For serious bodybuilders", 75.0, null, "24 Hours", "24 Hours"));
                long gym5 = gymDao.insertGym(new Gym("The Fitness Hub", 212, "Cape Road", "All-in-one family fitness", 55.0, null, "5:30 AM", "10:00 PM"));
                long gym6 = gymDao.insertGym(new Gym("Bayfront Boxing Club", 333, "Marine Drive", "Boxing and combat sports", 65.0, null, "10:00 AM", "9:00 PM"));
                long gym7 = gymDao.insertGym(new Gym("PE CrossFit", 450, "Walmer Boulevard", "High-Intensity Interval Training", 80.0, null, "6:00 AM", "8:00 PM"));
                long gym8 = gymDao.insertGym(new Gym("Zen Yoga & Pilates", 500, "Stanley Street", "Mind and body wellness", 45.0, null, "8:00 AM", "7:00 PM"));
                long gym9 = gymDao.insertGym(new Gym("Family Active Centre", 610, "Buffelsfontein Road", "Gym with childcare facilities", 50.0, null, "7:00 AM", "8:00 PM"));
                long gym10 = gymDao.insertGym(new Gym("Ultimate Performance", 720, "Circular Drive", "Elite athlete training facility", 90.0, null, "By Appointment", "By Appointment"));

                // 5. Create Links between Gyms and Features
                // Link "Power Gym" (gym2) to Free Weights (eq1), Strength Coach (tt2), and BodyPump (gct9)
                miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef() {{ gymCode = (int) gym2; equipID = (int) eq1; }});
                miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef() {{ gymCode = (int) gym2; trainerID = (int) tt2; }});
                miscDao.insertGymClassCrossRef(new GymClassCrossRef() {{ gymCode = (int) gym2; classID = (int) gct9; }});

                // Link "Wellness Studio" (gym3) to Yoga (gct3), Pilates (gct4), and a Yoga Instructor (tt3)
                miscDao.insertGymClassCrossRef(new GymClassCrossRef() {{ gymCode = (int) gym3; classID = (int) gct3; }});
                miscDao.insertGymClassCrossRef(new GymClassCrossRef() {{ gymCode = (int) gym3; classID = (int) gct4; }});
                miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef() {{ gymCode = (int) gym3; trainerID = (int) tt3; }});

                // Link "PE CrossFit" (gym7) to Kettlebells (eq4), Rowing Machines (eq5), and a CrossFit Trainer (tt5)
                miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef() {{ gymCode = (int) gym7; equipID = (int) eq4; }});
                miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef() {{ gymCode = (int) gym7; equipID = (int) eq5; }});
                miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef() {{ gymCode = (int) gym7; trainerID = (int) tt5; }});

            });
        }
    };


}