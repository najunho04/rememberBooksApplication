package com.najunho.rememberbooks.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.Db.MyBookRepo;

import java.util.List;

public class MyLibraryViewModel extends ViewModel {
    private MutableLiveData<List<MyBook>> _myBookList = new MutableLiveData<>();
    public LiveData<List<MyBook>> myBookList = _myBookList;

    public LiveData<List<MyBook>> getMyBookList(){return _myBookList;}
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void loadMyBooks(){
        MyBookRepo.getMyBookList(mAuth.getCurrentUser().getUid(), new MyBookRepo.OnMyBookListListener() {
            @Override
            public void onSuccess(List<MyBook> myBooks) {
                Log.d("loadMyLibraryBookList", "success" + myBooks.size());
                _myBookList.setValue(myBooks);
            }

            @Override
            public void onError(Exception e) {
                Log.e("loadMyLibraryBookList", "error: " + e);
            }
        });
    }
}
