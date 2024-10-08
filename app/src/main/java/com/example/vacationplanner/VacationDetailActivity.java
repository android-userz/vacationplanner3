package com.example.vacationplanner;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.example.vacationplanner.utils.NetworkUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.vacationplanner.database.AppDatabase;
import com.example.vacationplanner.database.Vacation;
import com.example.vacationplanner.utils.NotificationHelper;
import com.example.vacationplanner.utils.NetworkUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VacationDetailActivity extends AppCompatActivity {

    private EditText editTitle, editHotel, editStartDate, editEndDate;
    private Button btnSave, btnViewExcursions, btnShare;
    private AppDatabase database;
    private Vacation currentVacation;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private NotificationHelper notificationHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_detail);

        initUI();

        database = AppDatabase.getInstance(this);
        notificationHelper = new NotificationHelper(this);

        int vacationId = getIntent().getIntExtra("vacation_id", -1);
        if (vacationId != -1) {
            loadVacation(vacationId);
        }

        setupDatePickers();
        setupSaveButton();
        setupViewExcursionsButton();
        setupShareButton();
    }

    private void initUI() {
        editTitle = findViewById(R.id.editTitle);
        editHotel = findViewById(R.id.editHotel);
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        btnSave = findViewById(R.id.btnSave);
        btnViewExcursions = findViewById(R.id.btnViewExcursions);
        btnShare = findViewById(R.id.btnShare);
    }

    private void loadVacation(int vacationId) {
        database.vacationDao().getVacationById(vacationId).observe(this, vacation -> {
            if (vacation != null) {
                currentVacation = vacation;
                populateVacationDetails(vacation);
            }
        });
    }

    private void populateVacationDetails(Vacation vacation) {
        editTitle.setText(vacation.getTitle());
        editHotel.setText(vacation.getHotel());
        editStartDate.setText(dateFormat.format(vacation.getStartDate()));
        editEndDate.setText(dateFormat.format(vacation.getEndDate()));
    }

    private void setupDatePickers() {
        editStartDate.setOnClickListener(v -> showDatePickerDialog(editStartDate));
        editEndDate.setOnClickListener(v -> showDatePickerDialog(editEndDate));
    }

    private void showDatePickerDialog(final EditText dateField) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format(Locale.US, "%02d/%02d/%d", monthOfYear + 1, dayOfMonth, year1);
                    dateField.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            btnSave.setEnabled(false);  // Disable button to prevent multiple clicks
            saveVacation();
        });
    }

    private void saveVacation() {
        String title = editTitle.getText().toString().trim();
        String hotel = editHotel.getText().toString().trim();
        String startDateStr = editStartDate.getText().toString().trim();
        String endDateStr = editEndDate.getText().toString().trim();

        if (validateFields(title, hotel, startDateStr, endDateStr)) {
            try {
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = dateFormat.parse(endDateStr);

                if (endDate.before(startDate)) {
                    Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                    return;
                }

                saveOrUpdateVacation(title, hotel, startDate, endDate);

            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
            }
        }
    }

    private boolean validateFields(String title, String hotel, String startDate, String endDate) {
        if (title.isEmpty() || hotel.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(true);
            return false;
        }
        return true;
    }

    private void saveOrUpdateVacation(String title, String hotel, Date startDate, Date endDate) {
        Vacation vacation = new Vacation();
        vacation.setTitle(title);
        vacation.setHotel(hotel);
        vacation.setStartDate(startDate);
        vacation.setEndDate(endDate);

        if (currentVacation != null) {
            vacation.setId(currentVacation.getId());
            new SaveVacationTask(vacation, true).execute();
        } else {
            new SaveVacationTask(vacation, false).execute();
        }
    }

    private void setupViewExcursionsButton() {
        btnViewExcursions.setOnClickListener(v -> {
            if (currentVacation != null) {
                Intent intent = new Intent(VacationDetailActivity.this, ExcursionListActivity.class);
                intent.putExtra("vacation_id", currentVacation.getId());
                startActivity(intent);
            } else {
                Toast.makeText(VacationDetailActivity.this, "Please save the vacation first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupShareButton() {
        btnShare.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                shareVacationDetails();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareVacationDetails() {
        if (currentVacation != null) {
            String shareText = "Vacation: " + currentVacation.getTitle() + "\n" +
                    "Hotel: " + currentVacation.getHotel() + "\n" +
                    "Start Date: " + dateFormat.format(currentVacation.getStartDate()) + "\n" +
                    "End Date: " + dateFormat.format(currentVacation.getEndDate());

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share Vacation Details"));
        } else {
            Toast.makeText(this, "No vacation details to share", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_vacation) {
            deleteVacation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteVacation() {
        if (currentVacation != null) {
            new DeleteVacationTask().execute(currentVacation);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this vacation?")
                .setPositiveButton("Yes", (dialog, id) -> performDelete())
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void performDelete() {
        new Thread(() -> {
            database.vacationDao().delete(currentVacation);
            runOnUiThread(() -> {
                Toast.makeText(VacationDetailActivity.this, "Vacation deleted", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private class SaveVacationTask extends AsyncTask<Void, Void, Void> {
        private Vacation vacation;
        private boolean isUpdate;

        SaveVacationTask(Vacation vacation, boolean isUpdate) {
            this.vacation = vacation;
            this.isUpdate = isUpdate;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isUpdate) {
                database.vacationDao().update(vacation);
            } else {
                long id = database.vacationDao().insert(vacation);
                vacation.setId((int) id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String message = isUpdate ? "Vacation updated" : "Vacation added";
            Toast.makeText(VacationDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            notificationHelper.scheduleVacationStartNotification(vacation);
            notificationHelper.scheduleVacationEndNotification(vacation);
            finish();
        }
    }

    private class DeleteVacationTask extends AsyncTask<Vacation, Void, Integer> {
        @Override
        protected Integer doInBackground(Vacation... vacations) {
            return database.vacationDao().getExcursionCountForVacation(vacations[0].getId());
        }

        @Override
        protected void onPostExecute(Integer excursionCount) {
            if (excursionCount > 0) {
                Toast.makeText(VacationDetailActivity.this, "Cannot delete vacation with associated excursions", Toast.LENGTH_LONG).show();
            } else {
                showDeleteConfirmationDialog();
            }
        }
    }
}
