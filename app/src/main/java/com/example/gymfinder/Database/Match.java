package com.example.gymfinder.Database;

// In com/example/gymfinder/Database/Match.java


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "matches",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "userID",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Gym.class,
                        parentColumns = "gymCode",
                        childColumns = "gymId",
                        onDelete = ForeignKey.CASCADE)
        })
public class Match {

    @PrimaryKey(autoGenerate = true)
    public int matchId;

    // This links to User.userID
    @ColumnInfo(index = true)
    public int userId;

    // This links to Gym.gymCode
    @ColumnInfo(index = true)
    public int gymId;

    public double matchScore;
    public double distanceKm;

    // Room requires an empty constructor
    public Match() { }
}
