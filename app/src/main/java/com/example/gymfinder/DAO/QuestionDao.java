package com.example.gymfinder.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gymfinder.Database.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert
    long insertQuestion(Question question);

    @Update
    void updateQuestion(Question question);

    @Query("SELECT * FROM Question")
    List<Question> getAllQuestions();
}