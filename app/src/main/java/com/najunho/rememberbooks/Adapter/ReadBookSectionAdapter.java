package com.najunho.rememberbooks.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.ReadDoneBook;
import com.najunho.rememberbooks.R;

import java.util.List;

public class ReadBookSectionAdapter extends RecyclerView.Adapter<ReadBookSectionAdapter.ViewHolder> {

    private List<ReadDoneBook> readBooks;

    public ReadBookSectionAdapter(List<ReadDoneBook> readBooks) {
        this.readBooks = readBooks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_section_read_books, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText("읽은 책");

        // 내부 리사이클러뷰 설정
        GridLayoutManager layoutManager = new GridLayoutManager(
                holder.rvContent.getContext(),
                2,
                GridLayoutManager.HORIZONTAL,
                false
        );
        holder.rvContent.setLayoutManager(layoutManager);
        holder.rvContent.setHasFixedSize(true);

        // 내부 어댑터 연결
        InnerReadBookAdapter innerAdapter = new InnerReadBookAdapter(readBooks);
        holder.rvContent.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() {
        return 1; // 섹션 제목 + 리스트 통째로 1개의 아이템
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RecyclerView rvContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_read_books_title);
            rvContent = itemView.findViewById(R.id.rv_read_books_content);
        }
    }
}