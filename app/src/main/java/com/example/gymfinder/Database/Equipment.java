package com.example.gymfinder.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(/*... foreignKeys ...*/)
public class Equipment {
    @PrimaryKey(autoGenerate = true)
    public int equipID;

    public String equipName;

    @ColumnInfo(index = true)
    public Integer gymCode;
    public Equipment() { }

    public Equipment(String equipName) {
        this.equipName = equipName;
    }
}
