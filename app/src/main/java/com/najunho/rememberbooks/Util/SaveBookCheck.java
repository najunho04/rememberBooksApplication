package com.najunho.rememberbooks.Util;

import com.najunho.rememberbooks.DataClass.MyBook;

public class SaveBookCheck {
    private MyBook myBook;
    private int state;

    public SaveBookCheck(MyBook myBook, int state) {
        this.myBook = myBook;
        this.state = state;
    }
    public MyBook getMyBook(){return myBook;}
    public int getState(){return state;}
}
