package com.najunho.rememberbooks.Adapter.Main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.R;

import java.util.List;

public class ReadBookSectionAdapter extends RecyclerView.Adapter<ReadBookSectionAdapter.ViewHolder> {
    public InnerReadBookAdapter.OnClickListener listener;

    private List<MyBook> readBooks;
    public ReadBookSectionAdapter(InnerReadBookAdapter.OnClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_section_read_books, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (readBooks.isEmpty()){
            holder.emptyBook.setVisibility(View.VISIBLE);
        }else {
            holder.emptyBook.setVisibility(View.GONE);
        }
        holder.tvTitle.setText("읽은 책");

        int spanCount = (readBooks.size() == 1) ? 1 : 2;

        // 내부 리사이클러뷰 설정
        GridLayoutManager layoutManager = new GridLayoutManager(
                holder.rvContent.getContext(),
                spanCount,
                GridLayoutManager.HORIZONTAL,
                false
        );
        holder.rvContent.setLayoutManager(layoutManager);
        holder.rvContent.setHasFixedSize(false);

        // 내부 어댑터 연결
        InnerReadBookAdapter innerAdapter = new InnerReadBookAdapter(readBooks, book -> {
            listener.onItemClick(book);
        });
        holder.rvContent.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() {
        return 1; // 섹션 제목 + 리스트 통째로 1개의 아이템
    }

    // 데이터를 받아서 내부 어댑터로 전달하는 통로
    public void setTop5Data(List<MyBook> books) {
        readBooks = books;
        notifyDataSetChanged(); // 또는 특정 포지션 업데이트
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, emptyBook;
        RecyclerView rvContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_read_books_title);
            rvContent = itemView.findViewById(R.id.rv_read_books_content);
            emptyBook = itemView.findViewById(R.id.empty_book);

        }
    }
}