package com.najunho.rememberbooks.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.ReadDoneBook;
import com.najunho.rememberbooks.R;

import java.util.List;

public class RecommendSectionAdapter extends RecyclerView.Adapter<RecommendSectionAdapter.ViewHolder> {

    private List<ReadDoneBook> recommendBooks;

    public RecommendSectionAdapter(List<ReadDoneBook> recommendBooks) {
        this.recommendBooks = recommendBooks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_section_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText("이런 책은 어때요?");

        // 내부 가로 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rvContent.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        holder.rvContent.setLayoutManager(layoutManager);
        holder.rvContent.setHasFixedSize(true);

        InnerRecommendAdapter innerAdapter = new InnerRecommendAdapter(recommendBooks);
        holder.rvContent.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() {
        return 1; // 섹션 전체가 하나의 아이템임
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RecyclerView rvContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_recommend_title);
            rvContent = itemView.findViewById(R.id.rv_recommend_content);
        }
    }
}