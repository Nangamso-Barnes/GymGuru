
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
<<<<<<< HEAD
    public double price;
    public String openingTime;//changed for clarity
    public String closingTime;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] gymPicture; // Added the picture field as a byte array
=======
    public int price;
    public String startTime;
    public String endTime;
>>>>>>> da337631b2a496b88d3d07b69ad18d459239b937

    public Gym() { } // Empty constructor for Room

    public Gym(String gymName, int gymStreetNumber, String gymStreetName, String gymDescription, double price,byte[] gymPicture,String openingTime,String closingTime) {
        this.gymName = gymName;
        this.gymStreetNumber = gymStreetNumber;
        this.gymStreetName = gymStreetName;
        this.gymDescription = gymDescription;
        this.price = price;
<<<<<<< HEAD
        this.gymPicture=gymPicture;
        this.openingTime=openingTime;
        this.closingTime=closingTime;
=======
        this.startTime = startTime;
        this.endTime = endTime;
>>>>>>> da337631b2a496b88d3d07b69ad18d459239b937
    }
}