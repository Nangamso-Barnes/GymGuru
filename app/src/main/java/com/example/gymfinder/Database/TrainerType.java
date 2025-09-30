package com.example.gymfinder.Database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Gym.class,
        parentColumns = "gymCode",
        childColumns = "gymCode",
        onDelete = ForeignKey.CASCADE))
public class TrainerType {
    @PrimaryKey(autoGenerate = true)
    public int trainerID;

    public String serviceList;

    @ColumnInfo(index = true)
    public Integer gymCode;
    public TrainerType() { } // Empty constructor for Room

    public TrainerType(String serviceList) {
        this.serviceList = serviceList;
    }
}