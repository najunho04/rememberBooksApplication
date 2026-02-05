package com.najunho.rememberbooks.DataClass;

import java.io.Serializable;
import java.util.Objects;

public class DiscussionLog implements Serializable {
    private String question;
    private String answer;
    private long timestamp; // Firestore의 Timestamp를 long(ms)으로 변환해 사용 권장

    public DiscussionLog() {}

    public DiscussionLog(String question, String answer, long timestamp) {
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    // Getters
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public long getTimestamp() { return timestamp; }

    // DiffUtil을 위한 동일성 체크 로직
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscussionLog that = (DiscussionLog) o;
        return timestamp == that.timestamp &&
                Objects.equals(question, that.question) &&
                Objects.equals(answer, that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, answer, timestamp);
    }
}