package ly.smarthive.gecol.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import ly.smarthive.gecol.R;
import ly.smarthive.gecol.model.Reading;

public class ReadingsDataAdapter extends RecyclerView.Adapter<ReadingsDataAdapter.MyViewHolder> {
    private final List<Reading> readingsList;

    public ReadingsDataAdapter(List<Reading> ReadingsList) {
        this.readingsList = ReadingsList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView value, date;

        public MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.reading_date);
            value = view.findViewById(R.id.reading_value);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reading, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Reading reading = readingsList.get(position);
        holder.value.setText(reading.getValue());
        holder.date.setText(reading.getDate());
    }

    @Override
    public int getItemCount() {
        return readingsList.size();
    }

}