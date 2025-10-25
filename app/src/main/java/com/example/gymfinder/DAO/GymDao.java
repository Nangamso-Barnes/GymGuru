package com.example.gymfinder.DAO;

import androidx.lifecycle.LiveData; // <-- Import LiveData
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gymfinder.Database.Gym;
import java.util.List;

@Dao
public interface GymDao {
    @Insert
    long insertGym(Gym gym);
    @Update
    int updateGym(Gym gym);

    @Query("SELECT * FROM Gym ORDER BY gymName ASC")
    LiveData<List<Gym>> getAllGyms();

    @Query("SELECT * FROM Gym WHERE gymCode = :gymCode LIMIT 1")
    Gym getGymByCode(int gymCode);

    @Query("DELETE FROM Gym WHERE gymCode = :gymCode")
    int deleteGym(int gymCode);


    @Query("SELECT DISTINCT openingTime || ' - ' || closingTime FROM Gym")
    List<String> getDistinctTimeSlots();

    // This is for the matching algorithm's background thread
    @Query("SELECT * FROM Gym")
    List<Gym> getAllGymsSync();

}