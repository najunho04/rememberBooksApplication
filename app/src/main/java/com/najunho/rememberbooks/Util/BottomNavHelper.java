package com.najunho.rememberbooks.Util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.najunho.rememberbooks.Activity.MainActivity;
import com.najunho.rememberbooks.Activity.MyLibraryActivity;
import com.najunho.rememberbooks.Activity.MyPageActivity;
import com.najunho.rememberbooks.Activity.ReadingRecordActivity;
import com.najunho.rememberbooks.Activity.SearchBookListActivity;
import com.najunho.rememberbooks.R;

public class BottomNavHelper {
    public static void setupBottomNav(Activity activity) {
        View btnHome = activity.findViewById(R.id.btn_menu_home);
        View btnSearch = activity.findViewById(R.id.btn_menu_search);
        View btnLibrary = activity.findViewById(R.id.btn_menu_library);
        View btnRecord = activity.findViewById(R.id.btn_menu_record);
        View btnMypage = activity.findViewById(R.id.btn_menu_mypage);
        // ... 나머지 뷰 findViewById

        setNavClick(activity, btnHome, MainActivity.class);
        setNavClick(activity, btnSearch, SearchBookListActivity.class);
        setNavClick(activity, btnLibrary, MyLibraryActivity.class);
        setNavClick(activity, btnRecord, ReadingRecordActivity.class);
        setNavClick(activity, btnMypage, MyPageActivity.class);
        // ... 나머지 버튼들 등록
    }

    private static void setNavClick(Activity activity, View view, Class<?> targetClass) {
        if (view == null) return;
        view.setOnClickListener(v -> {
            if (activity.getClass().equals(targetClass)) {
                Toast.makeText(activity, "현재 페이지입니다.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(activity, targetClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        });
    }
}