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

public class InnerReadBookAdapter extends RecyclerView.Adapter<InnerReadBookAdapter.ReadDoneViewHolder>{
    private final List<ReadDoneBook> bookList;


    public InnerReadBookAdapter(List<ReadDoneBook> bookList) {
        this.bookList = bookList;
    }


    @NonNull
    @Override
    public ReadDoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_read_done, parent, false);
        return new ReadDoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadDoneViewHolder holder, int position) {
        ReadDoneBook book = bookList.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvScore.setText("score : " + book.getScore() + "/100");
        holder.tvComment.setText(book.getComment());
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
