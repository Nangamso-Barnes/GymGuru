package com.example.gymfinder.Database;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Question {
    @PrimaryKey(autoGenerate = true)
    public int queID;

    public String question;
    @ColumnInfo(name = "question_tag")
    public String questionTag;
}

