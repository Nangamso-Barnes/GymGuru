package com.example.gymfinder.DAO;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.gymfinder.Database.UserResponse;

@Dao
public interface UserResponseDao {
    @Insert
    void saveUserResponse(UserResponse response);
}