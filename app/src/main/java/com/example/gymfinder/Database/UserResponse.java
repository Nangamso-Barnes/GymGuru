package com.example.gymfinder.Database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = User.class,
                parentColumns = "userID",
                childColumns = "userID",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Question.class,
                parentColumns = "queID",
                childColumns = "queID",
                onDelete = ForeignKey.CASCADE)
})
public class UserResponse {
    @PrimaryKey(autoGenerate = true)
    public int resID;

    public String answer;

    @ColumnInfo(index = true)
    public int userID;

    @ColumnInfo(index = true)
    public int queID;
}