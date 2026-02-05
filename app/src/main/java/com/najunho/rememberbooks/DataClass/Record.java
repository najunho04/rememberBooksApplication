package com.najunho.rememberbooks.DataClass;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Record implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id; // 고유 ID
    private String day;
    private String date;
    private String quote;
    private String thought;
    private int startPage;
    private int endPage;

    public Record(){}

    // 생성자, Getter, Setter (생략 가능)
    public Record(String id, String day, String date, String quote, String thought, int startPage, int endPage) {
        this.id = id;
        this.day = day;
        this.date = date;
        this.quote = quote;
        this.thought = thought;
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public String getId() { return id; }
    public String getDay() { return day; }
    public String getDate() { return date; }
    public String getQuote() { return quote; }
    public String getThought() { return thought; }
    public int getStartPage() { return startPage; }
    public int getEndPage() { return endPage; }

    public void setId(String id) { this.id = id; }
    public void setDay(String day) { this.day = day; }
    public void setDate(String date) { this.date = date; }
    public void setQuote(String quote) { this.quote = quote; }
    public void setThought(String thought) { this.thought = thought; }
    public void setStartPage(int startPage) { this.startPage = startPage; }
    public void setEndPage(int endPage) { this.endPage = endPage; }


    @NonNull
    @Override
    public String toString() {
        return "Record{" +
                "id='" + id + '\'' +
                ", day='" + day + '\'' +
                ", date='" + date + '\'' +
                ", quote='" + quote + '\'' +
                ", thought='" + thought + '\'' +
                ", startPage='" + startPage + '\'' +
                ", endPage='" + endPage + '\'' +
                '}';
    }
}