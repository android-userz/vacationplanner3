package com.example.vacationplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationplanner.adapters.VacationAdapter;
import com.example.vacationplanner.database.AppDatabase;
import com.example.vacationplanner.database.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VacationAdapter adapter;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        recyclerView = findViewById(R.id.recyclerViewVacations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = AppDatabase.getInstance(this);

        adapter = new VacationAdapter(new VacationAdapter.VacationClickListener() {
            @Override
            public void onVacationClick(Vacation vacation) {
                Intent intent = new Intent(VacationListActivity.this, VacationDetailActivity.class);
                intent.putExtra("vacation_id", vacation.getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        database.vacationDao().getAllVacations().observe(this, new Observer<List<Vacation>>() {
            @Override
            public void onChanged(List<Vacation> vacations) {
                adapter.setVacations(vacations);
            }
        });

        FloatingActionButton fabAddVacation = findViewById(R.id.fabAddVacation);
        fabAddVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationListActivity.this, VacationDetailActivity.class);
                startActivity(intent);
            }
        });
    }
}