package com.example.gymfinder.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "User")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int userID;

    public String UserName;
    public String FirstName;
    public String LastName;
    public String emailAddress;
    public String password;
    public String contactNumber;
    public String gender;
    public String streetName;
    public String streetNumber;

}

