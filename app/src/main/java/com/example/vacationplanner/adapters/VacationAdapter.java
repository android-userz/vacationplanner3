package com.example.vacationplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.Vacation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private List<Vacation> vacationList;
    private final VacationClickListener clickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public VacationAdapter(VacationClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vacation, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacationList.get(position);
        holder.textTitle.setText(vacation.getTitle());
        holder.textHotel.setText(vacation.getHotel());
        holder.textStartDate.setText(dateFormat.format(vacation.getStartDate()));
        holder.textEndDate.setText(dateFormat.format(vacation.getEndDate()));
        holder.itemView.setOnClickListener(v -> clickListener.onVacationClick(vacation));
    }

    @Override
    public int getItemCount() {
        return (vacationList == null) ? 0 : vacationList.size();
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacationList = vacations;
        notifyDataSetChanged();
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textHotel, textStartDate, textEndDate;

        public VacationViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textVacationTitle);
            textHotel = itemView.findViewById(R.id.textVacationHotel);
            textStartDate = itemView.findViewById(R.id.textVacationStartDate);
            textEndDate = itemView.findViewById(R.id.textVacationEndDate);
        }
    }

    public interface VacationClickListener {
        void onVacationClick(Vacation vacation);
    }
}
