package com.najunho.rememberbooks.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.Adapter.ReviewAdapter;
import com.najunho.rememberbooks.DataClass.Review;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.ViewModel.ReviewViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchBookReviewFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private ReviewViewModel reviewViewModel;
    private static final String ARG_ISBN13 = "isbn13";
    private String isbn13;

    public static SearchBookReviewFragment newInstance(String isbn13) {
        SearchBookReviewFragment fragment = new SearchBookReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN13, isbn13);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_search_book_review, container, false);

        if (getArguments()!=null){
            isbn13 = getArguments().getString(ARG_ISBN13);
        }
        Log.d("isbn13" , "isbn13: " + isbn13);

        recyclerView = view.findViewById(R.id.rv_book_review);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        reviewAdapter = new ReviewAdapter(new ArrayList<>(reviewList));
        recyclerView.setAdapter(reviewAdapter);

        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        reviewViewModel.getReviewList().observe(requireActivity(), reviews -> {
            if(reviews == null) {return;}
            Log.d("reviewList", "reviewList:" + reviews);
            //리뷰뷰 클릭 이벤트는 ㄱㅊ, 슬라이드 시에 null issue
            reviewAdapter.setReviewList(reviews);
        });
        reviewViewModel.loadReviews(isbn13);


        return view;
    }
}
