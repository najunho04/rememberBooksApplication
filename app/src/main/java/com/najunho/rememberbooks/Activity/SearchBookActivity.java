package com.najunho.rememberbooks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.najunho.rememberbooks.Adapter.Search.ContentAdapter;
import com.najunho.rememberbooks.Adapter.Search.HeaderAdapter;
import com.najunho.rememberbooks.Adapter.Search.TabAdapter;
import com.najunho.rememberbooks.DataClass.AladinResponse;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Db.UserRepo;
import com.najunho.rememberbooks.Fragments.SearchReadDoneClickBottomSheetFragment;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Retrofit.AladinApiService;
import com.najunho.rememberbooks.Retrofit.RetrofitClient;
import com.najunho.rememberbooks.Util.OnSingleClickListener;
import com.najunho.rememberbooks.Util.SaveBookCheck;
import com.najunho.rememberbooks.Util.TimeHelper;
import com.najunho.rememberbooks.ViewModel.SearchBookViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBookActivity extends AppCompatActivity {
    private TextView tvSearch;
    private FrameLayout layoutLoading;
    private RecyclerView recyclerView;
    private ConcatAdapter concatAdapter;
    // 섹션별 어댑터
    private HeaderAdapter headerAdapter;
    private TabAdapter tabAdapter;
    private ContentAdapter contentAdapter;
    private String isbn13;
    private SearchResult bookItem; //tabAdapter에서 사용
    private int currentPosition = 0; // ★ 1. 현재 페이지 상태를 Activity가 직접 관리
    private SearchBookViewModel viewModel;
    private FirebaseAuth mAuth;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        tvSearch = findViewById(R.id.tvSearch);
        layoutLoading = findViewById(R.id.layout_loading);

        mAuth = FirebaseAuth.getInstance();

        isbn13 = getIntent().getStringExtra("isbn13");

        viewModel = new ViewModelProvider(this).get(SearchBookViewModel.class);
        viewModel.getAladinBookInfo(isbn13);
        recyclerView = findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getResult().observe(this, result -> {
            //getAladinBookInfo() load 완료 + 어댑터 초기 세팅 시에만
            if (result != null && recyclerView.getAdapter() == null) {
                bookItem = result;

                //header tv set
                tvSearch.setText(bookItem.title);

                //headerAdapter
                headerAdapter = new HeaderAdapter(result);

                //contentAdapter
                contentAdapter = new ContentAdapter(SearchBookActivity.this, result,position -> {
                    //fragment selected 시에 버튼 ui 변경
                    //recyclerview layout 계산 중에 fragment 변경 로직 발생하면 issue 발생
                    recyclerView.post(() -> updateCurrentPosition(position));
                });

                //tabAdapter
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
            }
        });

        viewModel.loadUser(mAuth.getCurrentUser().getUid());
        viewModel.getUser().observe(this, user -> {
            currentUser = user;
        });

        viewModel.getSaveSuccess().observe(this, event -> {
            SaveBookCheck check = event.getContentIfNotHandled(); //한번만 check 꺼낼 수 있음
            if (check != null){
                layoutLoading.setVisibility(View.GONE);
                if (check.getState() == MyBook.STATE_DONE){
                    Toast.makeText(this, "저장했습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);

                    // FLAG_ACTIVITY_NEW_TASK: 새로운 태스크 생성
                    // FLAG_ACTIVITY_CLEAR_TASK: 기존에 쌓여있던 모든 액티비티 스택을 제거
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();

                }else if (check.getState() == MyBook.STATE_READING){
                    Toast.makeText(SearchBookActivity.this, "독서를 시작합니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SearchBookActivity.this, ReadingRecordActivity.class);
                    Log.d("getSaveSuccess", "intent putExtra : book cover: " + check.getMyBook().getCover());
                    intent.putExtra("myBook", check.getMyBook());
                    intent.putExtra("beforeActivity", "searchBook");
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(SearchBookActivity.this, "내 서재에 보관되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SearchBookActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }else {
                Log.d("getSaveSuccess", "already observed event");
            }
        });

        //하단 고정 버튼 리스너 (독서완료, 시작, 보관)
        setupBottomButtons();

    }

    private void setupBottomButtons() {

        //독서 완료 버튼
        findViewById(R.id.read_done_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //버튼 클릭 로직 1초 대기
                SearchReadDoneClickBottomSheetFragment bottomSheet
                        = SearchReadDoneClickBottomSheetFragment.newInstance(bookItem, currentUser);
                bottomSheet.show(getSupportFragmentManager(), "ReadDoneBottomSheet");
            }
        });

        //독서 시작 버튼
        findViewById(R.id.read_start_btn).setOnClickListener(v -> {
            MyBook myBook = new MyBook(bookItem);
            myBook.setState(MyBook.STATE_READING); //독서 중
            myBook.setDateOfRead(TimeHelper.getCurrentDate());

            layoutLoading.setVisibility(View.VISIBLE);

            viewModel.saveFireStore(mAuth.getCurrentUser().getUid(), currentUser, myBook, bookItem.isbn13);
        });

        //보관중 버튼
        findViewById(R.id.store_btn).setOnClickListener(v -> {
            MyBook myBook = new MyBook(bookItem);
            myBook.setState(MyBook.STATE_SAVED); //보관중

            layoutLoading.setVisibility(View.VISIBLE);

            viewModel.saveFireStore(mAuth.getCurrentUser().getUid(), currentUser, myBook, bookItem.isbn13);
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
