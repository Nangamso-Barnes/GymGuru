package com.example.gymfinder.Database;

import androidx.room.Entity;

@Entity(primaryKeys = {"gymCode","equipID"})
public class GymEquipmentCrossRef {
    public int gymCode;
    public int equipID;
}
