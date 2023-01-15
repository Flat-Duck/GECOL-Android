package ly.smarthive.gecol.adapter;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import ly.smarthive.gecol.R;
import ly.smarthive.gecol.model.Reading;

public class ReadingsDataAdapter extends RecyclerView.Adapter<ReadingsDataAdapter.MyViewHolder> {
    private final List<Reading> readingsList;
    public SelectedItem selectedItem;
    Context context;
    public ReadingsDataAdapter(List<Reading> ReadingsList,SelectedItem mSelectedItem,Context context) {
        this.readingsList = ReadingsList;
        this.context = context;
        this.selectedItem = mSelectedItem;
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView value, date;
        Button payBtn;

        public MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.reading_date);
            value = view.findViewById(R.id.reading_value);
            payBtn = view.findViewById(R.id.pay_btn);
            payBtn.setOnClickListener(view1 -> selectedItem.selectedItem(readingsList.get(getAdapterPosition()),true));
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
        if (reading.isPaid()) {
            holder.payBtn.setEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                holder.payBtn.getBackground().setColorFilter(new BlendModeColorFilter(Color.GREEN, BlendMode.MULTIPLY));
                holder.payBtn.setText("مدفوعة");
            }
        }
    }

    @Override
    public int getItemCount() {
        return readingsList.size();
    }

    public interface SelectedItem{
        void selectedItem(Reading reading, boolean pay);
    }
}