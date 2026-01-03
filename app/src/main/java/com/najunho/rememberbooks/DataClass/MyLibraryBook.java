package com.najunho.rememberbooks.DataClass;

public class MyLibraryBook {
    public String title;
    public String author;
    public String metaPrimary; //score or date of read
    public String metaSecondary; //comment or remaining page
    public String coverUrl;
    public String state;

    public MyLibraryBook(String title, String author, String metaPrimary, String metaSecondary, String coverUrl, String state) {
        this.title = title;
        this.author = author;
        this.metaPrimary = metaPrimary;
        this.metaSecondary = metaSecondary;
        this.coverUrl = coverUrl;
        this.state = state;
    }

    public String getTitle(){return title;}
    public void serTitle(String title){this.title = title;}
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author = author;}
    public String getScore(){return metaPrimary;}
    public void setScore(String score){this.metaPrimary = score;}
    public String getComment(){return metaSecondary;}
    public void setComment(String comment){this.metaSecondary = comment;}
    public String getCoverUrl(){return coverUrl;}
    public void setCoverUrl(String coverUrl){this.coverUrl = coverUrl;}
    public String getState(){return state;}
    public void setState(String state){this.state = state;}
}
