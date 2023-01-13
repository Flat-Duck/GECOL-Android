package ly.smarthive.gecol.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ly.smarthive.gecol.R;
import ly.smarthive.gecol.model.Notice;

public class NoticesDataAdapter extends RecyclerView.Adapter<NoticesDataAdapter.MyViewHolder> {
    private final List<Notice> noticesList;

    public NoticesDataAdapter(List<Notice> NoticesList) {
        this.noticesList = NoticesList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date;

        public MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.notice_date);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notice notice = noticesList.get(position);
        holder.date.setText(notice.getDate());
    }

    @Override
    public int getItemCount() {
        return noticesList.size();
    }

}