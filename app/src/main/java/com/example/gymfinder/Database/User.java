
package com.example.gymfinder.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "User",
        foreignKeys = @ForeignKey(entity = Gym.class,
                parentColumns = "gymCode",
                childColumns = "gymCode",
                onDelete = ForeignKey.SET_NULL)) // Or CASCADE, depending on desired behavior
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
    public String userRole;
    public double latitude;
    public double longitude;

    @ColumnInfo(index = true)
    public Integer gymCode;
    public User() {
        // Empty constructor for Room
    }
}