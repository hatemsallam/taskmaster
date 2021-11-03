package com.example.taskmaster;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Task.class}, version = 1)
public abstract class TaskDataBase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static TaskDataBase taskDatabase; // declaration for the instance

    public TaskDataBase() { // constructor implementation
    }

    public static synchronized TaskDataBase  getInstance(Context context) { // method returns the instance

        if(taskDatabase == null){
            taskDatabase = Room.databaseBuilder(context,
                    TaskDataBase.class, "TaskDataBase").allowMainThreadQueries().build();
        }

        return taskDatabase;
    }
}
