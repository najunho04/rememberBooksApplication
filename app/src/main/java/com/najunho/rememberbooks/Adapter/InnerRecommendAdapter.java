package com.najunho.rememberbooks.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.ReadDoneBook;
import com.najunho.rememberbooks.R;

import java.util.List;

public class InnerRecommendAdapter extends RecyclerView.Adapter<InnerRecommendAdapter.RecommendSectionViewHolder>{
    private final List<ReadDoneBook> bookList;

    public InnerRecommendAdapter(List<ReadDoneBook> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public InnerRecommendAdapter.RecommendSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rank_horizontal_books, parent, false);
        return new InnerRecommendAdapter.RecommendSectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerRecommendAdapter.RecommendSectionViewHolder holder, int position) {
        ReadDoneBook book = bookList.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.score.setText(book.getScore());
        holder.bookCover.setImageResource(R.drawable.demian_book);
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    public class RecommendSectionViewHolder extends RecyclerView.ViewHolder {
        private TextView title, author, score;
        private ImageView bookCover;

        public RecommendSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.iv_book_cover);
            title = itemView.findViewById(R.id.tv_title);
            author = itemView.findViewById(R.id.tv_author);
            score = itemView.findViewById(R.id.tv_score);
        }
    }
}
