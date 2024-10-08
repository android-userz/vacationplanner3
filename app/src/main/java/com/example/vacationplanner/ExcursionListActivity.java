package com.example.vacationplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationplanner.adapters.ExcursionAdapter;
import com.example.vacationplanner.database.AppDatabase;
import com.example.vacationplanner.database.Excursion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExcursionListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExcursionAdapter adapter;
    private AppDatabase database;
    private int vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_list);

        vacationId = getIntent().getIntExtra("vacation_id", -1);
        if (vacationId == -1) {
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewExcursions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = AppDatabase.getInstance(this);

        adapter = new ExcursionAdapter(new ExcursionAdapter.ExcursionClickListener() {
            @Override
            public void onExcursionClick(Excursion excursion) {
                Intent intent = new Intent(ExcursionListActivity.this, ExcursionDetailActivity.class);
                intent.putExtra("excursion_id", excursion.getId());
                intent.putExtra("vacation_id", vacationId);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        database.excursionDao().getExcursionsForVacation(vacationId).observe(this, new Observer<List<Excursion>>() {
            @Override
            public void onChanged(List<Excursion> excursions) {
                adapter.setExcursions(excursions);
            }
        });

        FloatingActionButton fabAddExcursion = findViewById(R.id.fabAddExcursion);
        fabAddExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExcursionListActivity.this, ExcursionDetailActivity.class);
                intent.putExtra("vacation_id", vacationId);
                startActivity(intent);
            }
        });
    }
}