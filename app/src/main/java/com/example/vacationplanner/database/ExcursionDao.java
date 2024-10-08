package com.example.vacationplanner.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExcursionDao {

    @Insert
    long insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursion WHERE id = :excursionId")
    LiveData<Excursion> getExcursionById(int excursionId);

    @Query("SELECT * FROM excursion WHERE vacationId = :vacationId")
    LiveData<List<Excursion>> getExcursionsForVacation(int vacationId);
}
