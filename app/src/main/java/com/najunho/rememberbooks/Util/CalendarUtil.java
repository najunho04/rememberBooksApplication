package com.najunho.rememberbooks.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtil {
    public static List<Calendar> getDaysInMonth(Calendar currentMonth) {
        List<Calendar> dayList = new ArrayList<>();

        // 원본 훼손 방지를 위한 복제
        Calendar calendar = (Calendar) currentMonth.clone();

        // 1. 이번 달 1일로 세팅
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // 2. 1일의 요일 구하기 (일:1, 월:2, ..., 토:7)
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0~6으로 변환

        // 3. 캘린더 시작 날짜(지난달 날짜 포함)로 이동
        // 예: 1일이 수요일(3)이면, 일(0), 월(1), 화(2) 3칸을 뒤로 감
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);

        // 4. 42칸(6주) 채우기
        while (dayList.size() < 42) {
            dayList.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dayList;
    }

    //사용 x
    private void setMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private String getKeyFromCalendar(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d.%02d.%02d", year, month, day);
    }

    private Calendar getCalendarFromKey(String key) {
        String[] parts = key.split("\\.");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        setMidnight(calendar);
        return calendar;
    }
}