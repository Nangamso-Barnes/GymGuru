
package com.example.gymfinder.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Gym {
    @PrimaryKey(autoGenerate = true)
    public int gymCode;
    public String gymName;
    public String gymStreetName;
    public int gymStreetNumber;
    public String gymDescription;
    public String openingTime;//changed for clarity
    public String closingTime;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] gymPicture; // Added the picture field as a byte array
    public double price;

    public Gym() { } // Empty constructor for Room

    public Gym(String gymName, int gymStreetNumber, String gymStreetName, String gymDescription, double price,byte[] gymPicture,String openingTime,String closingTime) {
        this.gymName = gymName;
        this.gymStreetNumber = gymStreetNumber;
        this.gymStreetName = gymStreetName;
        this.gymDescription = gymDescription;
        this.price = price;
        this.gymPicture=gymPicture;
        this.openingTime=openingTime;
        this.closingTime=closingTime;


    }
}