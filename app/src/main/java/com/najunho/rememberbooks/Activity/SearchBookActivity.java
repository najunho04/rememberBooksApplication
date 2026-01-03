package com.najunho.rememberbooks.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.Adapter.ContentAdapter;
import com.najunho.rememberbooks.Adapter.HeaderAdapter;
import com.najunho.rememberbooks.Adapter.TabAdapter;
import com.najunho.rememberbooks.DataClass.Book;
import com.najunho.rememberbooks.R;

public class SearchBookActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ConcatAdapter concatAdapter;

    // 섹션별 어댑터
    private HeaderAdapter headerAdapter;
    private TabAdapter tabAdapter;
    private ContentAdapter contentAdapter;
    private int currentPosition = 0; // ★ 1. 현재 페이지 상태를 Activity가 직접 관리
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        recyclerView = findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Book bookData = new Book(
                "데미안",
                "헤르만 헤세",
                "민음사",
                "자아의 삶을 추구하는 한 젊은이의 통과 의례를 기록한 고전...",
                "장르소설, 독일문학",
                239
        );

        headerAdapter = new HeaderAdapter(bookData);

        contentAdapter = new ContentAdapter(this, position -> {
            //fragment selected 시에 버튼 ui 변경
            //recyclerview layout 계산 중에 fragment 변경 로직 발생하면 issue 발생
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    updateCurrentPosition(position);
                }
            });
        });

        tabAdapter = new TabAdapter(position -> {
            // 탭 클릭 시 ViewPager 페이지 이동
            updateCurrentPosition(position);
        });

        //ConcatAdapter 설정 및 조립
        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(true) // 뷰 타입 독립 유지
                .build();

        //결합
        concatAdapter = new ConcatAdapter(config, headerAdapter, tabAdapter, contentAdapter);
        recyclerView.setAdapter(concatAdapter);

        // 6. 하단 고정 버튼 리스너 (독서완료, 시작, 보관)
        setupBottomButtons();
    }

    private void setupBottomButtons() {
        findViewById(R.id.read_done_btn).setOnClickListener(v -> {
            Toast.makeText(this, "독서 완료 상태로 저장되었습니다.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.read_start_btn).setOnClickListener(v -> {
            Toast.makeText(this, "독서를 시작합니다!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.store_btn).setOnClickListener(v -> {
            Toast.makeText(this, "내 서재에 보관되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }
    private void updateCurrentPosition(int position){
        if(currentPosition == position){
            return;
        }
        currentPosition = position;

        //activity에서 어댑터 UI 통합 관리
        if (tabAdapter != null) {
            tabAdapter.updateUi(currentPosition); // TabAdapter에게: "UI 변경해!"
        }
        if (contentAdapter != null) {
            contentAdapter.setCurrentItem(currentPosition); // ContentAdapter에게: "페이지 이동해!"
        }
    }

}
