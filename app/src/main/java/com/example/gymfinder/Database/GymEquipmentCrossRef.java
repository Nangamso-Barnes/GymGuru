package com.example.gymfinder.Database;

import androidx.room.Entity;

@Entity(primaryKeys = {"gymCode","equipID"})
public class GymEquipmentCrossRef {
    public int gymCode;
    public int equipID;

    public GymEquipmentCrossRef(int gymCode, int equipID) {
        this.gymCode = gymCode;
        this.equipID = equipID;
    }
}
