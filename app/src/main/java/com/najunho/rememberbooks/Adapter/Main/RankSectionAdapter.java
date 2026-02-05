package com.najunho.rememberbooks.Adapter.Main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.R;

import java.util.List;

public class RankSectionAdapter extends RecyclerView.Adapter<RankSectionAdapter.RankSectionViewHolder> {

    private List<MyBook> rankedBooks;
    public InnerRankAdapter.OnClickListener listener;

    public RankSectionAdapter(InnerRankAdapter.OnClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public RankSectionAdapter.RankSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_section_top5, parent, false);
        return new RankSectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankSectionAdapter.RankSectionViewHolder holder, int position) {

        if (rankedBooks.isEmpty()){
            holder.emptyBook.setVisibility(View.VISIBLE);
        }else {
            holder.emptyBook.setVisibility(View.GONE);
        }
        holder.tvTitle.setText("TOP5 도서");

        // 내부 가로 리사이클러뷰 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rvContent.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        holder.rvContent.setLayoutManager(layoutManager);
        holder.rvContent.setHasFixedSize(false);

        InnerRankAdapter innerAdapter = new InnerRankAdapter(rankedBooks, book -> {
            listener.onItemClick(book);
        });
        holder.rvContent.setAdapter(innerAdapter);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void setTop5Data(List<MyBook> books) {
        rankedBooks = books;
        notifyDataSetChanged(); // 또는 특정 포지션 업데이트
    }

    public static class RankSectionViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, emptyBook;
        RecyclerView rvContent;
        public RankSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_section_title);
            rvContent = itemView.findViewById(R.id.rv_top5_content);
            emptyBook = itemView.findViewById(R.id.empty_book);
        }
    }
}
