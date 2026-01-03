package com.najunho.rememberbooks.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.Book;
import com.najunho.rememberbooks.R;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder> {

    private Book bookData;

    public HeaderAdapter(Book bookData) {
        this.bookData = bookData;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        if (bookData != null) {
            // Optional을 활용한 안전한 텍스트 세팅 (아까 배운 삼항연산자/람다 활용)
            holder.tvTitle.setText(bookData.getTitle() != null ? bookData.getTitle() : "제목 없음");
            holder.tvAuthor.setText(bookData.getAuthor() != null ? bookData.getAuthor() : "저자 미상");
            holder.tvDescription.setText(bookData.getDescription() != null ? bookData.getDescription() : "책 소개가 없습니다.");
            holder.tvCategory.setText(bookData.getCategory() != null ? bookData.getCategory() : "기타");

            // 이미지 로딩 로직 (예: Glide나 기본 리소스)
            // holder.ivCover.setImageResource(R.drawable.demian_book);
        }
    }

    @Override
    public int getItemCount() {
        return 1; // 헤더는 항상 1개
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvDescription, tvCategory;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_book_cover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}