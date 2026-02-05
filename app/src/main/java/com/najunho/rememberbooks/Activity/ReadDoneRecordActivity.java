package com.najunho.rememberbooks.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.najunho.rememberbooks.Adapter.RecordDoneAdapter;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Db.RecordRepo;
import com.najunho.rememberbooks.Fragments.RecordDoneBottomSheetFragment;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.ViewModel.ReadDoneRecordViewModel;
import com.najunho.rememberbooks.ViewPagerAdpater.RecordDonePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReadDoneRecordActivity extends AppCompatActivity {
    private ReadDoneRecordViewModel viewModel;
    private ImageView ivBookCover;
    private TextView bookTitle, bookAuthor, bookScore, bookComment, talkWithAi;
    private RecyclerView recyclerView;
    private ViewPager2 viewPager;
    private Button btnSlide, btnPage;
    private RecordDoneAdapter adapter;
    private MyBook book;
    private List<Record> recordList; //추후 DB에서 가져옴
    private String isbn13;

    private static final String EXTRA_ISBN = "extra_isbn13";

    // 다른 곳에서 나를 호출할 때 사용할 전용 "통로" 생성
    public static Intent newIntent(Context context, String isbn13) {
        Intent intent = new Intent(context, ReadDoneRecordActivity.class);
        intent.putExtra(EXTRA_ISBN, isbn13);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_done);

        ivBookCover = findViewById(R.id.iv_book_cover);
        bookTitle = findViewById(R.id.bookTitle);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookScore = findViewById(R.id.bookScore);
        bookComment = findViewById(R.id.bookComment);
        talkWithAi = findViewById(R.id.talk_with_ai);

        viewPager = findViewById(R.id.read_log_viewPager);
        recyclerView = findViewById(R.id.read_log_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //intent로 isbn13 받아오기
        isbn13 = getIntent().getStringExtra(EXTRA_ISBN);
        if (isbn13.isEmpty()){
            Log.d("isbn13", "failed to get isbn13");
            finish();
        }

        viewModel = new ViewModelProvider(this).get(ReadDoneRecordViewModel.class);
        //Db에서 MyBook, Records Load
        viewModel.loadMyBook(isbn13);

        viewModel.getMyBook().observe(this, myBook -> {
            book = myBook;
            bookTitle.setText(myBook.getTitle());
            bookAuthor.setText(myBook.getAuthor());
            bookScore.setText(myBook.getScore() + " / 100");
            bookComment.setText(myBook.getComment());
            Glide.with(ReadDoneRecordActivity.this)
                    .load(myBook.getCover())
                    .placeholder(R.drawable.book_cover)
                    .into(ivBookCover);
        });

        viewModel.getRecordList().observe(this, records->{
            if (records == null || viewPager.getAdapter() != null || recyclerView.getAdapter() != null) {
                Log.d("getRecordList", "viewPager or recyclerView is not null");
                return;
            }

            //viewPager setup
            RecordDonePagerAdapter viewPagerAdapter = new RecordDonePagerAdapter(
                    ReadDoneRecordActivity.this,
                    records);
            viewPager.setAdapter(viewPagerAdapter);

            //recyclerview setup
            adapter = new RecordDoneAdapter(records, record -> {
                //Click event : Load BottomSheet
                RecordDoneBottomSheetFragment bottomSheet = RecordDoneBottomSheetFragment.newInstance(record);
                Log.d("onItemClick", record.toString());
                bottomSheet.show(getSupportFragmentManager(), "DoneBottomSheet");
            });
            recyclerView.setAdapter(adapter);
        });

        //btn tabs setup
        btnSlide = findViewById(R.id.btnSlideView);
        btnPage = findViewById(R.id.btnPageView);

        btnSlide.setOnClickListener(v-> {
            recyclerView.setVisibility(View.VISIBLE);
            btnSlide.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CBBBA0"))); // 활성화

            viewPager.setVisibility(View.GONE);
            btnPage.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E9E1D6"))); // 비활성화
        });

        btnPage.setOnClickListener(v-> {
            recyclerView.setVisibility(View.GONE);
            btnSlide.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E9E1D6"))); // 활성화

            viewPager.setVisibility(View.VISIBLE);
            btnPage.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CBBBA0"))); // 비활성화
        });

        talkWithAi.setOnClickListener(v->{
            Intent intent = new Intent(this, TalkWithAIActivity.class);
            intent.putExtra("isbn13" , book.getIsbn13());
            intent.putExtra("favoriteGenre", book.getCategory());
            intent.putExtra("currentBookTitle", book.getTitle());
            intent.putExtra("readingStatus", book.getComment());
            startActivity(intent);
        });
    }
}
