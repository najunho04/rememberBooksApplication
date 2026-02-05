package com.najunho.rememberbooks.Db;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.najunho.rememberbooks.DataClass.BookStats;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.Util.ApplicationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookStatsRepo {
    public interface OnBookStatsListener {
        void onSuccess(List<BookStats> bookStatsList);
        void onError(Exception e);
    }

    public static void uploadBookStats(WriteBatch batch, MyBook book, String userId, String nickName) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        // --- 1. 통계 업데이트 (상위 컬렉션) ---
        DocumentReference statsRef = db.collection("bookStats").document(book.getIsbn13());

        Map<String, Object> statsUpdates = new HashMap<>();
        statsUpdates.put("isbn13", book.getIsbn13());
        statsUpdates.put("readCount", FieldValue.increment(1));
        statsUpdates.put("totalScore", FieldValue.increment(book.getScore()));
        //averageScore 계산은 Cloud Run에서
        statsUpdates.put("title", book.getTitle());
        statsUpdates.put("author", book.getAuthor());
        statsUpdates.put("cover", book.getCover());

        // set() 대신 batch.set() 사용
        // SetOptions.merge(): 문서가 없으면 생성하고, 있으면 기존 필드를 유지하며 지정한 데이터만 합침(덮어쓰기 방지)
        batch.set(statsRef, statsUpdates, SetOptions.merge());

        // --- 2. 리뷰 저장 (서브컬렉션) ---
        // .add()는 DocumentReference를 자동으로 생성하므로 배치를 위해 미리 ID를 생성합니다.
        DocumentReference reviewRef = statsRef.collection("reviews").document();
        Map<String, Object> reviewData = book.toReview(userId, nickName).toMap();

        // batch.set()으로 리뷰 추가
        batch.set(reviewRef, reviewData);
        Log.d("uploadBookStats", "batch set up success");
    }

    public static void getRecommendBooks(OnBookStatsListener listener){
        FirebaseFirestore db = ApplicationUtil.getFirestore();
        db.collection("bookStats")
                .orderBy("totalScore", Query.Direction.DESCENDING) // 평균 점수 내림차순
                .limit(5) // 상위 5개만
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<BookStats> topBooks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        // 데이터 변환
                        BookStats stat = document.toObject(BookStats.class);
                        topBooks.add(stat);
                    }

                    listener.onSuccess(topBooks);
                })
                .addOnFailureListener(e -> {
                    Log.e("getRecommendBooks", "error: " + e);
                    listener.onError(e);
                });
    }
}
