package com.najunho.rememberbooks.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Db.RecordRepo;
import com.najunho.rememberbooks.Util.TimeHelper;

import java.util.ArrayList;
import java.util.List;

public class RecordViewModel extends ViewModel {
    // 외부에서 수정할 수 없는 관찰 전용 데이터
    private final MutableLiveData<List<Record>> _recordList = new MutableLiveData<>();
    public final LiveData<List<Record>> recordList = _recordList;
    private MutableLiveData<Boolean> _isSaveSuccess = new MutableLiveData<>();
    private final MutableLiveData<MyBook> _myBook = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isUpdateSuccess = new MutableLiveData<>();



    public LiveData<MyBook> getMyBook() { return _myBook; }
    public LiveData<String> getErrorMessage() { return _errorMessage; }
    public LiveData<Boolean> isSaveSuccess() { return _isSaveSuccess; }
    public LiveData<List<Record>> getRecordList() {
        return recordList;
    }
    public LiveData<Boolean> isUpdateSuccess() { return _isUpdateSuccess; }

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public void addRecord(Record record, String isbn13) {
        if (Boolean.TRUE.equals(isLoading.getValue())) {
            Log.d("addRecord", "isLoading is true");
            return;
        }

        isLoading.setValue(true);
        RecordRepo.saveRecord(mAuth.getCurrentUser().getUid(), isbn13, record, new RecordRepo.OnRecordListener() {
            @Override
            public void onSuccess(Record record) {
                isLoading.setValue(false);
                List<Record> currentList = _recordList.getValue();
                if (currentList == null) {
                    currentList = new ArrayList<>(); // 혹시 모를 null 상황 방지
                }
                List<Record> newList = new ArrayList<>(currentList);
                newList.add(record);
                Log.d("LIST_TEST", "New List HashCode: " + newList.hashCode());

                _recordList.setValue(newList);
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                Log.e("Firestore", "책 저장 실패: " + e.getMessage());
            }
        });
    }
    public void updateRecord(Record updatedRecord, String isbn13) {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;

        isLoading.setValue(true);

        RecordRepo.updateRecord(mAuth.getCurrentUser().getUid(), isbn13, updatedRecord, new RecordRepo.OnRecordListener() {
            @Override
            public void onSuccess(Record record) {
                isLoading.setValue(false);
                List<Record> currentList = new ArrayList<>(_recordList.getValue());
                List<Record> newList = new ArrayList<>(currentList);

                for (int i = 0; i < currentList.size(); i++) {
                    if (currentList.get(i).getId() == updatedRecord.getId()) {
                        newList.set(i, updatedRecord);
                        break;
                    }
                }
                _recordList.setValue(newList);
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                Log.e("updateRecord", e.getMessage());
            }
        });

    }

    public void deleteRecord(Record record, String isbn13) {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;

        isLoading.setValue(true);

        RecordRepo.deleteRecord(mAuth.getCurrentUser().getUid(), isbn13, record, record.getStartPage() , new RecordRepo.OnRecordListener() {
            @Override
            public void onSuccess(Record record) {
                isLoading.setValue(false);
                List<Record> currentList = new ArrayList<>(_recordList.getValue());
                List<Record> newList = new ArrayList<>(currentList);

                newList.removeIf(r -> r.getId().equals(record.getId()));

                // 삭제 후 남은 아이템들의 Day를 순서대로 재정렬
                for (int i = 0; i < newList.size(); i++) {
                    Record old = newList.get(i);
                    // 기존 객체의 setDay를 쓰는 대신, 아예 새 객체를 생성해서 교체!
                    Record updated = new Record(
                            old.getId(),
                            "Day " + (i + 1),
                            old.getDate(),
                            old.getQuote(),
                            old.getThought(),
                            old.getStartPage(),
                            old.getEndPage()
                    );
                    newList.set(i, updated);
                }
                _recordList.setValue(newList);
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                Log.e("deleteRecord", e.getMessage());
            }
        });
    }

    public void getRecords(String isbn13) {
        Log.d("getRecords", "vm start");
        RecordRepo.getRecords(mAuth.getCurrentUser().getUid(), isbn13, new RecordRepo.OnRecordListListener() {
            @Override
            public void onSuccess(List<Record> records) {
                //Records 없을 경우 빈 리스트
                _recordList.setValue(records);
                Log.d("getRecords", "success");
            }

            @Override
            public void onError(Exception e) {
                Log.e("getRecords", e.getMessage());
            }
        });
    }

    public void endReading(String isbn13, int score, String comment) {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;

        isLoading.setValue(true);
        MyBookRepo.endReading(mAuth.getCurrentUser().getUid(), isbn13, score, comment, task -> {
            //intent (ReadDoneActivity로) and load?
            isLoading.setValue(false);
            _isSaveSuccess.setValue(true);
        });
    }


    public void loadMyBookByIsbn13(String userId, String isbn13) {
        MyBookRepo.getMyBookByIsbn13(userId, isbn13, new MyBookRepo.OnMyBookListener() {
            @Override
            public void onSuccess(MyBook myBook) {
                // 데이터를 LiveData에 저장 (그러면 관찰자에게 알림이 감)
                _myBook.setValue(myBook);
            }

            @Override
            public void onError(Exception e) {
                _errorMessage.setValue(e.getMessage());
            }
        });
    }

    public void loadLatestBook(){
        MyBookRepo.getMyBook(mAuth.getCurrentUser().getUid(), new MyBookRepo.OnMyBookListener() {
            @Override
            public void onSuccess(MyBook myBook) {
                //독서중인 책 DB에 없음
                if(myBook == null){
                    _myBook.setValue(null);
                }
                _myBook.setValue(myBook);
            }

            @Override
            public void onError(Exception e) {
                _errorMessage.setValue(e.getMessage());
            }
        });
    }

    public void updateState(String isbn13){
        MyBookRepo.updateState(mAuth.getCurrentUser().getUid(),
                isbn13,
                MyBook.STATE_READING,
                TimeHelper.getCurrentDate(),
                new MyBookRepo.OnCheckListener() {
                    @Override
                    public void onResult(boolean exists) {
                        _isUpdateSuccess.setValue(exists); //true
                    }

                    @Override
                    public void onError(Exception e) {
                        _errorMessage.setValue(e.getMessage());
                    }
                });
    }
}
