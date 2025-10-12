package com.example.gymfinder.Database;

import androidx.room.Entity;

@Entity(primaryKeys = {"gymCode","trainerID"})
public class GymTrainerCrossRef {
    public int gymCode;
    public int trainerID;
}
