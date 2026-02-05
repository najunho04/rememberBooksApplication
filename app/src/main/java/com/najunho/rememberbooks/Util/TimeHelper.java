package com.najunho.rememberbooks.Util;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.najunho.rememberbooks.R;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class TimeHelper {

    /**
     * 현재 날짜를 "yyyy.MM.dd" 형식의 문자열로 반환합니다.
     * @return 예: "2026.01.07"
     */
    public static String getCurrentDate() {
        // 1. 현재 날짜 가져오기 (2026-01-07 형태)
        LocalDate now = LocalDate.now();

        // 2. 원하는 포맷 정의 (ex: 2026.12.12)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        // 3. 포맷 적용 후 반환
        return now.format(formatter);
    }

    /**
     * Firebase Timestamp를 "yyyy.MM.dd" 형식의 문자열로 변환합니다.
     */
    public static String timestampToString(Timestamp timestamp) {
        if (timestamp == null) return "";

        // Timestamp -> Date -> LocalDate 변환 (Java 8 이상)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                .withZone(ZoneId.systemDefault());

        return formatter.format(timestamp.toInstant());
    }

    public static void openDatePicker(TextView targetTextView, Context context) {
        // 0. Context가 유효한지 체크 (Activity가 종료 중이면 다이얼로그를 띄우지 않음)
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }

        // 1. 현재 날짜 가져오기 (디폴트 값)
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // 2. DatePickerDialog 생성
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.MyDatePickerStyle,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 3. 날짜 선택 완료 시 처리 (selectedMonth는 0부터 시작하므로 +1 필요)
                    // YYYY.MM.DD 형식으로 포맷팅
                    String formattedDate = String.format(Locale.KOREA, "%d.%02d.%02d",
                            selectedYear, selectedMonth + 1, selectedDay);

                    // TextView에 결과 설정
                    targetTextView.setText(formattedDate);
                },
                year, month, day);

        // (선택 사항) 오늘 이후 날짜를 선택하지 못하게 막으려면 아래 코드 추가
        // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
}