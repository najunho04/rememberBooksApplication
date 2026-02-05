package com.najunho.rememberbooks.Db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.Review;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Util.ApplicationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBookRepo {
    public interface OnMyBookListener{
        void onSuccess(MyBook myBook);
        void onError(Exception e);
    }

    public interface OnMyBookListListener{
        void onSuccess(List<MyBook> myBookList);
        void onError(Exception e);
    }

    public interface OnCheckListener {
        void onResult(boolean exists);
        void onError(Exception e);
    }
    public static void saveMyBook(String userId, User user, String nickName, MyBook myBook, String isbn13, OnMyBookListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();
        WriteBatch batch = db.batch();

        // 1. 내 서재 데이터 추가
        DocumentReference myBookRef = db.collection("users").document(userId)
                .collection("MyBooks").document(isbn13);
        batch.set(myBookRef, myBook.toHashMap());

        // 2. 독서 완료 상태일 때 다른 Repository 함수들 호출
        if (myBook.getState() == MyBook.STATE_DONE) {
            // 통계 업데이트 로직 위임 (bookStats Collection 용)
            BookStatsRepo.uploadBookStats(batch, myBook, userId, nickName);

            // 리뷰 업데이트 로직 위임 (review Collection 용)
            Review review = myBook.toReview(userId, nickName);
            review.setDisclosure(user.getDisclosure());
            ReviewRepo.addReview(batch, review);

            //user readCount update
            UserRepo.updateReadCount(batch, userId);
        }

        // 3. 최종적으로 한 번만 commit
        batch.commit()
                .addOnSuccessListener(aVoid -> listener.onSuccess(myBook))
                .addOnFailureListener(e -> listener.onError(e));
    }

    public static void getMyBook(String userId, OnMyBookListener listener){
        FirebaseFirestore db = ApplicationUtil.getFirestore();

         db.collection("users").document(userId)
                 .collection("MyBooks")
                 .whereEqualTo("state", 1)
                 .orderBy("dateOfRead", Query.Direction.DESCENDING)
                 .limit(1)
                 .get()
                 .addOnSuccessListener(querySnapshot -> {
                     if (!querySnapshot.isEmpty()) {
                         // 결과가 있을 때만 처리
                         MyBook myBook = querySnapshot.getDocuments().get(0).toObject(MyBook.class);
                         listener.onSuccess(myBook);
                     } else {
                         // 독서 중인 책이 없을 때 처리
                         listener.onSuccess(null);
                     }
                 })
                 .addOnFailureListener(listener::onError);
    }

    public static void getMyBookByIsbn13(String userId, String isbn13, OnMyBookListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        db.collection("users").document(userId)
                .collection("MyBooks").document(isbn13)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    MyBook myBook = documentSnapshot.toObject(MyBook.class);
                    listener.onSuccess(myBook);
                })
                .addOnFailureListener(e->{
                    Log.e("getMyBookByIsbn13", e.getMessage());
                    listener.onError(e);
                });
    }

    public static void getMyBookList(String userId, OnMyBookListListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        db.collection("users").document(userId)
                .collection("MyBooks")
                .orderBy("dateOfRead")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MyBook> myBookList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        MyBook myBook = document.toObject(MyBook.class);
                        myBookList.add(myBook);
                    }

                    listener.onSuccess(myBookList);
                }).addOnFailureListener(e->{
                    Log.e("getMyBookList", e.getMessage());
                    listener.onError(e);
                });
    }

    public static void updateReadPage(String userId, String isbn13, int readPage, OnCompleteListener listener){
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        db.collection("users")
                .document(userId)
                .collection("MyBooks")
                .document(isbn13)
                .update("readPage", readPage)
                .addOnSuccessListener(aviod -> {
                    Log.d("updateRemainingPage", "success");
                    listener.onComplete(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("updateRemainingPage", e.getMessage());
                });
    }

    public static void endReading(String userId, String isbn13, int score, String comment, OnCompleteListener<Void> listener){
        //update score, comment ,state to done
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        // 1. 업데이트할 필드들을 Map에 담기
        Map<String, Object> updates = new HashMap<>();
        updates.put("state", MyBook.STATE_DONE);
        updates.put("score", score);
        updates.put("comment", comment);

        db.collection("users").document(userId)
                .collection("MyBooks").document(isbn13)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("endReading", "success");
                    listener.onComplete(null);
                })
                .addOnFailureListener(e->{
                    Log.e("endReading", e.getMessage());
                });

    }

    public static void updateState(String userId, String isbn13, int state, String dateOfRead, OnCheckListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        Map<String, Object> updates = new HashMap<>();
        updates.put("state", state);
        updates.put("dateOfRead", dateOfRead);

        db.collection("users")
                .document(userId)
                .collection("MyBooks")
                .document(isbn13)
                .update(updates)
                .addOnSuccessListener(task -> {
                    listener.onResult(true);
                    Log.d("updateState", "success");
                }).addOnFailureListener(e->{
                    listener.onError(e);
                    Log.e("updateState", e.getMessage());
                });
    }

    public static void checkIsbn13Exists(String userId, String isbn13, OnCheckListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        // 특정 문서 경로를 바로 지정
        db.collection("users").document(userId)
                .collection("MyBooks").document(isbn13)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // 문서가 존재하면 true, 없으면 false 반환
                    if (documentSnapshot.exists()) {
                        listener.onResult(true);
                    } else {
                        listener.onResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("checkIsbn13Exists", "에러 발생: " + e.getMessage());
                    listener.onError(e);
                });
    }
}
