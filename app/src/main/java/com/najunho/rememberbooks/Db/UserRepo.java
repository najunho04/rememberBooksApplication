package com.najunho.rememberbooks.Db;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Util.ApplicationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserRepo {
    public interface OnUserListener{
        void onSuccess(User user);
        void onError(Exception e);
    }
    public interface OnUserListListener{
        void onSuccess(List<User> users);
        void onError(Exception e);
    }
    public interface OnCheckListener {
        void onSuccess(boolean success);
        void onError(Exception e);
    }

    public static void addUser(String userId, User user, OnCompleteListener<Void> listener){
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(listener);
    }

    public static void getUser(String userId, OnUserListener listener){
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    listener.onSuccess(user);
                })
                .addOnFailureListener(listener::onError);
    }

    public static void changeNickName(String userId, String newNickName, OnCompleteListener<Void> listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("nickName", newNickName);

        db.collection("users").document(userId)
                .update(updateData)
                .addOnCompleteListener(listener);
    }

    public static void updateReadCount(WriteBatch batch, String userId){
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        DocumentReference ref = db.collection("users").document(userId);
        batch.update(ref, "readCount", FieldValue.increment(1));
        Log.d("updateReadCount", "batch readCount");
    }

    public static void loadTopReadCountUsers(OnUserListListener listener){
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        // 1. readCount 필드를 기준으로 내림차순(DESCENDING) 정렬 후 상위 5개 제한
        db.collection("users")
                .orderBy("readCount", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    listener.onSuccess(users);
                })
                .addOnFailureListener(e -> {
                    Log.e("loadTopReadCountUsers", "e: " + e);
                });
    }

    public static void changeDisclosure(String uid, OnCheckListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        // 1. Transaction 시작 (읽기-쓰기를 한 번에 처리)
        db.runTransaction(transaction -> {
            DocumentReference docRef = db.collection("users").document(uid);
            DocumentSnapshot snapshot = transaction.get(docRef);

            User user = snapshot.toObject(User.class);
            if (user != null) {
                boolean currentDisclosure = user.getDisclosure();
                boolean newDisclosureValue = !currentDisclosure;

                // 유저 문서 업데이트
                transaction.update(docRef, "disclosure", newDisclosureValue);

                // 리뷰 공개 여부도 함께 변경해야 한다면 여기서 처리하거나 리턴값으로 활용
                return newDisclosureValue;
            }
            throw new FirebaseFirestoreException("User not found", FirebaseFirestoreException.Code.NOT_FOUND);
        }).addOnSuccessListener(newDisclosure -> {
            // 2. 유저 정보 수정 성공 후, 리뷰들도 일괄 변경 (Batch 활용 가능)
            // ReviewRepo.changeDisclosure 내부에서 새로운 Batch를 생성해서 commit 하도록 설계하세요.
            ReviewRepo.changeDisclosure(uid);
            listener.onSuccess(newDisclosure);
        }).addOnFailureListener(e -> {
            listener.onError(e);
        });
    }
}
