package com.example.gymfinder.DAO;

// In com/example/gymfinder/DAO/MatchDao.java


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.gymfinder.Database.Match;
import com.example.gymfinder.HelperMethods.MatchWithGym;

import java.util.List;

@Dao
public interface MatchDao {


    @Query("SELECT * FROM matches WHERE userId = :userId ORDER BY matchScore DESC, distanceKm ASC")
    LiveData<List<Match>> getMatches(int userId);


    @Query("DELETE FROM matches WHERE userId = :userId")
    void clearUserMatches(int userId);


    @Insert
    void insertMatches(List<Match> matches);

    @Transaction
    @Query("SELECT * FROM matches WHERE userId = :userId ORDER BY matchScore DESC, distanceKm ASC")
    LiveData<List<MatchWithGym>> getMatchesWithGym(int userId);
    @Transaction
    default void clearAndInsertNewMatches(int userId, List<Match> newMatches) {
        // Step 1: Delete all old results
        clearUserMatches(userId);

        // Step 2: Insert all new results
        if (newMatches != null && !newMatches.isEmpty()) {
            insertMatches(newMatches);
        }
    }
}
