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

    //edit gym
    // Methods to get related items for a specific gym
    @Query("SELECT T.* FROM TrainerType T INNER JOIN GymTrainerCrossRef GT ON T.trainerID = GT.trainerID WHERE GT.gymCode = :gymCode")
    List<TrainerType> getTrainersForGym(int gymCode);

    @Query("SELECT E.* FROM Equipment E INNER JOIN GymEquipmentCrossRef GE ON E.equipID = GE.equipID WHERE GE.gymCode = :gymCode")
    List<Equipment> getEquipmentForGym(int gymCode);

    @Query("SELECT GCT.* FROM GymClassType GCT INNER JOIN GymClassCrossRef GC ON GCT.classID = GC.classID WHERE GC.gymCode = :gymCode")
    List<GymClassType> getClassesForGym(int gymCode);

    // Methods to find a specific item by name (to avoid duplicates)
    @Query("SELECT * FROM Equipment WHERE equipName = :name LIMIT 1")
    Equipment findEquipmentByName(String name);

    @Query("SELECT * FROM GymClassType WHERE fitnessClass = :name LIMIT 1")
    GymClassType findClassByName(String name);

    @Query("SELECT * FROM TrainerType WHERE serviceList = :name LIMIT 1")
    TrainerType findTrainerByName(String name);

    // Methods to clear old relationships before saving updated ones
    @Query("DELETE FROM GymEquipmentCrossRef WHERE gymCode = :gymCode")
    void clearEquipmentForGym(int gymCode);

    @Query("DELETE FROM GymClassCrossRef WHERE gymCode = :gymCode")
    void clearClassesForGym(int gymCode);

    @Query("DELETE FROM GymTrainerCrossRef WHERE gymCode = :gymCode")
    void clearTrainersForGym(int gymCode);

    //gym report
    @Query("SELECT GCT.fitnessClass as name, COUNT(GC.classID) as count " +
            "FROM GymClassCrossRef GC " +
            "JOIN GymClassType GCT ON GC.classID = GCT.classID " +
            "WHERE GC.gymCode = :gymCode " +
            "GROUP BY GCT.fitnessClass " +
            "ORDER BY count DESC")
    List<ReportResult> getMostPopularClasses(int gymCode);

    @Query("SELECT E.equipName as name, COUNT(GE.equipID) as count " +
            "FROM GymEquipmentCrossRef GE " +
            "JOIN Equipment E ON GE.equipID = E.equipID " +
            "WHERE GE.gymCode = :gymCode " +
            "GROUP BY E.equipName " +
            "ORDER BY count DESC")
    List<ReportResult> getMostPopularEquipment(int gymCode);

    // --- THIS IS THE NEW METHOD YOU NEED ---
    @Query("SELECT TT.serviceList as name, COUNT(GT.trainerID) as count " +
            "FROM GymTrainerCrossRef GT " +
            "JOIN TrainerType TT ON GT.trainerID = TT.trainerID " +
            "WHERE GT.gymCode = :gymCode " +
            "GROUP BY TT.serviceList " +
            "ORDER BY count DESC")
    List<ReportResult> getMostPopularTrainers(int gymCode);
}
