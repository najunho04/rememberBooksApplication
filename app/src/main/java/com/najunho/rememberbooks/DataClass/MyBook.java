package com.najunho.rememberbooks.DataClass;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MyBook implements Serializable {
    public static final int STATE_DONE = 0;
    public static final int STATE_READING = 1;
    public static final int STATE_SAVED = 2;

    private String title;
    private String author;
    private String publisher;
    private String description;
    private String category;
    private int page = 0;
    private String cover;
    private String isbn13;

    //-----여기까진 searchResult에서 받아오는 필드---

    private String dateOfRead; //책 등록 시 업데이트
    private int readPage = 0;
    private int state = -1; // 0: 독서 완료, 1: 독서 중, 2: 보관 중
    private int score = 0; //독서 완료 시 등록
    private String comment; //독서 완료 시 등록

    // 1. 기본 생성자 (Firebase 등 라이브러리 사용 시 필수)
    public MyBook() {}

    // 2. 전체 필드 생성자
    public MyBook(String title, String author, String publisher, String description,
                  String category, int page, String cover,String isbn13, String dateOfRead,
                  int remainingPage, int state, int score, String comment) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.category = category;
        this.page = page;
        this.cover = cover;
        this.isbn13 = isbn13;
        this.dateOfRead = dateOfRead;
        this.readPage = remainingPage;
        this.state = state;
        this.score = score;
        this.comment = comment;
    }

    // 3. Getter 및 Setter
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

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getIsbn13(){return isbn13;}
    public void setIsbn13(String isbn13){this.isbn13 = isbn13;}

    public String getDateOfRead() { return dateOfRead; }
    public void setDateOfRead(String dateOfRead) { this.dateOfRead = dateOfRead; }

    public int getReadPage() { return readPage; }
    public void setReadPage(int readPage) { this.readPage = readPage; }

    public int getState() { return state; }
    public void setState(int state) { this.state = state; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    // 4. toString() - 디버깅 용도
    @Override
    public String toString() {
        return "MyBook{" +
                "title='" + title + '\'' +
                ", state=" + state +
                ", score=" + score +
                '}';
    }

    // 5. toHashMap() - DB(Firebase 등) 저장 용도
    public Map<String, Object> toHashMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("author", author);
        result.put("publisher", publisher);
        result.put("description", description);
        result.put("category", category);
        result.put("page", page);
        result.put("cover", cover);
        result.put("isbn13", isbn13);
        result.put("dateOfRead", dateOfRead);
        result.put("readPage", readPage);
        result.put("state", state);
        result.put("score", score);
        result.put("comment", comment);
        return result;
    }

    // 변환 생성자 추가
    public MyBook(SearchResult result) {
        this.title = result.title;
        this.author = result.author;
        this.publisher = result.publisher;
        this.description = result.description;
        this.category = result.categoryName;
        this.cover = result.cover;
        this.isbn13 = result.isbn13;

        // 페이지 수 처리 (subInfo가 null일 경우를 대비한 방어 코드)
        if (result.subInfo != null) {
            this.page = result.subInfo.itemPage;
            this.readPage = 0; // 초기값은 전체 페이지
        } else {
            this.page = 0;
            this.readPage = 0;
        }

        // SearchResult에 없는 필드들은 기본값 설정
        this.state = -1; // 기본적으로 '보관 중' 상태로 저장
        this.dateOfRead = "";      // 아직 읽지 않음
        this.score = 0;
        this.comment = "";
    }

    public Review toReview(String userId, String nickName) {
        // Review(isbn13, user, score, review) 생성자 활용
        // MyBook의 'comment'가 Review의 'review' 필드로 매핑됩니다.
        Review review = new Review(
                this.isbn13,
                userId,
                nickName,
                this.score,
                this.comment
        );

        // 추가적인 필드 세팅이 필요하다면 여기서 수행
        return review;
    }

}