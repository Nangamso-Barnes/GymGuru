package com.example.gymfinder.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gymfinder.DAO.GymDao;
import com.example.gymfinder.DAO.UserDao;

@Database(
        entities = {
                User.class,
                Gym.class,
               // Question.class,
               // UserResponse.class,
                //GymClassType.class,
                //TrainerType.class,
                //Equipment.class
        },
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract GymDao gymDao();
    private static volatile  AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "GYM_GURU"
                            )
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries() // ⚠️ Avoid in production
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
