package com.example.gymfinder.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gymfinder.Database.Equipment;
import com.example.gymfinder.Database.GymClassCrossRef;
import com.example.gymfinder.Database.GymClassType;
import com.example.gymfinder.Database.GymEquipmentCrossRef;
import com.example.gymfinder.Database.GymTrainerCrossRef;
import com.example.gymfinder.Database.TrainerType;

import java.util.List;

@Dao
public interface MiscDao {
    @Insert
    long insertTrainerType(TrainerType trainerTypes);

    @Insert
    long insertGymClassType(GymClassType gymClassTypes);

    @Insert
    long insertEquipment(Equipment equipment);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGymEquipmentCrossRef(GymEquipmentCrossRef crossRef);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGymClassCrossRef(GymClassCrossRef crossRef);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGymTrainerCrossRef(GymTrainerCrossRef crossRef);

    @Query("SELECT serviceList FROM TrainerType")
    List<String> getAllTrainers();

    @Query("SELECT fitnessClass FROM GymClassType")
    List<String> getAllClasses();

    @Query("SELECT equipName FROM Equipment")
    List<String> getAllEquipment();
}
