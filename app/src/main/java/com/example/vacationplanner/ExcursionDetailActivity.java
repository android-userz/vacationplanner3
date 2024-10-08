package com.example.vacationplanner;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.vacationplanner.database.AppDatabase;
import com.example.vacationplanner.database.Excursion;
import com.example.vacationplanner.database.Vacation;
import com.example.vacationplanner.utils.NotificationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetailActivity extends AppCompatActivity {

    private EditText editTitle, editDate;
    private Button btnSave;
    private AppDatabase database;
    private Excursion currentExcursion;
    private int vacationId;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_detail);

        editTitle = findViewById(R.id.editExcursionTitle);
        editDate = findViewById(R.id.editExcursionDate);
        btnSave = findViewById(R.id.btnSaveExcursion);

        database = AppDatabase.getInstance(this);
        notificationHelper = new NotificationHelper(this);

        vacationId = getIntent().getIntExtra("vacation_id", -1);
        int excursionId = getIntent().getIntExtra("excursion_id", -1);
        if (excursionId != -1) {
            loadExcursion(excursionId);
        }

        setupDatePicker();
        setupSaveButton();
    }

    private void loadExcursion(int excursionId) {
        database.excursionDao().getExcursionById(excursionId).observe(this, new Observer<Excursion>() {
            @Override
            public void onChanged(Excursion excursion) {
                if (excursion != null) {
                    currentExcursion = excursion;
                    editTitle.setText(excursion.getTitle());
                    editDate.setText(dateFormat.format(excursion.getDate()));
                }
            }
        });
    }

    private void setupDatePicker() {
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.format(Locale.US, "%02d/%02d/%d", monthOfYear + 1, dayOfMonth, year);
                        editDate.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcursion();
            }
        });
    }

    private void saveExcursion() {
        String title = editTitle.getText().toString().trim();
        String dateStr = editDate.getText().toString().trim();

        if (title.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            final Date date = dateFormat.parse(dateStr);

            database.vacationDao().getVacationById(vacationId).observe(this, new Observer<Vacation>() {
                @Override
                public void onChanged(final Vacation vacation) {
                    if (vacation != null) {
                        if (date.before(vacation.getStartDate()) || date.after(vacation.getEndDate())) {
                            Toast.makeText(ExcursionDetailActivity.this, "Excursion date must be within the vacation dates", Toast.LENGTH_LONG).show();
                            return;
                        }

                        final Excursion excursion = new Excursion();
                        excursion.setTitle(title);
                        excursion.setDate(date);
                        excursion.setVacationId(vacationId);

                        if (currentExcursion != null) {
                            excursion.setId(currentExcursion.getId());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    database.excursionDao().update(excursion);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ExcursionDetailActivity.this, "Excursion updated", Toast.LENGTH_SHORT).show();
                                            notificationHelper.scheduleExcursionNotification(excursion); // Correct method call
                                            finish();
                                        }
                                    });
                                }
                            }).start();
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long id = database.excursionDao().insert(excursion);
                                    excursion.setId((int) id);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ExcursionDetailActivity.this, "Excursion added", Toast.LENGTH_SHORT).show();
                                            notificationHelper.scheduleExcursionNotification(excursion); // Correct method call
                                            finish();
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                }
            });

        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_excursion) {
            deleteExcursion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteExcursion() {
        if (currentExcursion != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Excursion")
                    .setMessage("Are you sure you want to delete this excursion?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    database.excursionDao().delete(currentExcursion);
                                    notificationHelper.cancelNotification(currentExcursion.getId()); // Cancel notification

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ExcursionDetailActivity.this, "Excursion deleted", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

}