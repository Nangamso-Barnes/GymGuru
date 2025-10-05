package com.example.gymfinder.DAO;

import androidx.lifecycle.LiveData; // <-- Import LiveData
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.gymfinder.Database.Gym;
import java.util.List;

@Dao
public interface GymDao {
    @Insert
    long insertGym(Gym gym);

    // --- This is the improved method ---
    @Query("SELECT * FROM Gym ORDER BY gymName ASC")
    LiveData<List<Gym>> getAllGyms(); // Changed to return LiveData

    @Query("SELECT * FROM Gym WHERE gymCode = :gymCode LIMIT 1")
    Gym getGymByCode(int gymCode);

    @Query("DELETE FROM Gym WHERE gymCode = :gymCode")
    int deleteGym(int gymCode);
}