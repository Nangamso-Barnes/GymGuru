package com.example.gymfinder.Database;

import androidx.room.Entity;

@Entity(primaryKeys = {"gymCode","trainerID"})
public class GymTrainerCrossRef {
    public int gymCode;
    public int trainerID;

    public GymTrainerCrossRef(int gymCode, int trainerID) {
        this.gymCode = gymCode;
        this.trainerID = trainerID;
    }
}
