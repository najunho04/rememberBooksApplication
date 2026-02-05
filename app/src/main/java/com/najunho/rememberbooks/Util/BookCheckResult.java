package com.najunho.rememberbooks.Util;

// 이벤트를 통해 전달하고 싶은 데이터 묶음
public class BookCheckResult {
    public final String isbn13;
    public final boolean isExist;

    public BookCheckResult(String isbn13, boolean isExist) {
        this.isbn13 = isbn13;
        this.isExist = isExist;
    }
}