package com.najunho.rememberbooks.DataClass;

import java.util.List;

// 전체 응답을 담는 클래스
public class AladinResponse {
    public String version;
    public String title;
    public int totalResults;
    public List<SearchResult> item; // 검색/조회 결과가 모두 여기 담깁니다.
}