package com.najunho.rememberbooks.Adapter.Main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.najunho.rememberbooks.DataClass.BookStats;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.R;

import java.util.List;

public class InnerRecommendAdapter extends RecyclerView.Adapter<InnerRecommendAdapter.RecommendSectionViewHolder>{
    private final List<BookStats> bookList;
    public interface OnClickListener{
        void onItemClick(BookStats book);
    }
    private OnClickListener listener;

    public InnerRecommendAdapter(List<BookStats> bookList, OnClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InnerRecommendAdapter.RecommendSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_top5_book, parent, false);
        return new InnerRecommendAdapter.RecommendSectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerRecommendAdapter.RecommendSectionViewHolder holder, int position) {
        BookStats book = bookList.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.score.setText(book.getAverageScore() + "/100");
        Glide.with(holder.itemView)
                .load(book.getCover())
                .centerCrop()
                .placeholder(R.drawable.default_background)
                .into(holder.bookCover);

        holder.itemView.setOnClickListener(v->{
            listener.onItemClick(book);
        });
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
