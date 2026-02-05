package com.najunho.rememberbooks.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.DataClass.AladinResponse;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Retrofit.AladinApiService;
import com.najunho.rememberbooks.Retrofit.RetrofitClient;
import com.najunho.rememberbooks.Util.BookCheckResult;
import com.najunho.rememberbooks.Util.Event;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.najunho.rememberbooks.BuildConfig;

public class SearchBookListViewModel extends ViewModel {
    private static final String TTB_KEY = BuildConfig.ALADDIN_KEY;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<List<SearchResult>> _result = new MutableLiveData<>();
    public LiveData<List<SearchResult>> result = _result;

    // String 데이터를 담은 이벤트를 발행함
    private MutableLiveData<Event<BookCheckResult>> _navigateToDetail = new MutableLiveData<>();
    public LiveData<Event<BookCheckResult>> navigateToDetail = _navigateToDetail;


    public LiveData<List<SearchResult>> getResult(){return _result;}
    public LiveData<Event<BookCheckResult>> getNavigateToDetail(){return _navigateToDetail;}
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void searchBookList(String searchQuery){
        if (Boolean.TRUE.equals(isLoading.getValue())) return; // 이미 로딩 중이면 차단
        isLoading.setValue(true);

        // 1. 분리된 클라이언트를 통해 서비스 생성
        AladinApiService apiService = RetrofitClient.createService(AladinApiService.class);

        // 2. API 호출
        Call<AladinResponse> call = apiService.searchBook(
                TTB_KEY,
                searchQuery,
                "Title",
                10,
                1,
                "Book",
                "js", // JSON 출력
                "20131101"
        );

        // 3. 비동기 실행
        call.enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isLoading.setValue(false);
                     _result.setValue(response.body().item);
                    Log.d("AladinResponse","success");
                }
            }
            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.d("AladinResponse","에러 발생: " + t.getMessage());
            }
        });
    }

    public void existBook(String isbn13){
        if (Boolean.TRUE.equals(isLoading.getValue())) return; // 이미 로딩 중이면 차단
        isLoading.setValue(true);

        MyBookRepo.checkIsbn13Exists(mAuth.getCurrentUser().getUid(), isbn13, new MyBookRepo.OnCheckListener() {
            @Override
            public void onResult(boolean exists) {
                isLoading.setValue(false);
                _navigateToDetail.setValue(new Event<>(new BookCheckResult(isbn13, exists)));
                Log.d("checkIsbn13Exists", "success");
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                Log.e("checkIsbn13Exists", "e: " + e);
            }
        });
    }
}
