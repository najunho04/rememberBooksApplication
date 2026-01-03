package com.najunho.rememberbooks.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.MyLibraryBook;
import com.najunho.rememberbooks.R;

import java.util.List;
import java.util.Objects;

public class MyLibraryAdapter extends RecyclerView.Adapter<MyLibraryAdapter.MyLibraryViewHolder>{
    private final List<MyLibraryBook> bookList;

    public MyLibraryAdapter(List<MyLibraryBook> bookList) {
        this.bookList = bookList;
    }
    @NonNull
    @Override
    public MyLibraryAdapter.MyLibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_library_book, parent, false);
        return new MyLibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyLibraryAdapter.MyLibraryViewHolder holder, int position) {
        MyLibraryBook book = bookList.get(position);

        if(Objects.equals(book.getState(), "독서 완료")){
            holder.bindWhenDoneRead(book);
            Log.d("onBindViewHolder", "독서 완료 item으로 bind 성공");
        }
        if (Objects.equals(book.getState(), "독서 중")) {
            holder.bindWhenReading(book);
            Log.d("onBindViewHolder", "독서 중 item으로 bind 성공");
        }
        if (Objects.equals(book.getState(), "보관 중")) {
            holder.bindWhenSaved(book);
            Log.d("onBindViewHolder", "보관 중 item으로 bind 성공");
        }
        else {
            Log.d("onBindViewHolder", "failed to bind view holder");
        }
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    public class MyLibraryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvMetaPrimary; //점수 or 날짜
        TextView tvMetaSecondary; //한줄평 or 남은 독서량
        TextView tvReadState; //독서 완료 or 독서 중 or 보관 중

        public MyLibraryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvMetaPrimary = itemView.findViewById(R.id.tv_meta_primary);
            tvMetaSecondary = itemView.findViewById(R.id.tv_meta_secondary);
            tvReadState = itemView.findViewById(R.id.tv_read_state);
        }

        private void bindWhenDoneRead(MyLibraryBook book){
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvMetaPrimary.setText(book.getScore());
            tvMetaSecondary.setText(book.getComment());
            tvReadState.setText("독서 완료");
        }

        private void bindWhenReading(MyLibraryBook book) {
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvMetaPrimary.setText(book.getScore());
            tvMetaSecondary.setText(book.getComment());
            tvReadState.setText("독서 중");
        }

        private void bindWhenSaved(MyLibraryBook book) {
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvMetaPrimary.setText(book.getScore());
            tvMetaSecondary.setText(book.getComment());
            tvReadState.setText("보관 중");
        }
    }
}
