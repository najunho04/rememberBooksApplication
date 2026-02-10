package com.najunho.rememberbooks.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.Adapter.CalendarAdapter;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Util.BottomNavHelper;
import com.najunho.rememberbooks.Util.CalendarUtil;
import com.najunho.rememberbooks.ViewModel.MyPageViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPageActivity extends AppCompatActivity {
    // UI Components
    private TextView cardInfo, cardInfo2, cardInfo3, tvMainHeader;
    private ImageView intentMySetting;
    private FrameLayout loadingLayout;
    private LinearLayout layoutHeader;
    private NestedScrollView mainScrollView;
    private ShapeableImageView img_book_cover, img_book_cover2, img_book_cover3;
    private RecyclerView rvCalendar;
    private CalendarAdapter calendarAdapter;
    private TextView tvMonthTitle; // "2026년 1월" 표시
    private ImageButton btnPrevMonth, btnNextMonth;
    private LinearLayout memberContainer;
    private Calendar currentCalendar = Calendar.getInstance(); // 현재 보고 있는 달

    // Data / Logic
    private MyPageViewModel vm;
    private Map<String, List<MyBook>> dailyBooksMap = new HashMap<>();
    private List<EventDay> events = new ArrayList<>();
    private User currentUser;
    private String uid;

    // 통계용 변수 (메서드 내 지역변수로 처리해도 되지만, 기존 구조 유지)
    private int totalRead = 0;
    private int totalReading = 0;
    private int totalSave = 0;
    private String readDoneUrl = null;
    private String readingUrl = null;
    private String readSaveUrl = null;

    //Main View 호출 변수
    private int loadedTaskCount = 0;
    private final int TOTAL_TASKS = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        // 1. 초기화 (View, ViewModel, Listener)
        initViews();
        initViewModel();

        // 2. Observer 등록 (생명주기 동안 한 번만 등록)
        observeData();

        // 3. 하단 바 설정
        BottomNavHelper.setupBottomNav(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 4. 데이터 갱신 요청 (화면 진입 시마다 최신 데이터 로드)
        if (uid != null) {
            vm.loadUser(uid);
            vm.loadDailyBooksMap(uid); //Map, myBookList observe trigger
            vm.loadTopReadCountUsers();
        }
    }

    private void initViews() {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cardInfo = findViewById(R.id.tv_card_info);
        cardInfo2 = findViewById(R.id.tv_card_info2);
        cardInfo3 = findViewById(R.id.tv_card_info3);
        tvMainHeader = findViewById(R.id.tv_main);

        img_book_cover = findViewById(R.id.iv_book_cover);
        img_book_cover2 = findViewById(R.id.iv_book_cover2);
        img_book_cover3 = findViewById(R.id.iv_book_cover3);

        memberContainer = findViewById(R.id.memberContainer);

        layoutHeader = findViewById(R.id.layout_header);
        mainScrollView = findViewById(R.id.main_scroll_view);
        loadingLayout = findViewById(R.id.loading_layout);

        layoutHeader.setVisibility(View.GONE);
        mainScrollView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);

        // 1. 캘린더 리사이클러뷰 설정
        rvCalendar = findViewById(R.id.rv_calendar); // XML에 RecyclerView 추가 필요
        tvMonthTitle = findViewById(R.id.tv_month_title);
        btnPrevMonth = findViewById(R.id.btn_prev);
        btnNextMonth = findViewById(R.id.btn_next);

        // Grid Layout 설정 (7열)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 7);
        rvCalendar.setLayoutManager(layoutManager);

        // 어댑터 초기 설정 (데이터는 아직 없음)
        calendarAdapter = new CalendarAdapter(
                new ArrayList<>(),
                new HashMap<>(),
                currentCalendar,
                (day, books) -> {
                    // [클릭 이벤트 처리]
                    if (books != null && !books.isEmpty()) {
                        // 기존 로직 그대로 사용 (ReadingRecordActivity 이동 등)
                        MyBook book = books.get(0);
                        if (book.getState() == MyBook.STATE_DONE){
                            Intent intent = ReadDoneRecordActivity.newIntent(this, book.getIsbn13());
                            startActivity(intent);
                        } else if(book.getState() == MyBook.STATE_READING){
                            Intent intent = new Intent(this, ReadingRecordActivity.class);
                            intent.putExtra("myBook", book);
                            intent.putExtra("beforeActivity", "myLibrary");
                            startActivity(intent);
                        }
                    }
                }
        );
        rvCalendar.setAdapter(calendarAdapter);

        // 2. 이전/다음 달 버튼 리스너
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            refreshCalendar();
        });
        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            refreshCalendar();
        });

        intentMySetting = findViewById(R.id.intent_my_setting);
        intentMySetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, MySettingActivity.class);
            startActivity(intent);
        });
    }

    // 3. 캘린더 새로고침 (달 이동 시 호출)
    private void refreshCalendar() {
        // 상단 타이틀 변경 ("2026년 2월")
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        tvMonthTitle.setText(year + "년 " + month + "월");

        // 날짜 리스트 생성
        List<Calendar> days = CalendarUtil.getDaysInMonth(currentCalendar);

        // 어댑터에 데이터 전달 (Map은 ViewModel에서 받아온 dailyBooksMap 사용)
        calendarAdapter.updateData(days, dailyBooksMap, currentCalendar);
    }

    private void initViewModel() {
        vm = new ViewModelProvider(this).get(MyPageViewModel.class);
    }

    private void checkAllUIReady() {
        loadedTaskCount++;
        if (loadedTaskCount >= TOTAL_TASKS) {
            // 모든 UI 로직이 끝났을 때만 호출
            showMainContent();
        }
    }

    private void showMainContent() {
        Log.d("checkAllUIReady", "showMainContent success");
        layoutHeader.setVisibility(View.VISIBLE);
        mainScrollView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    private void observeData() {
        // 1. 사용자 정보 업데이트
        vm.getUser().observe(this, user -> {
            if (user != null) {
                currentUser = user;
                tvMainHeader.setText(currentUser.getNickName() + "님 환영합니다");
            }
            Log.d("checkAllUIReady", "success1");
            checkAllUIReady();
        });

        // 2. 캘린더 데이터 업데이트
        vm.getDailyBooksMap().observe(this, map -> {
            dailyBooksMap = map;
            Log.d("Observer", "DailyBooksMap updated");
            // 맵 데이터가 들어오면 캘린더 갱신
            refreshCalendar();
            Log.d("checkAllUIReady", "success2");
            checkAllUIReady();
        });

        // 3. 상위 독서왕 리스트 업데이트
        vm.getUserList().observe(this, users -> {
            updateTopMembersUI(users);
            Log.d("checkAllUIReady", "success3");
            checkAllUIReady();
        });

        // 4. 내 책 통계 업데이트
        vm.getMyBookList().observe(this, books -> {
            updateStatsUI(books);
            Log.d("checkAllUIReady", "success4");
            checkAllUIReady();
        });
    }

    // --- UI Update Methods (Logic separated from Observer) ---

    //3번째 통계 UI
    private void updateTopMembersUI(List<User> users) {
        if (users == null) return;

        memberContainer.removeAllViews(); // 중복 방지를 위해 기존 뷰 제거
        LayoutInflater inflater = LayoutInflater.from(this);

        for (User user : users) {
            View itemView = inflater.inflate(R.layout.item_top_member, memberContainer, false);
            ImageView ivProfile = itemView.findViewById(R.id.ivUserProfile);
            TextView tvName = itemView.findViewById(R.id.tvUserName);

            tvName.setText(user.getNickName());

            Glide.with(this)
                    .load(R.drawable.user_v2)
                    .circleCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(ivProfile);

            memberContainer.addView(itemView);
        }
    }

    //1번 통계 UI
    private void updateStatsUI(List<MyBook> books) {
        if (books == null) return;

        // 초기화 (중복 누적 방지)
        totalRead = 0;
        totalReading = 0;
        totalSave = 0;
        readDoneUrl = null;
        readingUrl = null;
        readSaveUrl = null;

        for (MyBook book : books) {
            if (book.getState() == MyBook.STATE_DONE) {
                totalRead++;
                if (readDoneUrl == null) readDoneUrl = book.getCover();
            } else if (book.getState() == MyBook.STATE_READING) {
                totalReading++;
                if (readingUrl == null) readingUrl = book.getCover();
            } else {
                totalSave++;
                if (readSaveUrl == null) readSaveUrl = book.getCover();
            }
        }

        // UI 반영
        cardInfo.setText(totalRead + "권");
        cardInfo2.setText(totalReading + "권");
        cardInfo3.setText(totalSave + "권");

        Glide.with(this).load(readDoneUrl).placeholder(R.drawable.default_book_cover_v4)
                .transform(new CenterCrop(), new RoundedCorners(8)).into(img_book_cover);

        Glide.with(this).load(readingUrl).placeholder(R.drawable.default_book_cover_v4)
                .transform(new CenterCrop(), new RoundedCorners(8)).into(img_book_cover2);

        Glide.with(this).load(readSaveUrl).placeholder(R.drawable.default_book_cover_v4)
                .transform(new CenterCrop(), new RoundedCorners(8)).into(img_book_cover3);
    }
}