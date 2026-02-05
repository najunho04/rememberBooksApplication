package com.najunho.rememberbooks.ViewModel;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.najunho.rememberbooks.Activity.ReadingRecordActivity;
import com.najunho.rememberbooks.Activity.SearchBookActivity;
import com.najunho.rememberbooks.BuildConfig;
import com.najunho.rememberbooks.DataClass.AladinResponse;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Db.UserRepo;
import com.najunho.rememberbooks.Retrofit.AladinApiService;
import com.najunho.rememberbooks.Retrofit.RetrofitClient;
import com.najunho.rememberbooks.Util.Event;
import com.najunho.rememberbooks.Util.SaveBookCheck;

import org.commonmark.internal.util.LinkScanner;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBookViewModel extends ViewModel {
    private static final String TTB_KEY = BuildConfig.ALADDIN_KEY;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<SearchResult> _result = new MutableLiveData<>();
    public LiveData<SearchResult> result = _result;

    public LiveData<SearchResult> getResult(){return _result;}

    private MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;
    public LiveData<User> getUser() {return user;}

    private MutableLiveData<Event<SaveBookCheck>> _saveSuccess = new MutableLiveData<>();
    public LiveData<Event<SaveBookCheck>> saveSuccess = _saveSuccess;
    public LiveData<Event<SaveBookCheck>> getSaveSuccess(){return saveSuccess;}


    public void getAladinBookInfo(String isbn13){
        // 1. 분리된 클라이언트를 통해 서비스 생성
        AladinApiService apiService = RetrofitClient.createService(AladinApiService.class);

        // 2. API 호출
        Call<AladinResponse> call = apiService.lookupBook(
                TTB_KEY,
                isbn13,
                "ISBN13"
                , "js" // JSON 출력
                , "20131101"
        );

        // 3. 비동기 실행
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SearchResult bookItem = response.body().item.get(0);

                    Log.d("lookupBook", "result title: " + response.body().title);
                    Log.d("lookupBook", "result title: " + bookItem.title);
                    Log.d("lookupBook", "result title: " + bookItem.isbn13);
                    Log.d("lookupBook", "result title: " + bookItem.categoryName);
                    Log.d("lookupBook", "result title: " + bookItem.subInfo.itemPage);

                    Log.d("lookUpBook", "result" + bookItem.link);
                    Log.d("lookUpBook", "result" + bookItem.stockStatus);
                    String url = bookItem.cover.replace("/coversum/", "/cover200/");
                    bookItem.cover = url;
                    Log.d("lookupBook","cover: " + bookItem.cover);
                    _result.setValue(bookItem);
                }
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                Log.d("AladinResponse", "에러 발생: " + t.getMessage());
            }
        });
    }

    public void loadUser(String uid){
        UserRepo.getUser(uid, new UserRepo.OnUserListener() {
            @Override
            public void onSuccess(User user) {
                _user.setValue(user);
            }

            @Override
            public void onError(Exception e) {
                Log.e("Firestore", "유저 정보 불러오기 실패: " + e.getMessage());
            }
        });
    }

    public void saveFireStore(String uid, User user, MyBook myBook, String isbn13) {
        if (Boolean.TRUE.equals(isLoading.getValue())) return; // 이미 로딩 중이면 차단

        isLoading.setValue(true);
        MyBookRepo.saveMyBook(uid, user, user.getNickName(), myBook, isbn13, new MyBookRepo.OnMyBookListener() {
            @Override
            public void onSuccess(MyBook myBook) {
                isLoading.setValue(false);
                if (myBook.getState() == MyBook.STATE_DONE){
                    SaveBookCheck check = new SaveBookCheck(myBook, MyBook.STATE_DONE);
                    _saveSuccess.setValue(new Event<>(check));
                } else if (myBook.getState() == MyBook.STATE_READING) {
                    SaveBookCheck check = new SaveBookCheck(myBook, MyBook.STATE_READING);
                    _saveSuccess.setValue(new Event<>(check));
                }else {
                    SaveBookCheck check = new SaveBookCheck(myBook, MyBook.STATE_SAVED);
                    _saveSuccess.setValue(new Event<>(check));
                }
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                Log.e("Firestore", "책 저장 실패: " + e.getMessage());
            }
        });
    }
}
