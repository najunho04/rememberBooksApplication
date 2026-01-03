package com.najunho.rememberbooks.Activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.najunho.rememberbooks.DataClass.MyLibraryBook;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.ViewPagerAdpater.LibraryPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyLibraryActivity extends AppCompatActivity {
    private List<MyLibraryBook> myLibraryBookList;
    private ViewPager2 viewPager;
    private TextView[] tabs;
    private TextView tab_all, tab_done, tab_reading, tab_saved;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);

        myLibraryBookList = new ArrayList<>();
        MyLibraryBook myBook1 = new MyLibraryBook("데미안", "헤르만 헤세", "12", "지리네","", "독서 완료");
        MyLibraryBook myBook2 = new MyLibraryBook("데미안", "헤르만 헤세", "12", "지리네","", "독서 완료");
        MyLibraryBook myBook3 = new MyLibraryBook("데미안", "헤르만 헤세", "12", "지리네","", "독서 완료");

        myLibraryBookList.add(myBook1);
        myLibraryBookList.add(myBook2);
        myLibraryBookList.add(myBook3);


        viewPager = findViewById(R.id.vp_library);
        tabs = new TextView[]{
                findViewById(R.id.tab_all),
                findViewById(R.id.tab_done),
                findViewById(R.id.tab_reading),
                findViewById(R.id.tab_saved)
        };

        // 1. 어댑터 설정
        LibraryPagerAdapter adapter = new LibraryPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 2. 탭 클릭 시 ViewPager 이동
        for (int i = 0; i < tabs.length; i++) {
            final int index = i; //chip 삭제 로직과 동일
            tabs[i].setOnClickListener(v -> viewPager.setCurrentItem(index));
        }

        // 3. ViewPager 슬라이드 시 탭 디자인 변경 (콜백)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabUI(position);
            }
        });
    }
    private void updateTabUI(int position) {
        for (int i = 0; i < tabs.length; i++) {
            if (i == position) {
                tabs[i].setBackgroundResource(R.drawable.bg_tab_selected);
                tabs[i].setBackgroundTintList(null); // 원본 색상(베이지)
            } else {
                tabs[i].setBackgroundResource(R.drawable.bg_tab_selected);
                tabs[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3EFEA"))); // 비활성 색상
            }
        }
    }
}
