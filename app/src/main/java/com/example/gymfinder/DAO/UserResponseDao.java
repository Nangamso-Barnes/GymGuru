package com.example.gymfinder.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.gymfinder.Database.UserResponse;

import java.util.List;

@Dao
public interface UserResponseDao {
    @Insert
    void saveUserResponse(UserResponse response);
    // This is the new method we need
    @Query("SELECT * FROM UserResponse WHERE userID = :userId")
    List<UserResponse> getResponsesForUser(int userId);

    // You'll also need a way to insert all responses at once
    @Insert
    void insertAllResponses(List<UserResponse> responses);

    @Query("DELETE FROM UserResponse WHERE userID = :userId")
    void clearUserResponses(int userId);
    @Transaction
    default void clearAndInsertAllResponses(int userId, List<UserResponse> responses) {
        clearUserResponses(userId);
        insertAllResponses(responses);
    }
}