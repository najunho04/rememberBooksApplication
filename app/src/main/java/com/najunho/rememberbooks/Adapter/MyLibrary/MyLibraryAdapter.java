package com.najunho.rememberbooks.Adapter.MyLibrary;

import android.util.Log;
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

public class MyLibraryAdapter extends RecyclerView.Adapter<MyLibraryAdapter.MyLibraryViewHolder>{
    private final List<MyBook> bookList;
    public interface OnClickListener {
        void onClickReading(MyBook book);
        void onClickDoneRead(MyBook book);
        void onClickSaved(MyBook book);
    }
    private OnClickListener listener;

    public MyLibraryAdapter(List<MyBook> bookList, OnClickListener listener) {

        this.bookList = bookList;
        this.listener = listener;
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
        MyBook book = bookList.get(position);

        if((book.getState()==MyBook.STATE_DONE)){
            holder.bindWhenDoneRead(book, listener);
            Log.d("onBindViewHolder", "독서 완료 item으로 bind 성공");
        }
        if (book.getState() == MyBook.STATE_READING) {
            holder.bindWhenReading(book, listener);
            Log.d("onBindViewHolder", "독서 중 item으로 bind 성공");
        }
        if (book.getState() == MyBook.STATE_SAVED) {
            holder.bindWhenSaved(book, listener);
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

        private void bindWhenDoneRead(MyBook book, OnClickListener listener){
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvMetaPrimary.setText(book.getScore() + "/100");
            tvMetaSecondary.setText(book.getComment());
            tvReadState.setText("독서 완료");
            Glide.with(itemView.getContext())
                    .load(book.getCover())
                    .placeholder(R.drawable.book_cover)
                    .into(ivBookCover);

            itemView.setOnClickListener(v->{
                if(listener != null){
                    listener.onClickDoneRead(book);
                }
            });
        }

        private void bindWhenReading(MyBook book, OnClickListener listener) {
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvMetaPrimary.setText(book.getDateOfRead());
            String formattedRead = String.format("%d / %dp", book.getReadPage(), book.getPage());
            tvMetaSecondary.setText(formattedRead);
            tvReadState.setText("독서 중");
            Glide.with(itemView.getContext())
                    .load(book.getCover())
                    .placeholder(R.drawable.book_cover)
                    .into(ivBookCover);

            itemView.setOnClickListener(v-> {
                if (listener != null) {
                    listener.onClickReading(book);
                }
            });
        }

        private void bindWhenSaved(MyBook book, OnClickListener listener){
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvMetaPrimary.setText(book.getDateOfRead());
            tvMetaPrimary.setVisibility(View.GONE);
            tvMetaSecondary.setVisibility(View.GONE); //ui 때문에
            tvMetaSecondary.setText("");
            tvReadState.setText("보관 중");
            Glide.with(itemView.getContext())
                    .load(book.getCover())
                    .placeholder(R.drawable.book_cover)
                    .into(ivBookCover);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClickSaved(book);
                }
            });
        }
    }
}
