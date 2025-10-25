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

    @Query("SELECT * FROM User WHERE UserName = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM User WHERE userID = :userId LIMIT 1")
    User getUserById(int userId);

    @Update
    int updateUser(User user);
<<<<<<< HEAD
    @Query("DELETE FROM User WHERE userID = :id")
    void deleteUserById(int id);


=======
    @Query("UPDATE User SET latitude = :lat, longitude = :lon WHERE userID = :userId")
    void updateUserCoordinates(int userId, double lat, double lon);
>>>>>>> 536b443a752ee791cff3d560523de874951a16b6
}
