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

import com.najunho.rememberbooks.DataClass.DiscussionLog;
import com.najunho.rememberbooks.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiscussionAdapter extends ListAdapter<DiscussionLog, DiscussionAdapter.LogViewHolder> {

    public interface OnClickListener{
        void onClick(int position, DiscussionLog log);
    }
    private OnClickListener listener;

    // 1. DiffUtil 설정: 리스트의 변경사항을 계산하는 기준
    private static final DiffUtil.ItemCallback<DiscussionLog> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<DiscussionLog>() {
                @Override
                public boolean areItemsTheSame(@NonNull DiscussionLog oldItem, @NonNull DiscussionLog newItem) {
                    // 시간값이 고유하다면 시간값으로 비교
                    return oldItem.getTimestamp() == newItem.getTimestamp();
                }

                @Override
                public boolean areContentsTheSame(@NonNull DiscussionLog oldItem, @NonNull DiscussionLog newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public DiscussionAdapter(OnClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discussion_log, parent, false);
        Log.d("onCreateViewHolder", "success");
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    // 2. ViewHolder 클래스
    static class LogViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLogTime, tvQuestion;
        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogTime = itemView.findViewById(R.id.tvLogTime);
            tvQuestion = itemView.findViewById(R.id.tv_question);
        }

        public void bind(DiscussionLog log, OnClickListener listener) {
            tvLogTime.setText(log.getQuestion()); // 리스트에는 질문을 제목처럼 표시

            // 타임스탬프 변환 (예: 2026.01.18 23:05)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
            String dateStr = sdf.format(new Date(log.getTimestamp()));
            tvQuestion.setText(dateStr);

            itemView.setOnClickListener(v->{
                listener.onClick(getLayoutPosition(), log);
            });
        }
    }
}