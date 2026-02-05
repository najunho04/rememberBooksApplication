package com.najunho.rememberbooks.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.najunho.rememberbooks.DataClass.Review;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.Db.ReviewRepo;

import java.util.ArrayList;
import java.util.List;

public class ReviewViewModel extends ViewModel {
    private static final MutableLiveData<SearchResult> _searchResultLiveData = new MutableLiveData<>();
    private LiveData<SearchResult> searchResultLiveData = _searchResultLiveData;
    private static final MutableLiveData<List<Review>> _reviewList = new MutableLiveData<>();
    private LiveData<List<Review>> reviewList = _reviewList;

    public void setSearchResult(SearchResult result) {
        _searchResultLiveData.setValue(result);
    }

    public void setReviewList(List<Review> reviews) {
        _reviewList.setValue(reviews);
    }


    public LiveData<SearchResult> getSearchResultLiveData() {
        return searchResultLiveData;
    }
    public LiveData<List<Review>> getReviewList(){
        return reviewList;
    }

    public void loadReviews(String isbn13){
        ReviewRepo.getReviewsByIsbn13(isbn13, new ReviewRepo.OnReviewListListener() {
            @Override
            public void onSuccess(List<Review> reviews) {
                _reviewList.setValue(new ArrayList<>(reviews));
                Log.d("loadReviews" , "success: " + reviews);
            }

            @Override
            public void onError(Exception e) {
                Log.e("loadReviews", "error:" + e);
            }
        });
    }


}
