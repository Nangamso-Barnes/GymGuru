package com.example.gymfinder.DAO;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.gymfinder.Database.Gym;

import java.util.List;

@Dao
public interface GymDao {
    @Insert
    long insertGym(Gym gym);

    @Query("SELECT * FROM Gym")
    List<Gym> getAllGyms();

    // Add this new method
    @Query("SELECT * FROM Gym WHERE gymCode = :gymCode LIMIT 1")
    Gym getGymByCode(int gymCode);

    @Query("DELETE FROM Gym WHERE gymCode = :gymCode")
    int deleteGym(int gymCode);

    @Query("SELECT operationalHours FROM Gym")
    List<String> getAllOperationalHours();
}