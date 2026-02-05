package com.najunho.rememberbooks.DataClass;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class Review {
    private String isbn13;
    private String user;
    private String nickName;
    private int score = 0;
    private String review;
    private Timestamp timestamp;
    private boolean disclosure = true;

    public Review() {}

    public Review(String isbn13, String user, String nickName, int score, String review){
        this.isbn13 = isbn13;
        this.user = user;
        this.nickName = nickName;
        this.score = score;
        this.review = review;
    }

    // Firestore 저장을 위한 HashMap 변환 메서드
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("isbn13", isbn13);
        result.put("user", user);
        result.put("nickName", nickName);
        result.put("score", score);
        result.put("review", review);
        // 저장 시점의 서버 시간을 기록하도록 설정
        result.put("timestamp", FieldValue.serverTimestamp());
        result.put("disclosure", disclosure);
        return result;
    }

    // Getter / Setter
    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public boolean getDisclosure() { return disclosure; }
    public void setDisclosure(boolean disclosure) { this.disclosure = disclosure; }
}
