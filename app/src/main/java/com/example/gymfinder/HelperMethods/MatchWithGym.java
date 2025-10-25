package com.example.gymfinder.HelperMethods;

// In com/example/gymfinder/Database/MatchWithGym.java


import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.gymfinder.Database.Gym;
import com.example.gymfinder.Database.Match;


 //This is a POJO (Plain Old Java Object) to hold the combined

public class MatchWithGym {

    // Room will get the Match object first
    @Embedded
    public Match match;

    // Then Room will use the 'gymId' from the 'match' object
    // to find the matching 'gymCode' in the Gym table.
    @Relation(
            parentColumn = "gymId",
            entityColumn = "gymCode"
    )
    public Gym gym;
}
