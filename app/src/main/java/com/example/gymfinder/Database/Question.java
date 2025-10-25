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
    public Question() {}

    // This new constructor will be used for seeding
    public Question(String question, String questionTag) {
        this.question = question;
        this.questionTag = questionTag;
    }
}

