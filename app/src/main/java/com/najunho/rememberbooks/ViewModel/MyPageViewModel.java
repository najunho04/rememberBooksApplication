package com.najunho.rememberbooks.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Db.RecordRepo;
import com.najunho.rememberbooks.Db.UserRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MyPageViewModel extends ViewModel {
    private MutableLiveData<Map<String, List<MyBook>>> _dailyBooksMap = new MutableLiveData<>();
    private Map<String, List<MyBook>> dailyBooksMap = new HashMap<>();
    private MutableLiveData<List<MyBook>> _myBookList = new MutableLiveData<>();
    private final MutableLiveData<List<Record>> _recordList = new MutableLiveData<>();
    private MutableLiveData<List<User>> _userList = new MutableLiveData<>();
    private MutableLiveData<Integer> _totalRead = new MutableLiveData<>();
    private MutableLiveData<Integer> _totalReading = new MutableLiveData<>();
    private MutableLiveData<Integer> _totalSave = new MutableLiveData<>();
    private MutableLiveData<User> _user = new MutableLiveData<>();

    public LiveData<List<User>> getUserList(){return _userList;}
    public LiveData<List<MyBook>> getMyBookList(){return _myBookList;}
    public LiveData<List<Record>> getRecordList(){return _recordList;}
    public LiveData<Map<String, List<MyBook>>> getDailyBooksMap(){return _dailyBooksMap;}
    public LiveData<Integer> getTotalRead(){return _totalRead;}
    public LiveData<Integer> getTotalReading(){return _totalReading;}
    public LiveData<Integer> getTotalSave(){return _totalSave;}
    public LiveData<User> getUser(){return _user;}



    public void loadDailyBooksMap(String uid){
        //Mybook 리스트 가져오기
        MyBookRepo.getMyBookList(uid, new MyBookRepo.OnMyBookListListener() {
            @Override
            public void onSuccess(List<MyBook> myBookList) {

                _myBookList.setValue(myBookList);
                final int totalBooks = myBookList.size();
                final AtomicInteger processedCount = new AtomicInteger(0); // 스레드 안전한 카운터
                Log.d("loadDailyBooksMap", "getMyBookList Success");

                for(MyBook book : myBookList){
                    if (book == null){
                        Log.e("loadDailyBooksMap", "no myBook data");
                        return;
                    }
                    //MyBook 마다 가진 Records 가져오기
                    getRecords(uid, book.getIsbn13(), new RecordRepo.OnRecordListListener() {
                        @Override
                        public void onSuccess(List<Record> records) {
                            processedCount.incrementAndGet();
                            Log.d("loadDailyBooksMap", "processedCount: " + processedCount.get());
                            if (records.isEmpty()){
                                Log.d("loadDailyBooksMap", "no records");
                                if (processedCount.get() == totalBooks) {
                                    //dailyBooksMap 액티비티로 observe
                                    _dailyBooksMap.setValue(new HashMap<>(dailyBooksMap));
                                    Log.d("loadDailyBooksMap", "dailyBooksMap setValue:" + dailyBooksMap.toString());
                                }
                                return;
                            }
                            _recordList.setValue(records);
                            Log.d("loadDailyBooksMap", "getRecords success");

                            //record date 필드 key, MyBook 객체 value로 Map 저장
                            for (Record record : records){
                                Log.d("getRecords", record.toString());
                                String date = record.getDate();
                                if (date != null){
                                    if (!dailyBooksMap.containsKey(date)) {
                                        dailyBooksMap.put(date, new ArrayList<>());
                                    }
                                    dailyBooksMap.get(date).add(book);
                                    Log.d("loadDailyBooksMap", "dailyBooksMap:" + dailyBooksMap.toString());
                                }
                            }
                            Log.d("loadDailyBooksMap", "totalBooks: " + totalBooks);
                            // [중요] 모든 Records 조회가 끝났는지 확인
                            if (processedCount.get() == totalBooks) {
                                //dailyBooksMap 액티비티로 observe
                                _dailyBooksMap.setValue(new HashMap<>(dailyBooksMap));
                                Log.d("loadDailyBooksMap", "dailyBooksMap setValue:" + dailyBooksMap.toString());
                            }
                        }
                        @Override
                        public void onError(Exception e) {
                            Log.e("getRecords", "e " + e);
                            // 에러가 나더라도 카운트는 올려줘야 마지막에 setValue가 호출됨
                            if (processedCount.incrementAndGet() == totalBooks) {
                                _dailyBooksMap.setValue(new HashMap<>(dailyBooksMap));
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("getMyBookList", "e " + e);
            }
        });
    }

    public void getRecords(String uid, String isbn13, RecordRepo.OnRecordListListener listener){
        RecordRepo.getRecords(uid, isbn13, new RecordRepo.OnRecordListListener() {
            @Override
            public void onSuccess(List<Record> records) {
                listener.onSuccess(records);
            }

            @Override
            public void onError(Exception e) {
                Log.e("getRecords", "e: " + e);
            }
        });
    }

    public void loadTopReadCountUsers(){
        UserRepo.loadTopReadCountUsers(new UserRepo.OnUserListListener() {

            @Override
            public void onSuccess(List<User> users) {
                _userList.setValue(new ArrayList<>(users));
            }

            @Override
            public void onError(Exception e) {
                Log.e("loadTopReadCountUsers", "e:" + e);
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
                Log.e("loadUser", "e:" + e);
            }
        });
    }

}
