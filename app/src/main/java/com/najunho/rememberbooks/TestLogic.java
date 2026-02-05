package com.najunho.rememberbooks;

import com.najunho.rememberbooks.DataClass.MyBook; // 패키지 경로에 맞춰 수정하세요
import java.util.ArrayList;
import java.util.List;

public class TestLogic {

    /**
     * 테스트용 MyBook 객체 3개를 생성하여 리스트로 반환합니다.
     * @return List<MyBook> 테스트 데이터 리스트
     */
    public static List<MyBook> getTestMyBookData() {
        List<MyBook> testList = new ArrayList<>();

        // 1. 독서 완료 상태의 책 (데미안)
        testList.add(new MyBook(
                "데미안",
                "헤르만 헤세",
                "민음사",
                "내 속에서 솟아 나오려는 것, 바로 그것을 나는 살아보려고 했다.",
                "국내도서>소설/시/희곡>세계문학",
                248,
                "https://image.aladin.co.kr/product/26/0/cover500/8937460443_1.jpg",
                "",
                "2023-10-01",
                0,    // 남은 페이지
                0,    // 0: 독서 완료
                95,    // 평점
                "나를 찾아가는 고통스러운 과정이 인상 깊었다."
        ));

        // 2. 독서 중 상태의 책 (불편한 편의점)
        testList.add(new MyBook(
                "불편한 편의점",
                "김호연",
                "나무옆의자",
                "서울 청파동 골목의 편의점에서 벌어지는 가슴 따뜻한 이야기.",
                "국내도서>소설/시/희곡>한국소설",
                268,
                "https://image.aladin.co.kr/product/26871/68/cover500/k192731502_1.jpg",
                "",
                "2023-12-15",
                120,  // 남은 페이지
                1,    // 1: 독서 중
                -1,    // 아직 매기지 않음
                null
        ));

        // 3. 보관 중 상태의 책 (사피엔스)
        testList.add(new MyBook(
                "사피엔스",
                "유발 하라리",
                "김영사",
                "인간의 역사에 대한 거대한 질문과 통찰.",
                "국내도서>인문학>서양철학",
                636,
                "https://image.aladin.co.kr/product/6835/65/cover500/893497246x_1.jpg",
                "",
                "2024-01-01",
                636,  // 남은 페이지 (아직 안 읽음)
                2,    // 2: 보관 중
                0,
                null
        ));

        return testList;
    }
}