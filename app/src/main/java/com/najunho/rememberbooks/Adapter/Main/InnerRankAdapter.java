package com.najunho.rememberbooks.Adapter.Main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.R;

import java.util.List;

public class InnerRankAdapter extends RecyclerView.Adapter<InnerRankAdapter.Top5SectionViewHolder>{
    public interface OnClickListener{
        void onItemClick(MyBook book);
    }
    private OnClickListener listener;

    private final List<MyBook> bookList;
    public InnerRankAdapter(List<MyBook> bookList, OnClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InnerRankAdapter.Top5SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_top5_book, parent, false);
        return new InnerRankAdapter.Top5SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerRankAdapter.Top5SectionViewHolder holder, int position) {
        MyBook book = bookList.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.score.setText(book.getScore() +  "/100");

        Glide.with(holder.itemView.getContext())
                .load(book.getCover())
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

    public class Top5SectionViewHolder extends RecyclerView.ViewHolder{
        private TextView title, author, score;
        private ImageView bookCover;
        public Top5SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.iv_book_cover);
            title = itemView.findViewById(R.id.tv_title);
            author = itemView.findViewById(R.id.tv_author);
            score = itemView.findViewById(R.id.tv_score);
        }
    }
}
