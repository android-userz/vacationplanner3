package com.example.vacationplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.Excursion;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private List<Excursion> excursionList;
    private final ExcursionClickListener clickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public ExcursionAdapter(ExcursionClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_excursion, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion excursion = excursionList.get(position);
        holder.textTitle.setText(excursion.getTitle());
        holder.textDate.setText(dateFormat.format(excursion.getDate()));
        holder.itemView.setOnClickListener(v -> clickListener.onExcursionClick(excursion));
    }

    @Override
    public int getItemCount() {
        return (excursionList == null) ? 0 : excursionList.size();
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursionList = excursions;
        notifyDataSetChanged();
    }

    public static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDate;

        public ExcursionViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textExcursionTitle);
            textDate = itemView.findViewById(R.id.textExcursionDate);
        }
    }

    public interface ExcursionClickListener {
        void onExcursionClick(Excursion excursion);
    }
    public interface DeleteClickListener {
        void onDeleteClick(Excursion excursion);
    }
}
