package com.example.gymfinder.Database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gymfinder.DAO.*;
import com.example.gymfinder.Database.User;
import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.Database.Equipment;
import com.example.gymfinder.Database.GymClassType;
import com.example.gymfinder.Database.TrainerType;
import com.example.gymfinder.Database.GymClassCrossRef;
import com.example.gymfinder.Database.GymEquipmentCrossRef;
import com.example.gymfinder.Database.GymTrainerCrossRef;
import com.example.gymfinder.Database.UserResponse;
import com.example.gymfinder.Database.Question;
import com.example.gymfinder.Database.Match;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        User.class,
        Gym.class,
        Equipment.class,
        GymClassType.class,
        TrainerType.class,
        GymClassCrossRef.class,
        GymEquipmentCrossRef.class,
        GymTrainerCrossRef.class,
        UserResponse.class,
        Question.class,
        Match.class
}, version = 1, exportSchema = false) // Increment version or uninstall app
public abstract class AppDatabase extends RoomDatabase {

    // Your DAOs
    public abstract UserDao userDao();
    public abstract GymDao gymDao();
    public abstract MiscDao miscDao();
    public abstract UserResponseDao userResponseDao();
    public abstract QuestionDao questionDao();
    public abstract MatchDao matchDao();

    private static volatile AppDatabase INSTANCE;

    // Executor for background seeding
    private static final ExecutorService databaseWriteExecutor =
            Executors.newSingleThreadExecutor();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "GYM_GURU")
                            .addCallback(roomCallback) // This line adds the populated data
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // --- THIS IS THE CALLBACK THAT POPULATES THE DATABASE ---
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // This runs on a background thread
            databaseWriteExecutor.execute(() -> {
                // Get all DAOs
                UserDao userDao = INSTANCE.userDao();
                GymDao gymDao = INSTANCE.gymDao();
                MiscDao miscDao = INSTANCE.miscDao();
                QuestionDao questionDao = INSTANCE.questionDao();

                // 1. Create Questions
                createTestQuestions(questionDao);

                // 2. Create Master Lists (Equipment, Classes, Trainers)
                long[] masterIds = createMasterData(miscDao);
                long freeWeightsId = masterIds[0];
                long yogaMatsId = masterIds[1];
                long yogaClassId = masterIds[2];
                long spinClassId = masterIds[3];
                long weightTrainerId = masterIds[4];
                long yogaTrainerId = masterIds[5];

                // 3. Create Gyms (Must be done before Gym Admin)
                long gym2Id = createTestGyms(gymDao, miscDao, freeWeightsId, yogaMatsId, yogaClassId, spinClassId, weightTrainerId, yogaTrainerId);

<<<<<<< HEAD
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

                miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int) gym2, (int) eq1));

                miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int) gym2, (int) tt2));

                miscDao.insertGymClassCrossRef(new GymClassCrossRef((int) gym2, (int) gct9));

                miscDao.insertGymClassCrossRef(new GymClassCrossRef((int) gym3, (int) gct3));


                miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int) gym3, (int) tt3));

                miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int) gym7, (int) eq4));

                miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int) gym7, (int) eq5));

                miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int) gym7, (int) tt5));
=======
                // 4. Create Admin/Gym Admin Users
                createSystemUsers(userDao, (int)gym2Id); // Pass gym2Id to link the Gym Admin

                // 5. Create 10 Test Users with PE addresses
                createTestUsers(userDao);
>>>>>>> 536b443a752ee791cff3d560523de874951a16b6
            });
        }
    };

    // --- SEED DATA METHODS ---

    private static void createTestQuestions(QuestionDao dao) {
        // Uses the constructor: new Question(String question, String questionTag)
        dao.insertQuestion(new Question("What is your maximum monthly budget?", "budget"));
        dao.insertQuestion(new Question("Are you interested in personal trainers?", "trainer_preference"));
        dao.insertQuestion(new Question("Are you interested in fitness classes?", "class_preference"));
        dao.insertQuestion(new Question("What time slot do you prefer?", "time_slot"));
        dao.insertQuestion(new Question("What equipment is most important to you?", "equipment_type"));
        dao.insertQuestion(new Question("Do you need free weights?", "free_weights"));
    }

    private static long[] createMasterData(MiscDao dao) {
        // Create master data and return their new IDs
        long freeWeightsId = dao.insertEquipment(new Equipment("Free Weights"));
        long yogaMatsId = dao.insertEquipment(new Equipment("Yoga Mats"));
        dao.insertEquipment(new Equipment("Treadmill"));
        dao.insertEquipment(new Equipment("Spinning Bikes"));

        long yogaClassId = dao.insertGymClassType(new GymClassType("Yoga"));
        long spinClassId = dao.insertGymClassType(new GymClassType("Spinning"));
        dao.insertGymClassType(new GymClassType("Boxing"));

        long weightTrainerId = dao.insertTrainerType(new TrainerType("Weightlifting"));
        long yogaTrainerId = dao.insertTrainerType(new TrainerType("Yoga"));
        dao.insertTrainerType(new TrainerType("Cardio"));

        // Return IDs of items we need to link
        return new long[] {freeWeightsId, yogaMatsId, yogaClassId, spinClassId, weightTrainerId, yogaTrainerId};
    }

    private static void createSystemUsers(UserDao dao, int gymAdminGymId) {
        // Admin
        User admin = new User();
        admin.UserName = "admin";
        admin.password = "admin123";
        admin.userRole = "Admin";
        admin.FirstName = "Admin";
        admin.LastName = "User";
        admin.emailAddress = "admin@app.com";
        admin.streetName = "Rink Street"; // Central
        admin.streetNumber = "1";
        admin.contactNumber = "000000000";
        admin.gender = "N/A";
        admin.latitude = -33.9638;
        admin.longitude = 25.6193;
        // admin.gymCode is null
        dao.register(admin);

        // Gym Admin (Linked to Planet Fitness Walmer)
        User gymAdmin = new User();
        gymAdmin.UserName = "gymadmin";
        gymAdmin.password = "gym123";
        gymAdmin.userRole = "Gym Admin";
        gymAdmin.FirstName = "Gym";
        gymAdmin.LastName = "Manager";
        gymAdmin.emailAddress = "gym@app.com";
        gymAdmin.streetName = "Main Road"; // Walmer
        gymAdmin.streetNumber = "120";
        gymAdmin.contactNumber = "111111111";
        gymAdmin.gender = "N/A";
        gymAdmin.latitude = -33.9822;
        gymAdmin.longitude = 25.5785;
        gymAdmin.gymCode = gymAdminGymId; // Link to Planet Fitness
        dao.register(gymAdmin);
    }

    private static void createTestUsers(UserDao dao) {
        // User 1 (Summerstrand)
        User u1 = new User();
        u1.UserName = "user1"; u1.password = "pass123"; u1.userRole = "User";
        u1.FirstName = "Alice"; u1.LastName = "Smith"; u1.emailAddress = "alice@test.com";
        u1.streetName = "Admiralty Way"; u1.streetNumber = "10";
        u1.contactNumber = "123456781"; u1.gender = "Female";
        u1.latitude = -33.9907; u1.longitude = 25.6668; // Summerstrand
        dao.register(u1);

        // User 2 (Walmer)
        User u2 = new User();
        u2.UserName = "user2"; u2.password = "pass123"; u2.userRole = "User";
        u2.FirstName = "Bob"; u2.LastName = "Johnson"; u2.emailAddress = "bob@test.com";
        u2.streetName = "Main Road"; u2.streetNumber = "250";
        u2.contactNumber = "123456782"; u2.gender = "Male";
        u2.latitude = -33.9788; u2.longitude = 25.5684; // Walmer
        dao.register(u2);

        // User 3 (Central)
        User u3 = new User();
        u3.UserName = "user3"; u3.password = "pass123"; u3.userRole = "User";
        u3.FirstName = "Charlie"; u3.LastName = "Brown"; u3.emailAddress = "charlie@test.com";
        u3.streetName = "Parliament Street"; u3.streetNumber = "50";
        u3.contactNumber = "123456783"; u3.gender = "Male";
        u3.latitude = -33.9625; u3.longitude = 25.6175; // Central
        dao.register(u3);

        // User 4 (Newton Park)
        User u4 = new User();
        u4.UserName = "user4"; u4.password = "pass123"; u4.userRole = "User";
        u4.FirstName = "Diana"; u4.LastName = "Prince"; u4.emailAddress = "diana@test.com";
        u4.streetName = "Newton Street"; u4.streetNumber = "30";
        u4.contactNumber = "123456784"; u4.gender = "Female";
        u4.latitude = -33.9455; u4.longitude = 25.5833; // Newton Park
        dao.register(u4);

        // User 5 (Mill Park)
        User u5 = new User();
        u5.UserName = "user5"; u5.password = "pass123"; u5.userRole = "User";
        u5.FirstName = "Eve"; u5.LastName = "Adams"; u5.emailAddress = "eve@test.com";
        u5.streetName = "Links Road"; u5.streetNumber = "5";
        u5.contactNumber = "123456785"; u5.gender = "Female";
        u5.latitude = -33.9550; u5.longitude = 25.5975;
        dao.register(u5);

        // User 6 (Humewood)
        User u6 = new User();
        u6.UserName = "user6"; u6.password = "pass123"; u6.userRole = "User";
        u6.FirstName = "Frank"; u6.LastName = "Castle"; u6.emailAddress = "frank@test.com";
        u6.streetName = "Beach Road"; u6.streetNumber = "121";
        u6.contactNumber = "123456786"; u6.gender = "Male";
        u6.latitude = -33.9740; u6.longitude = 25.6580;
        dao.register(u6);

        // User 7 (Lorraine)
        User u7 = new User();
        u7.UserName = "user7"; u7.password = "pass123"; u7.userRole = "User";
        u7.FirstName = "Grace"; u7.LastName = "Kelly"; u7.emailAddress = "grace@test.com";
        u7.streetName = "Circular Drive"; u7.streetNumber = "800";
        u7.contactNumber = "123456787"; u7.gender = "Female";
        u7.latitude = -33.9870; u7.longitude = 25.5380;
        dao.register(u7);

        // User 8 (Greenacres)
        User u8 = new User();
        u8.UserName = "user8"; u8.password = "pass123"; u8.userRole = "User";
        u8.FirstName = "Hank"; u8.LastName = "Pym"; u8.emailAddress = "hank@test.com";
        u8.streetName = "Cape Road"; u8.streetNumber = "500";
        u8.contactNumber = "123456788"; u8.gender = "Male";
        u8.latitude = -33.9490; u8.longitude = 25.5750;
        dao.register(u8);

        // User 9 (Summerstrand, near beach)
        User u9 = new User();
        u9.UserName = "user9"; u9.password = "pass123"; u9.userRole = "User";
        u9.FirstName = "Ivy"; u9.LastName = "Poison"; u9.emailAddress = "ivy@test.com";
        u9.streetName = "Marine Drive"; u9.streetNumber = "22";
        u9.contactNumber = "123456789"; u9.gender = "Female";
        u9.latitude = -33.9865; u9.longitude = 25.6710;
        dao.register(u9);

        // User 10 (St George's Park)
        User u10 = new User();
        u10.UserName = "user10"; u10.password = "pass123"; u10.userRole = "User";
        u10.FirstName = "Jack"; u10.LastName = "Ryan"; u10.emailAddress = "jack@test.com";
        u10.streetName = "Park Drive"; u10.streetNumber = "1";
        u10.contactNumber = "123456710"; u10.gender = "Male";
        u10.latitude = -33.9640; u10.longitude = 25.6075;
        dao.register(u10);
    }

    // This method now returns the ID of Gym 2 for the Gym Admin
    private static long createTestGyms(GymDao gymDao, MiscDao miscDao, long freeWeightsId, long yogaMatsId, long yogaClassId, long spinClassId, long weightTrainerId, long yogaTrainerId) {
        byte[] dummyPic = null;

        // 1. Virgin Active (Greenacres)
        Gym gym1 = new Gym("Virgin Active Greenacres", 50, "Cape Road", "Premium, large gym.", 75.0, dummyPic, "05:00", "21:00");
        gym1.latitude = -33.9485; gym1.longitude = 25.5730;
        long gym1Id = gymDao.insertGym(gym1);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym1Id, (int)freeWeightsId));
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym1Id, (int)yogaMatsId));
        miscDao.insertGymClassCrossRef(new GymClassCrossRef((int)gym1Id, (int)yogaClassId));
        miscDao.insertGymClassCrossRef(new GymClassCrossRef((int)gym1Id, (int)spinClassId));
        miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int)gym1Id, (int)weightTrainerId));

        // 2. Planet Fitness (Walmer)
        Gym gym2 = new Gym("Planet Fitness Walmer", 120, "Main Road", "Modern gym with great classes.", 65.0, dummyPic, "05:00", "21:00");
        gym2.latitude = -33.9815; gym2.longitude = 25.5770;
        long gym2Id = gymDao.insertGym(gym2); // We will return this ID
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym2Id, (int)freeWeightsId));
        miscDao.insertGymClassCrossRef(new GymClassCrossRef((int)gym2Id, (int)spinClassId));
        miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int)gym2Id, (int)weightTrainerId));

        // 3. Viva Gym (Moffett on Main)
        Gym gym3 = new Gym("Viva Gym Moffett", 600, "William Moffet Expy", "Affordable and accessible.", 35.0, dummyPic, "05:00", "22:00");
        gym3.latitude = -33.9775; gym3.longitude = 25.5580;
        long gym3Id = gymDao.insertGym(gym3);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym3Id, (int)freeWeightsId));
        miscDao.insertGymClassCrossRef(new GymClassCrossRef((int)gym3Id, (int)spinClassId));

        // 4. Profiles Gym (Summerstrand)
        Gym gym4 = new Gym("Profiles Health Club", 1, "Marine Drive", "Gym with a sea view.", 70.0, dummyPic, "06:00", "20:00");
        gym4.latitude = -33.9840; gym4.longitude = 25.6700;
        long gym4Id = gymDao.insertGym(gym4);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym4Id, (int)freeWeightsId));
        miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int)gym4Id, (int)weightTrainerId));

        // 5. Body Concept (Central)
        Gym gym5 = new Gym("Body Concept Gym", 35, "Rink Street", "Old school bodybuilding gym.", 40.0, dummyPic, "06:00", "21:00");
        gym5.latitude = -33.9630; gym5.longitude = 25.6190;
        long gym5Id = gymDao.insertGym(gym5);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym5Id, (int)freeWeightsId));
        miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int)gym5Id, (int)weightTrainerId));

        // 6. Movement (Walmer)
        Gym gym6 = new Gym("Movement", 15, "Main Road", "Yoga and Pilates Studio.", 80.0, dummyPic, "07:00", "19:00");
        gym6.latitude = -33.9750; gym6.longitude = 25.5800;
        long gym6Id = gymDao.insertGym(gym6);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym6Id, (int)yogaMatsId));
        miscDao.insertGymClassCrossRef(new GymClassCrossRef((int)gym6Id, (int)yogaClassId));
        miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int)gym6Id, (int)yogaTrainerId));

        // 7. CrossFit Algoa (Central)
        Gym gym7 = new Gym("CrossFit Algoa", 1, "Lower Valley Road", "CrossFit box.", 85.0, dummyPic, "05:00", "19:00");
        gym7.latitude = -33.9680; gym7.longitude = 25.6320;
        long gym7Id = gymDao.insertGym(gym7);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym7Id, (int)freeWeightsId));
        miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int)gym7Id, (int)weightTrainerId));

        // 8. Zone Fitness (Lorraine)
        Gym gym8 = new Gym("Zone Fitness Lorraine", 17, "Circular Drive", "Budget-friendly chain.", 30.0, dummyPic, "05:00", "21:00");
        gym8.latitude = -33.9860; gym8.longitude = 25.5350;
        long gym8Id = gymDao.insertGym(gym8);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym8Id, (int)freeWeightsId));
        miscDao.insertGymClassCrossRef(new GymClassCrossRef((int)gym8Id, (int)spinClassId));

        // 9. NMMU Madibaz Gym (Summerstrand)
        Gym gym9 = new Gym("Madibaz Gym", 1, "University Way", "University gym.", 25.0, dummyPic, "06:00", "21:00");
        gym9.latitude = -34.0060; gym9.longitude = 25.6680;
        long gym9Id = gymDao.insertGym(gym9);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym9Id, (int)freeWeightsId));
        miscDao.insertGymTrainerCrossRef(new GymTrainerCrossRef((int)gym9Id, (int)weightTrainerId));

        // 10. Ultimate Fitness (Newton Park)
        Gym gym10 = new Gym("Ultimate Fitness", 3, "Newton Street", "24/7 access gym.", 50.0, dummyPic, "00:00", "23:59");
        gym10.latitude = -33.9450; gym10.longitude = 25.5840;
        long gym10Id = gymDao.insertGym(gym10);
        miscDao.insertGymEquipmentCrossRef(new GymEquipmentCrossRef((int)gym10Id, (int)freeWeightsId));

        return gym2Id; // Return this ID for the Gym Admin
    }
}

