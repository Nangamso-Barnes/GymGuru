package com.example.gymfinder.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.gymfinder.Database.Gym;
import java.util.List;

@Dao
public interface GymDao {

    @Insert
    long insertGym(Gym gym);

    // Use LiveData for reactivity
    @Query("SELECT * FROM Gym ORDER BY gymName ASC")
    LiveData<List<Gym>> getAllGyms();

    @Query("SELECT * FROM Gym WHERE gymCode = :gymCode LIMIT 1")
    Gym getGymByCode(int gymCode);

    @Query("DELETE FROM Gym WHERE gymCode = :gymCode")
    int deleteGym(int gymCode);

    // Keep your partner’s distinct time slot method
   // @Query("SELECT DISTINCT startTime || ' - ' || endTime FROM Gym")
   // List<String> getDistinctTimeSlots();
}
