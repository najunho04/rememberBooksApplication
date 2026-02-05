package com.najunho.rememberbooks.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.R;

import java.util.List;

public class SearchBookListAdapter extends RecyclerView.Adapter<SearchBookListAdapter.SearchBookListViewHolder>{

    private List<SearchResult> searchBookList;
    public interface OnClickListener{
        void onClick(int position);
    }
    private OnClickListener listener;

    public SearchBookListAdapter(List<SearchResult> searchBookList, OnClickListener listener) {
        this.searchBookList = searchBookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchBookListAdapter.SearchBookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_book_list, parent, false);
        Log.d("onCreateViewHolder", "success");
        return new SearchBookListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBookListAdapter.SearchBookListViewHolder holder, int position) {
        SearchResult book = searchBookList.get(position);
        holder.bind(book, listener);
        Log.d("onBindViewHolder", "success");
    }

    @Override
    public int getItemCount() {
        return (!searchBookList.isEmpty()) ? searchBookList.size() : 0;
    }

    public void setSearchBookList(List<SearchResult> searchResult){
        searchBookList = searchResult;
        notifyDataSetChanged();
        Log.d("setSearchBookList", "success");
    }

    public static class SearchBookListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvAuthor, tvReview;
        private ImageView ivBookCover;

        public SearchBookListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvReview = itemView.findViewById(R.id.tv_review);
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
        }

        public void bind(SearchResult book, OnClickListener listener) {
            tvTitle.setText(book.title);
            tvAuthor.setText(book.author);
            tvReview.setText(String.valueOf(book.customerReviewRank));
            ivBookCover.setImageResource(R.drawable.demian_book); //추후 glide 처리
            Glide.with(itemView)             // Activity나 Fragment의 context
                    .load(book.cover)            // 로드할 이미지 URL
                    .placeholder(R.drawable.ic_launcher_background) // 로딩 중에 보여줄 이미지 (선택)
                    .error(R.drawable.ic_launcher_background)         // 로드 실패 시 보여줄 이미지 (선택)
                    .fallback(R.drawable.ic_launcher_background)   // URL이 null일 때 보여줄 이미지 (선택)
                    .into(ivBookCover); // 이미지를 넣을 ImageView

            itemView.setOnClickListener(v->{
                Log.d("onClick", "success");
                listener.onClick(getLayoutPosition());
            });
        }
    }
}
