package com.example.vacationplanner.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VacationDao {

    @Insert
    long insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM vacation WHERE id = :vacationId")
    LiveData<Vacation> getVacationById(int vacationId);

    @Query("SELECT * FROM vacation")
    LiveData<List<Vacation>> getAllVacations();

    @Query("SELECT COUNT(*) FROM excursion WHERE vacationId = :vacationId")
    int getExcursionCountForVacation(int vacationId);



}
