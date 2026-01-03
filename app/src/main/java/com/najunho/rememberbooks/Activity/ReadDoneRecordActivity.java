package com.najunho.rememberbooks.Activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.najunho.rememberbooks.Adapter.RecordDoneAdapter;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.Fragments.RecordDoneBottomSheetFragment;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.ViewPagerAdpater.RecordDonePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReadDoneRecordActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ViewPager2 viewPager;
    private Button btnSlide, btnPage;
    private RecordDoneAdapter adapter;
    private List<Record> recordList; //추후 DB에서 가져옴

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_done);

        recordList = new ArrayList<>();
        recordList = getTestData(recordList);

        viewPager = findViewById(R.id.read_log_viewPager);
        RecordDonePagerAdapter viewPagerAdapter = new RecordDonePagerAdapter(this, recordList);
        viewPager.setAdapter(viewPagerAdapter);

        //recyclerview setup
        recyclerView = findViewById(R.id.read_log_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecordDoneAdapter(recordList, new RecordDoneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Record record) {
                //bottomsheet 띄우기
                RecordDoneBottomSheetFragment bottomSheet = RecordDoneBottomSheetFragment.newInstance(record);
                Log.d("onItemClick", record.toString());
                bottomSheet.show(getSupportFragmentManager(), "DoneBottomSheet");
            }
        });

        recyclerView.setAdapter(adapter);

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
    }

    private List<Record> getTestData(List<Record> records){
        // 리스트 초기화
        records = new ArrayList<>();

        // 데이터 추가 (생성자가 있다고 가정하거나, 없을 경우 아래와 같이 작성)
        records.add(new Record(
                "1",
                "DAY 1",
                "2025.12.23",
                "삶이 있는 한 희망은 있다.",
                "오늘부터 독서를 시작하며 희망찬 다짐을 해본다.",
                10,
                25
        ));

        records.add(new Record(
                "2",
                "DAY 2",
                "2025.12.24",
                "어제보다 나은 내일은 내가 만든다.",
                "크리스마스 이브지만 멈추지 않고 15페이지를 읽었다.",
                26,
                40
        ));

        records.add(new Record(
                "3",
                "DAY 3",
                "2025.12.25",
                "가장 큰 위험은 아무것도 하지 않는 것이다.",
                "연휴에도 꾸준히 읽는 습관이 중요하다는 것을 깨달았다.",
                41,
                60
        ));

        return records;
    }
}
