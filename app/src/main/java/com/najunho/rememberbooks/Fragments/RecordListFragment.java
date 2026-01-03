package com.najunho.rememberbooks.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.R;

public class RecordListFragment extends Fragment {

    // Bundle 키 값 정의
    private static final String ARG_RECORD = "record";
    private static final String ARG_POSITION = "position";
    private static final String ARG_LIST_SIZE = "list_size";

    // 팩토리 메서드: ViewPager2 어댑터에서 호출 시 사용
    public static RecordListFragment newInstance(Record record, int position, int listSize) {
        RecordListFragment fragment = new RecordListFragment();
        Bundle args = new Bundle();
        // Record 클래스는 반드시 Serializable 또는 Parcelable을 구현해야 합니다.
        args.putSerializable(ARG_RECORD, record);
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_LIST_SIZE, listSize);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_record_done, container, false);

        Log.d("RecordListFragment", "onCreateView");

        // 뷰 초기화
        TextView tvDay = view.findViewById(R.id.tv_day);
        TextView tvPageIndex = view.findViewById(R.id.tv_page_index);
        TextView tvDate = view.findViewById(R.id.et_date);
        TextView tvStartPage = view.findViewById(R.id.tv_start_page);
        TextView tvEndPage = view.findViewById(R.id.tv_end_page);
        TextView tvQuote = view.findViewById(R.id.et_quote);
        TextView tvThought = view.findViewById(R.id.et_thought);

        // Bundle 데이터 추출 및 반영
        if (getArguments() != null) {
            Record record = (Record) getArguments().getSerializable(ARG_RECORD);
            int position = getArguments().getInt(ARG_POSITION);
            int listSize = getArguments().getInt(ARG_LIST_SIZE);

            if (record != null) {
                // 상단 Day 정보 (예: DAY 1)
                tvDay.setText(record.getDay());

                // 우측 상단 페이지 인덱스 (예: 1 / 5)
                // 포지션은 0부터 시작하므로 +1 처리
                String pageText = (position + 1) + " / " + listSize;
                tvPageIndex.setText(pageText);

                // 나머지 상세 데이터 바인딩
                tvDate.setText(record.getDate());
                tvQuote.setText(record.getQuote());
                tvThought.setText(record.getThought());

                tvStartPage.setText(String.valueOf(record.getStartPage()));
                tvEndPage.setText(String.valueOf(record.getEndPage()));
            }
        }

        return view;
    }

}
