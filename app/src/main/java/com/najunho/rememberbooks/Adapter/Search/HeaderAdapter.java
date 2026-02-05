package com.najunho.rememberbooks.Adapter.Search;

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

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder> {

    private SearchResult searchResult;

    public HeaderAdapter(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_section_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.bind(searchResult);
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

        private void bind(SearchResult searchResult){
            // Optional을 활용한 안전한 텍스트 세팅 (아까 배운 삼항연산자/람다 활용)
            tvTitle.setText(searchResult.title != null ? searchResult.title : "제목 없음");
            tvAuthor.setText(searchResult.author != null ? searchResult.author : "저자 미상");
            tvDescription.setText(searchResult.description != null ? searchResult.description : "책 소개가 없습니다.");
            tvCategory.setText(searchResult.categoryName != null ? searchResult.categoryName : "기타");

            Log.d("book_cover", searchResult.cover);
            Glide.with(itemView)             // Activity나 Fragment의 context
                    .load(searchResult.cover)            // 로드할 이미지 URL
                    .placeholder(R.drawable.book_cover) // 로딩 중에 보여줄 이미지 (선택)
                    //.error(R.drawable.ic_launcher_background)         // 로드 실패 시 보여줄 이미지 (선택)
                    //.fallback(R.drawable.ic_launcher_background)   // URL이 null일 때 보여줄 이미지 (선택)
                    .into(ivCover); // 이미지를 넣을 ImageView
        }
    }
}