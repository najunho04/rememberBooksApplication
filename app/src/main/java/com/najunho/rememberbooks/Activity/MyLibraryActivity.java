package com.najunho.rememberbooks.Activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Util.BottomNavHelper;
import com.najunho.rememberbooks.ViewModel.MyLibraryViewModel;
import com.najunho.rememberbooks.ViewPagerAdpater.LibraryPagerAdapter;

import java.util.List;

public class MyLibraryActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView[] tabs;
    private TextView emptyLibrary;
    private MyLibraryViewModel myLibraryViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);

        // 1. 초기화 (View, ViewModel, Listener)
        initViews();
        initViewModel();

        // 2. Observer 등록 (한 번만 실행됨)
        observeData();

        // 3. 하단 바 설정
        BottomNavHelper.setupBottomNav(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 4. 데이터 갱신 요청 (화면에 진입할 때마다 실행)
        // 다른 액티비티에서 변경된 내용이 있다면 여기서 다시 로드하여 반영합니다.
        myLibraryViewModel.loadMyBooks();
    }

    private void initViews() {
        emptyLibrary = findViewById(R.id.empty_library);
        viewPager = findViewById(R.id.vp_library);

        tabs = new TextView[]{
                findViewById(R.id.tab_all),
                findViewById(R.id.tab_done),
                findViewById(R.id.tab_reading),
                findViewById(R.id.tab_saved)
        };

        // 탭 클릭 리스너 설정
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            tabs[i].setOnClickListener(v -> viewPager.setCurrentItem(index));
        }

        // ViewPager 슬라이드 콜백 설정
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabUI(position);
            }
        });
    }

    private void initViewModel() {
        myLibraryViewModel = new ViewModelProvider(this).get(MyLibraryViewModel.class);
    }

    private void observeData() {
        // 데이터가 변경되면 UI(Adapter)를 업데이트합니다.
        myLibraryViewModel.getMyBookList().observe(this, myBookList -> {
            if (myBookList != null) {
                updateLibraryUI(myBookList);
            }
        });
    }

    private void updateLibraryUI(List<MyBook> myBookList) {
        if (myBookList!=null){
            // 1. 빈 화면 처리
            if (myBookList.isEmpty()) {
                emptyLibrary.setVisibility(View.VISIBLE);
            } else {
                emptyLibrary.setVisibility(View.GONE);
            }

            // 2. 어댑터 연결
            // 주의: 데이터를 갱신할 때마다 어댑터를 새로 만들면 현재 보고 있던 탭 위치가 초기화될 수 있습니다.
            // 현재 위치를 저장했다가 복구하는 로직을 추가하면 더 자연스럽습니다.
            int currentItem = viewPager.getCurrentItem();

            LibraryPagerAdapter adapter = new LibraryPagerAdapter(this, myBookList);
            viewPager.setAdapter(adapter);

            // 데이터 갱신 후 이전 탭 위치 유지 (필요 시 사용)
            viewPager.setCurrentItem(currentItem, false);
        }
    }

    private void updateTabUI(int position) {
        for (int i = 0; i < tabs.length; i++) {
            if (i == position) {
                tabs[i].setBackgroundResource(R.drawable.bg_tab_selected);
                tabs[i].setBackgroundTintList(null); // 원본 색상
            } else {
                tabs[i].setBackgroundResource(R.drawable.bg_tab_selected);
                tabs[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3EFEA"))); // 비활성 색상
            }
        }
    }
}