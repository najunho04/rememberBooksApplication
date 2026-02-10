package com.najunho.rememberbooks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.najunho.rememberbooks.Adapter.Main.RankSectionAdapter;
import com.najunho.rememberbooks.Adapter.Main.ReadBookSectionAdapter;
import com.najunho.rememberbooks.Adapter.Main.RecommendSectionAdapter;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Util.BookCheckResult;
import com.najunho.rememberbooks.Util.BottomNavHelper;
import com.najunho.rememberbooks.ViewModel.MainViewModel;
import com.najunho.rememberbooks.ViewModel.SearchBookListViewModel;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    private FrameLayout loadingLayout;
    private RecyclerView rvMain;
    private ConcatAdapter concatAdapter;
    // bookList는 ViewModel에서 관리하므로 여기서 굳이 멤버변수로 필요 없을 수도 있습니다.
    // private List<MyBook> bookList;

    // 어댑터들도 데이터 갱신을 위해 멤버 변수로 빼두는 것이 좋습니다 (선택사항이나 추천)
    private ReadBookSectionAdapter readAdapter;
    private RankSectionAdapter top5Adapter;
    private RecommendSectionAdapter recommendAdapter;

    private MainViewModel viewModel;
    private SearchBookListViewModel searchViewModel;
    private long lastClickTime = 0;

    //Main View 호출 변수
    private int loadedTaskCount = 0;
    private final int TOTAL_TASKS = 3; // userList, myBookList 두 개

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. 초기화 (Initialization) ---
        initViews();
        initAds();
        initViewModels();
        initAdapters(); // 어댑터 및 리사이클러뷰 설정

        // --- 2. Observer 등록 (한 번만 등록하면 됨) ---
        // 주의: observe는 onStart로 옮기지 않습니다. (중복 등록 방지)
        observeData();

        // 하단 바 설정
        BottomNavHelper.setupBottomNav(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // --- 3. 데이터 갱신 요청 (Data Fetching) ---
        // 화면이 보일 때마다 최신 데이터를 가져오도록 요청합니다.
        // 기존 데이터가 있다면 LiveData가 알아서 UI를 갱신합니다.
        Log.d("MainActivity", "onStart: Refreshing Data");
        viewModel.getMyBookList();
        viewModel.getRecommendBooks();
    }

    private void initViews() {
        loadingLayout = findViewById(R.id.layout_loading);
        loadingLayout.setVisibility(View.VISIBLE);

        rvMain = findViewById(R.id.main_recycler);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        rvMain.setAlpha(0f);
    }

    private void initAds() {
        MobileAds.initialize(this, initializationStatus -> { });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setVisibility(View.GONE);
    }

    private void initViewModels() {
        searchViewModel = new ViewModelProvider(this).get(SearchBookListViewModel.class);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void initAdapters() {
        // ReadBookSectionAdapter 초기화
        readAdapter = new ReadBookSectionAdapter(book -> {
            if (SystemClock.uptimeMillis() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.uptimeMillis();
            Log.d("ReadBookSectionAdapter", "onclick event");

            Intent intent = ReadDoneRecordActivity.newIntent(this, book.getIsbn13());
            startActivity(intent);
        });

        // RankSectionAdapter 초기화
        top5Adapter = new RankSectionAdapter(book -> {
            if (SystemClock.uptimeMillis() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.uptimeMillis();
            Log.d("RankSectionAdapter", "onclick event");

            Intent intent = ReadDoneRecordActivity.newIntent(this, book.getIsbn13());
            startActivity(intent);
        });

        // RecommendSectionAdapter 초기화
        recommendAdapter = new RecommendSectionAdapter(book -> {
            if (SystemClock.uptimeMillis() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.uptimeMillis();
            Log.d("RecommendSectionAdapter", "onclick event");

            searchViewModel.existBook(book.getIsbn13());
        });

        // ConcatAdapter 설정
        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(true)
                .build();
        concatAdapter = new ConcatAdapter(config, readAdapter, top5Adapter, recommendAdapter);
        rvMain.setAdapter(concatAdapter);
    }

    private void checkAllUIReady() {
        loadedTaskCount++;
        Log.d("showMainContent", "showMainContent count ++ ");
        if (loadedTaskCount >= TOTAL_TASKS) {
            // 모든 UI 로직이 끝났을 때만 호출
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // 0.5초 후에 실행될 로직 : MainView 보이기
                Log.d("showMainContent", "showMainContent start");
                Log.d("showMainContent", "time: " + (SystemClock.uptimeMillis()));
                showMainContent();
            }, 1000); // 500ms = 0.5초
        }
    }

    private void showMainContent() {
        rvMain.setAlpha(1f);
        mAdView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
        Log.d("showMainContent", "showMainContent called");
    }
    private void observeData() {
        // 1. 추천 도서 데이터 감지 -> 어댑터 갱신
        viewModel.getRecommendBookList().observe(this, bookStatsList -> {
            if (bookStatsList != null){
                Log.d("getRecommendBookList", "Updated: " + bookStatsList.size());
                recommendAdapter.setRecommendData(bookStatsList);
                checkAllUIReady();
            }
        });

        // 2. 완독 도서 데이터 감지 -> 어댑터 갱신
        viewModel.getMyReadDoneBookList().observe(this, myReadDoneBookList -> {
            if (myReadDoneBookList != null){
                Log.d("getMyReadDoneBookList", "Updated: " + myReadDoneBookList.size());
                readAdapter.setTop5Data(myReadDoneBookList);
                checkAllUIReady();
            }
        });

        // 3. Top5 도서 데이터 감지 -> 어댑터 갱신
        viewModel.getMyTop5BookList().observe(this, myTop5BookList -> {
            if (myTop5BookList != null){
                Log.d("getMyTop5BookList", "Updated: " + myTop5BookList.size());
                top5Adapter.setTop5Data(myTop5BookList);
                checkAllUIReady();
            }
        });

        // 4. 검색/상세 이동 이벤트 감지 (SingleLiveEvent 패턴)
        searchViewModel.getNavigateToDetail().observe(this, event -> {
            BookCheckResult result = event.getContentIfNotHandled();
            if (result != null) {
                if (result.isExist){
                    Toast.makeText(this, "이미 존재하는 책입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, SearchBookActivity.class);
                    intent.putExtra("isbn13", result.isbn13);
                    startActivity(intent);
                }
            }
        });
    }
}