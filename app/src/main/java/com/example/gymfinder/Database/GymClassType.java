package com.example.gymfinder.Database;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity
// (foreignKeys = @ForeignKey(entity = Gym.class,
        //parentColumns = "gymCode",
     //   childColumns = "gymCode",
      //  onDelete = ForeignKey.CASCADE))
public class GymClassType {
    @PrimaryKey(autoGenerate = true)
    public int classID;

    public String fitnessClass;

   // @ColumnInfo(index = true)
   // public int gymCode;
    public GymClassType() { } // Empty constructor for Room

    public GymClassType(String fitnessClass) {
        this.fitnessClass = fitnessClass;
    }
}
