package com.najunho.rememberbooks.DataClass;

import java.util.HashMap;
import java.util.Map;

public class BookStats {
    // isbn13은 문서 ID(Doc ID)로 사용되므로 필드에 포함하거나
    // 생성 시 별도로 관리할 수 있습니다.
    private String isbn13;
    private int totalScore;
    private int readCount;
    private double averageScore;
    private String title;
    private String author;
    private String cover;

    // 1. 기본 생성자 (Firestore에서 객체 직렬화를 위해 반드시 필요합니다)
    public BookStats() {}

    // 2. 전체 필드를 포함한 생성자
    public BookStats(String isbn13, String title, String author) {
        this.isbn13 = isbn13;
        this.title = title;
        this.author = author;
    }

    // 3. 데이터를 Map 형태로 변환하는 메서드 (DB 저장용)
    public Map<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("isbn13", isbn13);
        map.put("score", totalScore);
        map.put("readCount", readCount);
        map.put("averageScore", averageScore);
        map.put("title", title);
        map.put("author", author);
        map.put("cover", cover);
        return map;
    }

    // 4. Getter 및 Setter
    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }
    public double getAverageScore(){return averageScore;}
    public void setAverageScore(double averageScore){this.averageScore = averageScore;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author = author;}
    public String getCover(){return cover;}
    public void setCover(String cover){this.cover = cover;}


}