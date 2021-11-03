package com.example.taskmaster;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE id = :id")
    Task getStudentById(Long id);

    @Insert
    Long insertTask(Task task); // void: is not gonna return anytihng where the long return the id of the inserted student


}
