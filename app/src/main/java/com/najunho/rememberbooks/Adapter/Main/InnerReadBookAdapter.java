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

import okhttp3.internal.Util;

public class InnerReadBookAdapter extends RecyclerView.Adapter<InnerReadBookAdapter.ReadDoneViewHolder>{
    public interface OnClickListener{
        void onItemClick(MyBook book);
    }
    private OnClickListener listener;
    private final List<MyBook> bookList;

    public InnerReadBookAdapter(List<MyBook> bookList, OnClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReadDoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_read_book, parent, false);
        return new ReadDoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadDoneViewHolder holder, int position) {
        MyBook book = bookList.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvScore.setText(book.getScore() + "/100");
        holder.tvComment.setText(book.getComment());

        Glide.with(holder.itemView.getContext())
                .load(book.getCover())
                .placeholder(R.drawable.default_background)
                .into(holder.ivCover);

        holder.itemView.setOnClickListener(v->{
            listener.onItemClick(book);
        });
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }


    public class ReadDoneViewHolder extends RecyclerView.ViewHolder{
        ImageView ivCover;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvScore;
        TextView tvComment;

        public ReadDoneViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCover = itemView.findViewById(R.id.iv_book_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvComment = itemView.findViewById(R.id.tv_comment);

        }
    }
}
