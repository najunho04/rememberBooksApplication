package com.najunho.rememberbooks.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.DataClass.Review;
import com.najunho.rememberbooks.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList){
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_review, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUser, tvScore, tvReview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUser = itemView.findViewById(R.id.tv_user);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvReview = itemView.findViewById(R.id.tv_review);
        }

        public void bind(Review review){
            tvUser.setText((review.getUser() != null) ? review.getUser() : "유저 없음");
            tvScore.setText((review.getScore() != 0) ? String.valueOf(review.getScore()) : "점수 없음");
            tvReview.setText(review.getReview() != null ? review.getReview() : "리뷰 없음");
        }
    }
}
