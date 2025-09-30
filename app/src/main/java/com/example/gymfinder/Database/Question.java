package com.example.gymfinder.Database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Question {
    @PrimaryKey(autoGenerate = true)
    public int queID;

    public String question;
}

