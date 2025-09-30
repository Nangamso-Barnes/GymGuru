
package com.example.gymfinder.Database;

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
    public int price;
    public String operationalHours;
    public Gym() { } // Empty constructor for Room

    public Gym(String gymName, String gymStreetName, int gymStreetNumber, String gymDescription, int price, String operationalHours) {
        this.gymName = gymName;
        this.gymStreetName = gymStreetName;
        this.gymStreetNumber = gymStreetNumber;
        this.gymDescription = gymDescription;
        this.price = price;
        this.operationalHours = operationalHours;
    }
}