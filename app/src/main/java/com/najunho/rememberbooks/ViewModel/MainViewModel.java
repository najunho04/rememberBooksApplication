package com.najunho.rememberbooks.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.DataClass.BookStats;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.Db.BookStatsRepo;
import com.najunho.rememberbooks.Db.MyBookRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<MyBook>> _myReadDoneBookList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<MyBook>> _myTop5BookList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<BookStats>> _recommendBooks = new MutableLiveData<>(new ArrayList<>());

    public final LiveData<List<MyBook>> myReadDoneBookList = _myReadDoneBookList;
    public final LiveData<List<MyBook>> myTop5BookList = _myTop5BookList;
    public final LiveData<List<BookStats>> recommendBooks = _recommendBooks;


    public LiveData<List<MyBook>> getMyReadDoneBookList() {
        return myReadDoneBookList;
    }
    public LiveData<List<MyBook>> getMyTop5BookList() {
        return myTop5BookList;
    }
    public LiveData<List<BookStats>> getRecommendBookList() {return recommendBooks;}


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public void getMyBookList(){
        MyBookRepo.getMyBookList(mAuth.getCurrentUser().getUid(), new MyBookRepo.OnMyBookListListener() {
            @Override
            public void onSuccess(List<MyBook> myBookList) {
                Log.d("getMyBookList", "result: " + myBookList);

                // 1. 다 읽은 책 리스트 필터링
                List<MyBook> doneBooks = myBookList.stream()
                        .filter(book -> book.getState() == MyBook.STATE_DONE)
                        .collect(Collectors.toList());

                // 2. Top 5 리스트 추출 (점수 내림차순 정렬 후 5개 선택)
                List<MyBook> top5Books = doneBooks.stream()
                        .sorted((b1, b2) -> Integer.compare(b2.getScore(), b1.getScore())) // 내림차순 정렬
                        .limit(5) // 최대 5개
                        .collect(Collectors.toList());

                Log.d("getMyBookList", "doneBook" + doneBooks);
                Log.d("getMyBookList", "top5Book" + top5Books);

                _myReadDoneBookList.setValue(new ArrayList<>(doneBooks));
                _myTop5BookList.setValue(new ArrayList<>(top5Books));
            }

            @Override
            public void onError(Exception e) {
                Log.e("getMyBookList", "error: " + e);
            }
        });
    }

    public void getRecommendBooks(){
        BookStatsRepo.getRecommendBooks(new BookStatsRepo.OnBookStatsListener() {
            @Override
            public void onSuccess(List<BookStats> bookStatsList) {
                _recommendBooks.setValue(bookStatsList);
            }

            @Override
            public void onError(Exception e) {
                Log.e("getRecommendBooks", "error:" + e);
            }
        });
    }
}
