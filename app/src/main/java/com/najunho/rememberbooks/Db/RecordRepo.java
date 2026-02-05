package com.najunho.rememberbooks.Db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.Util.ApplicationUtil;

import java.util.List;

public class RecordRepo {
    public interface OnRecordListener {
        void onSuccess(Record record);

        void onError(Exception e);
    }

    public interface OnRecordListListener {
        void onSuccess(List<Record> records);

        void onError(Exception e);
    }

    public static void saveRecord(String userId, String isbn13, Record record, OnRecordListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        DocumentReference recordRef = db.collection("users").document(userId)
                .collection("MyBooks").document(isbn13)
                .collection("Records").document();

        record.setId(recordRef.getId());

        recordRef.set(record)
                .addOnSuccessListener(aVoid -> {
                    MyBookRepo.updateReadPage(userId, isbn13, record.getEndPage(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            listener.onSuccess(record);
                        }
                    });
                })
                .addOnFailureListener(listener::onError);
    }

    public static void updateRecord(String userId, String isbn13, Record record, OnRecordListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        // 기존 ID를 사용하여 경로 지정
        db.collection("users").document(userId)
                .collection("MyBooks").document(isbn13)
                .collection("Records").document(record.getId())
                .set(record) // 덮어쓰기
                .addOnSuccessListener(aVoid -> {
                    // 수정된 내용의 마지막 페이지를 MyBook에 반영
                    MyBookRepo.updateReadPage(userId, isbn13, record.getEndPage(), task -> {
                        listener.onSuccess(record);
                    });
                })
                .addOnFailureListener(listener::onError);
    }
    public static void deleteRecord(String userId, String isbn13, Record record, int startPage, OnRecordListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        db.collection("users").document(userId)
                .collection("MyBooks").document(isbn13)
                .collection("Records").document(record.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // 삭제 성공 시 빈 Record 객체 혹은 null을 반환하여 UI에서 제거하게 함
                    //record  삭제는 가장 하단  record만 삭제 가능하게끔..
                    MyBookRepo.updateReadPage(userId, isbn13, startPage, task -> {
                        listener.onSuccess(record);
                    });
                })
                .addOnFailureListener(listener::onError);
    }

    public static void getRecords(String usrId, String isbn13, OnRecordListListener listener){
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        db.collection("users").document(usrId)
                .collection("MyBooks").document(isbn13)
                .collection("Records")
                .orderBy("date")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Record> records = querySnapshot.toObjects(Record.class);
                    listener.onSuccess(records);
                }).addOnFailureListener(e->{
                    Log.e("getRecords", e.getMessage());
                });
    }

}
