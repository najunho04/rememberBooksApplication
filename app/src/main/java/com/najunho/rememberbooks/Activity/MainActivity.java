package com.najunho.rememberbooks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.Adapter.RankSectionAdapter;
import com.najunho.rememberbooks.Adapter.ReadBookSectionAdapter;
import com.najunho.rememberbooks.Adapter.RecommendSectionAdapter;
import com.najunho.rememberbooks.DataClass.ReadDoneBook;
import com.najunho.rememberbooks.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    private RecyclerView rvMain;
    private ConcatAdapter concatAdapter;
    private List<ReadDoneBook> bookList;
    private ImageButton btnMenuRecord, btn_menu_search, btn_menu_library, btn_menu_home, btn_menu_mypage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        btnMenuRecord = findViewById(R.id.btn_menu_record);
        btnMenuRecord.setOnClickListener(v -> {
            // 기록 페이지로 이동
            Intent intent = new Intent(this, ReadingRecordActivity.class);
            startActivity(intent);
        });

        btn_menu_search = findViewById(R.id.btn_menu_search);
        btn_menu_search.setOnClickListener(v->{
            // 검색 페이지로 이동
            Intent intent = new Intent(this, SearchBookActivity.class);
            startActivity(intent);
        });

        btn_menu_library = findViewById(R.id.btn_menu_library);
        btn_menu_library.setOnClickListener(v->{
            // 도서관 페이지로 이동
            Intent intent = new Intent(this, MyLibraryActivity.class);
            startActivity(intent);
        });

        btn_menu_home = findViewById(R.id.btn_menu_home);

        btn_menu_mypage = findViewById(R.id.btn_menu_mypage);
        btn_menu_mypage.setOnClickListener(v->{
            // 마이페이지 페이지로 이동
            Intent intent = new Intent(this, ReadDoneRecordActivity.class);
            startActivity(intent);
        });

        bookList = new ArrayList<>();
        ReadDoneBook book1 = new ReadDoneBook("데미안", "헤르만 헤세", "98", "알을 깨고 나오는 사람들의 이야기", "");
        ReadDoneBook book2 = new ReadDoneBook("인간실격", "다자이 오사무", "98", "알을 깨고 나오는 사람들의 이야기", "");
        ReadDoneBook book3 = new ReadDoneBook("인간실격", "다자이 오사무", "98", "알을 깨고 나오는 사람들의 이야기", "");
        bookList.add(book1);
        bookList.add(book2);
        bookList.add(book3);
        //-> 부모 스크롤뷰 때문에 자식 리사이클러뷰가 작동 x
        //ConcatAdapter 사용 예정

        List<ReadDoneBook> rankedBooks = new ArrayList<>();
        rankedBooks.add(book1);
        rankedBooks.add(book1);
        rankedBooks.add(book1);
        rankedBooks.add(book1);
        rankedBooks.add(book1);

        rvMain = findViewById(R.id.main_recycler);
        rvMain.setLayoutManager(new LinearLayoutManager(this));

        ReadBookSectionAdapter readAdapter = new ReadBookSectionAdapter(rankedBooks);
        RankSectionAdapter top5Adapter = new RankSectionAdapter(rankedBooks);
        RecommendSectionAdapter recommendAdapter = new RecommendSectionAdapter(rankedBooks);

        //ConcatAdapter setup
        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(true)
                .build();

        concatAdapter = new ConcatAdapter(config, readAdapter, top5Adapter, recommendAdapter);

        rvMain.setAdapter(concatAdapter);
    }
}
