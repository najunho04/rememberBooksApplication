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

public class RankSectionAdapter extends RecyclerView.Adapter<RankSectionAdapter.RankSectionViewHolder> {

    private List<ReadDoneBook> rankedBooks;

    public RankSectionAdapter(List<ReadDoneBook> rankedBooks) {
        this.rankedBooks = rankedBooks;
    }

    @NonNull
    @Override
    public RankSectionAdapter.RankSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_section_top5, parent, false);
        return new RankSectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankSectionAdapter.RankSectionViewHolder holder, int position) {
        ReadDoneBook book = rankedBooks.get(position);

        holder.tvTitle.setText("TOP5 도서");

        // 내부 가로 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rvContent.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        holder.rvContent.setLayoutManager(layoutManager);
        holder.rvContent.setHasFixedSize(true);

        InnerRankAdapter innerAdapter = new InnerRankAdapter(rankedBooks);
        holder.rvContent.setAdapter(innerAdapter);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class RankSectionViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        RecyclerView rvContent;
        public RankSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_section_title);
            rvContent = itemView.findViewById(R.id.rv_top5_content);
        }
    }
}
