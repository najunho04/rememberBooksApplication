package com.najunho.rememberbooks.DataClass;

import java.io.Serializable;


// 개별 도서 정보를 담는 클래스
public class SearchResult implements Serializable {
    // [기존 검색 API용 공통 필드]
    public String title;       // 상품명
    public String author;      // 저자
    public String cover;       // 커버 이미지 URL
    public int customerReviewRank;
    public String isbn13;      // 도서 고유 식별자 (13자리 ISBN)

    // [수정: 추가된 필드 4개]
    public String pubDate;     // 출간일 (문자열 - 예: "2026-01-29")
    public int priceSales;     // 판매가 (정수)
    public String stockStatus; // 재고 상태 (있을 경우 "", 품절 시 "품절" 등)
    public String link;        // 상품 상세 페이지 링크 (URL 문자열)

    // [상세 조회 API 시 추가되는 필드]
    public String publisher;   // 출판사
    public String description; // 상품 설명 (요약)
    public String categoryName;// 카테고리 (분야명)

    // [부가 정보 - 페이지 수 포함]
    public SubInfo subInfo;

    // subInfo 계층 구조를 위한 내부 클래스
    public static class SubInfo implements Serializable {
        public int itemPage;   // 책 페이지 수
    }
}