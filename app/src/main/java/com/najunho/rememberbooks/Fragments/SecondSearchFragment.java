package com.najunho.rememberbooks.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.Adapter.ReviewAdapter;
import com.najunho.rememberbooks.DataClass.Review;
import com.najunho.rememberbooks.R;

import java.util.ArrayList;
import java.util.List;

public class SecondSearchFragment  extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_book_review, container, false);

        reviewList = new ArrayList<>();
        Review review1 = new Review("닉네임1", 89, "내 인생책이에요.");
        Review review2 = new Review("닉네임2", 89, "내 인생책이에요.");
        Review review3 = new Review("닉네임3", 89, "내 인생책이에요.");

        reviewList.add(review1);
        reviewList.add(review2);
        reviewList.add(review3);

        recyclerView = view.findViewById(R.id.rv_book_review);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        reviewAdapter = new ReviewAdapter(new ArrayList<>(reviewList));
        recyclerView.setAdapter(reviewAdapter);

        return view;
    }
}
