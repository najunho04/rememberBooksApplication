package com.najunho.rememberbooks.Db;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.najunho.rememberbooks.DataClass.Review;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Util.ApplicationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReviewRepo {
    public interface OnReviewListListener{
        void onSuccess(List<Review> reviews);
        void onError(Exception e);
    }
    public static void getReviewsByIsbn13(String isbn13, OnReviewListListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        db.collection("reviews")
                .whereEqualTo("isbn13", isbn13) // 특정 ISBN만 필터링
                .orderBy("timestamp", Query.Direction.DESCENDING) // 최신순 정렬
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Review> reviews = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot){
                        Review review = document.toObject(Review.class);
                        if (review.getDisclosure()){
                            reviews.add(review);
                        }
                    }

                    Log.d("getReviewsByIsbn13", "reviews: " + reviews);
                    listener.onSuccess(reviews);
                })
                .addOnFailureListener(e -> listener.onError(e));
    }

    public static void addReview(WriteBatch batch, Review review){
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        DocumentReference reviewRef = db.collection("reviews").document();

        batch.set(reviewRef, review.toMap());
        Log.d("addReview", "batch set up success");
    }

    public static void changeDisclosure(String uid) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        WriteBatch batch = db.batch();

        //트랜잭션 사용해야 하나?
        CollectionReference ref = db.collection("reviews");
        ref.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot) {
                Review newReview = doc.toObject(Review.class);
                if (Objects.equals(newReview.getUser(), uid)){
                    boolean newDisclosure = !newReview.getDisclosure();
                    newReview.setDisclosure(newDisclosure);
                    batch.set(ref.document(doc.getId()), newReview);
                }
            }
            batch.commit().addOnSuccessListener(t->{
                Log.d("changeDisclosure", "success");
            });
        });
    }
}
