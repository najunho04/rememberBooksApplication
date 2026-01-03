package com.najunho.rememberbooks.DataClass;


public class Book {
    private String title;
    private String author;
    private String publisher;
    private String description;
    private String category;
    private int page; // 페이지 수는 숫자로 처리

    // 모든 필드를 포함하는 생성자
    public Book(String title, String author, String publisher, String description, String category, int page) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.category = category;
        this.page = page;
    }

    // Getter 및 Setter 메서드
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
}