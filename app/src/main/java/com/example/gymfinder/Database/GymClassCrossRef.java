package com.example.gymfinder.Database;

import androidx.room.Entity;

@Entity(primaryKeys = {"gymCode","classID"})
public class GymClassCrossRef {
    public int gymCode;
    public int classID;
}
