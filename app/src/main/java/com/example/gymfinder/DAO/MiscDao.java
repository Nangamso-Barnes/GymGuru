package com.example.gymfinder.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.gymfinder.Database.Equipment;
import com.example.gymfinder.Database.GymClassType;
import com.example.gymfinder.Database.TrainerType;

import java.util.List;

@Dao
public interface MiscDao {
    @Insert
    void insertTrainerType(TrainerType... trainerTypes);

    @Insert
    void insertGymClassType(GymClassType... gymClassTypes);

    @Insert
    void insertEquipment(Equipment... equipment);

    @Query("SELECT serviceList FROM TrainerType")
    List<String> getAllTrainers();

    @Query("SELECT fitnessClass FROM GymClassType")
    List<String> getAllClasses();

    @Query("SELECT equipName FROM Equipment")
    List<String> getAllEquipment();
}
