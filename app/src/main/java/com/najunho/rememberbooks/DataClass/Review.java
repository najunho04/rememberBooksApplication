package com.najunho.rememberbooks.DataClass;

public class Review {
    private String user;
    private int score = 0;
    private String review;

    public Review(String user, int score, String review){
        this.user = user;
        this.score = score;
        this.review = review;
    }

    public String getUser() {return user;}
    public int getScore() {return score;}
    public String getReview() {return review;}
    public void setUser(String user) {this.user = user;}
    public void setScore(int score) {this.score = score;}
    public void setReview(String review) {this.review = review;}
}
