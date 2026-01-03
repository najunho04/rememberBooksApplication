package com.najunho.rememberbooks.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.R;

import java.util.ArrayList;
import java.util.List;

public class RecordDoneAdapter extends RecyclerView.Adapter<RecordDoneAdapter.RecordViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Record record);
    }
    private final OnItemClickListener listener;

    private List<Record> itemList = new ArrayList<>();

    public RecordDoneAdapter(List<Record> list, OnItemClickListener listener) {
        this.itemList = list;
        this.listener = listener;
    }

    // 데이터 설정 메서드
    public void setItemList(List<Record> list) {
        this.itemList = list;
        notifyDataSetChanged(); // 단순 출력용이므로 전체 갱신 사용
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_record_done.xml 레이아웃을 가져옴
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record_done, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record item = itemList.get(position);

        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // 뷰홀더 클래스
    static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvDate, tvQuote;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_item_day);
            tvDate = itemView.findViewById(R.id.tv_item_summary_date);
            tvQuote = itemView.findViewById(R.id.tv_item_summary_quote);
        }

        public void bind(Record item, OnItemClickListener listener) {
            tvDay.setText("Day" + (getLayoutPosition() + 1));
            tvDate.setText(item.getDate());
            tvQuote.setText(item.getQuote());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}