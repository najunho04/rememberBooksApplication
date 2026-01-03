package com.najunho.rememberbooks.DataClass;

public class ReadDoneBook {
    public String title;
    public String author;
    public String score;
    public String comment;
    public String coverUrl;
    public String state;

    public ReadDoneBook(String title, String author, String score, String comment, String coverUrl) {
        this.title = title;
        this.author = author;
        this.score = score;
        this.comment = comment;
        this.coverUrl = coverUrl;
    }

    public String getTitle(){return title;}
    public void serTitle(String title){this.title = title;}
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author = author;}
    public String getScore(){return score;}
    public void setScore(String score){this.score = score;}
    public String getComment(){return comment;}
    public void setComment(String comment){this.comment = comment;}
    public String getCoverUrl(){return coverUrl;}
    public void setCoverUrl(String coverUrl){this.coverUrl = coverUrl;}
    public String getState(){return state;}
    public void setState(String state){this.state = state;}

}
