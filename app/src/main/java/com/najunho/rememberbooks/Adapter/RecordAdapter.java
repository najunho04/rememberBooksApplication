package com.najunho.rememberbooks.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.R;

import java.util.List;
import java.util.Objects;

public class RecordAdapter extends ListAdapter<Record, RecordAdapter.RecordViewHolder> {

    // 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(Record record);
        void onEditClick(Record record, int position);
        void onDeleteClick(Record record, int position);
    }

    private final OnItemClickListener listener;

    // DiffUtil 설정: 리스트의 변경 사항을 계산하여 필요한 부분만 갱신
    public RecordAdapter(OnItemClickListener listener) {
        super(new DiffUtil.ItemCallback<Record>() {
            @Override
            public boolean areItemsTheSame(@NonNull Record oldItem, @NonNull Record newItem) {
                // 고유 ID 비교
                Log.d("areItemsTheSame", "boolean: "+ Objects.equals(oldItem.getId(), newItem.getId()));
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Record oldItem, @NonNull Record newItem) {
                // 전체 내용 비교 (equals 구현 필요 혹은 필드 직접 비교)
                Log.d("areContentsTheSame", "boolean1: "+ Objects.equals(oldItem.getDay(), newItem.getDay()));
                Log.d("areContentsTheSame", "boolean2: "+ Objects.equals(oldItem.getDate(), newItem.getDate()) );
                Log.d("areContentsTheSame", "boolean3: "+ Objects.equals(oldItem.getQuote(), newItem.getQuote()));
                return Objects.equals(oldItem.getDay(), newItem.getDay()) &&
                        Objects.equals(oldItem.getDate(), newItem.getDate()) &&
                        Objects.equals(oldItem.getEndPage(), newItem.getEndPage()) &&
                        Objects.equals(oldItem.getQuote(), newItem.getQuote());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);
        Log.d("onCreateViewHolder", "success");
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Log.d("onBindViewHolder", "success");
        holder.bind(getItem(position), listener);
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDay, tvDate, tvQuote, tvEdit, tvDelete;


        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_item_day);
            tvDate = itemView.findViewById(R.id.tv_item_summary_date);
            tvQuote = itemView.findViewById(R.id.tv_item_summary_quote);
            tvEdit = itemView.findViewById(R.id.tv_item_edit);
            tvDelete = itemView.findViewById(R.id.tv_item_delete);
        }

        public void bind(Record record, OnItemClickListener listener) {
            tvDay.setText("Day" + (getLayoutPosition() + 1)); //record.getDay() + 1 해도 됨.
            tvDate.setText(record.getDate());
            tvQuote.setText(record.getQuote());

            // 아이템 전체 클릭 시 리스너 호출
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(record);
                }
            });

            // 수정 버튼 클릭 시에도 동일하게 작동하거나 별도 로직 가능
            tvEdit.setOnClickListener(v -> {
                //수정 프래그먼트로 이동
                if (listener != null) {
                    listener.onEditClick(record, getLayoutPosition());
                }
            });

            tvDelete.setOnClickListener(v->{
                //삭제 로직
                if (listener != null) {
                    listener.onDeleteClick(record, getLayoutPosition());
                }
            });
        }
    }
}