package com.example.gymfinder.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "userID",
        childColumns = "userID",
        onDelete = ForeignKey.CASCADE
))

public class Gym {
    @PrimaryKey(autoGenerate = true)
    public int gymCode;

    public String gymName;
    public String gymStreetName;
    public int gymStreetNumber;
    public String gymDescription;
    public int price;
    public String operationalHours;
    public int userID;
}
