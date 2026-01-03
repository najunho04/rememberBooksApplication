package com.najunho.rememberbooks.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.najunho.rememberbooks.Adapter.RecommendSectionAdapter;
import com.najunho.rememberbooks.DataClass.Record;

import java.util.ArrayList;
import java.util.List;

public class RecordViewModel extends ViewModel {
    // 외부에서 수정할 수 없는 관찰 전용 데이터
    private final MutableLiveData<List<Record>> _recordList = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Record>> recordList = _recordList;

    public LiveData<List<Record>> getRecordList() {
        return recordList;
    }

    public void addRecord(Record record) {
        List<Record> currentList = new ArrayList<>(_recordList.getValue());
        if (currentList == null) {
            currentList = new ArrayList<>(); // 혹시 모를 null 상황 방지
        }
        List<Record> newList = new ArrayList<>(currentList);
        newList.add(record);
        Log.d("LIST_TEST", "New List HashCode: " + newList.hashCode());

        _recordList.setValue(newList);
    }
    public void updateRecord(Record updatedRecord) {
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

    public void deleteRecord(Record record) {
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
}
