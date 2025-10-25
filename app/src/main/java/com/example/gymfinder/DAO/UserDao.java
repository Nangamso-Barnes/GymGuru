package com.example.gymfinder.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gymfinder.Database.User;

@Dao
public interface UserDao {
    @Insert
    long register(User user);

    @Query("SELECT userID FROM User WHERE UserName = :username AND password = :password LIMIT 1")
    Integer login(String username, String password);

    @Query("SELECT * FROM User WHERE userID = :userId LIMIT 1")
    User getUserById(int userId);

    @Update
    int updateUser(User user);
    @Query("DELETE FROM User WHERE userID = :id")
    void deleteUserById(int id);


}
