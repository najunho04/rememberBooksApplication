package com.najunho.rememberbooks.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Db.RecordRepo;

import java.util.ArrayList;
import java.util.List;

public class ReadDoneRecordViewModel extends ViewModel {
    private final MutableLiveData<MyBook> _book = new MutableLiveData<>();
    public final LiveData<MyBook> book = _book;
    private final MutableLiveData<List<Record>> _recordList = new MutableLiveData<>();
    public final LiveData<List<Record>> recordList = _recordList;

    public LiveData<MyBook> getMyBook(){return _book;}
    public LiveData<List<Record>> getRecordList(){return _recordList;}
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public void loadMyBook(String isbn13){
        MyBookRepo.getMyBookByIsbn13(mAuth.getCurrentUser().getUid(), isbn13, new MyBookRepo.OnMyBookListener() {
            @Override
            public void onSuccess(MyBook myBook) {
                _book.setValue(myBook);
                RecordRepo.getRecords(mAuth.getCurrentUser().getUid(), myBook.getIsbn13(), new RecordRepo.OnRecordListListener() {
                    @Override
                    public void onSuccess(List<Record> records) {
                        _recordList.setValue(new ArrayList<>(records));
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Firestore", "기록 가져오기 실패: " + e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("Firestore", "책 저장 실패: " + e);
            }
        });
    }
}
