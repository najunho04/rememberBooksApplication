package com.najunho.rememberbooks.Db;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.najunho.rememberbooks.Adapter.DiscussionAdapter;
import com.najunho.rememberbooks.DataClass.DiscussionLog;
import com.najunho.rememberbooks.Util.ApplicationUtil;

import java.util.ArrayList;
import java.util.List;

public class DiscussionRepo {
    public interface OnLogsLoadedListener {
        void onLogsLoaded(List<DiscussionLog> logs);
        void onError(Exception e);
    }
    public static ListenerRegistration observeDiscussionLogs(String userId, String isbn13, OnLogsLoadedListener listener) {
        FirebaseFirestore db = ApplicationUtil.getFirestore();

        return db.collection("users").document(userId)
                .collection("MyBooks").document(isbn13)
                .collection("discussion")
                .orderBy("timestamp", Query.Direction.DESCENDING) // 최신순
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        listener.onError(error);
                        return;
                    }
                    if (value == null){
                        listener.onError(new Exception("Value is null"));
                        return;
                    }

                    Log.d("observeDiscussionLogs", "docs size : " + value.getDocuments().size());
                    List<DiscussionLog> logs = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Timestamp ts = doc.getTimestamp("timestamp");
                        long time = (ts != null) ? ts.toDate().getTime() : System.currentTimeMillis();
                        logs.add(new DiscussionLog(
                                doc.getString("question"),
                                doc.getString("answer"),
                                time
                        ));
                        Log.d("observeDiscussionLogs", "question: " + doc.getString("question"));
                    }
                    listener.onLogsLoaded(logs);
                });
    }
}
