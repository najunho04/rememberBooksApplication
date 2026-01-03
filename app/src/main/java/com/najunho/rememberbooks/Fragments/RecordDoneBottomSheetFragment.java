package com.najunho.rememberbooks.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class RecordDoneBottomSheetFragment extends BottomSheetDialogFragment {

    // Bundle로 전달받을 데이터 키값
    private static final String ARG_DAY = "day";
    private static final String ARG_DATE = "date";
    private static final String ARG_START_PAGE = "startPage";
    private static final String ARG_END_PAGE = "endPage";
    private static final String ARG_QUOTE = "quote";
    private static final String ARG_THOUGHT = "thought";
    private int startPage;
    private int endPage;

    // 1. static 생성자 (데이터 전달용)
    public static RecordDoneBottomSheetFragment newInstance(Record record) {
        RecordDoneBottomSheetFragment fragment = new RecordDoneBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, record.getDay());
        args.putString(ARG_DATE, record.getDate());
        args.putInt(ARG_START_PAGE, record.getStartPage());
        args.putInt(ARG_END_PAGE, record.getEndPage());
        args.putString(ARG_QUOTE, record.getQuote());
        args.putString(ARG_THOUGHT, record.getThought());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 해당 XML 파일을 인플레이트
        View view = inflater.inflate(R.layout.layout_bottom_sheet_record_done, container, false);

        // 뷰 초기화
        TextView tvDay = view.findViewById(R.id.tv_day);
        TextView tvDate = view.findViewById(R.id.et_date); // ID가 et_date여도 TextView이므로 캐스팅 주의
        TextView tvStartPage = view.findViewById(R.id.tv_start_page);
        TextView tvEndPage = view.findViewById(R.id.tv_end_page);
        TextView tvQuote = view.findViewById(R.id.et_quote);
        TextView tvThought = view.findViewById(R.id.et_thought);
        TextView tvBack = view.findViewById(R.id.tv_back);

        // 2. Bundle에서 데이터 꺼내서 세팅
        if (getArguments() != null) {
            startPage = getArguments().getInt(ARG_START_PAGE);
            endPage = getArguments().getInt(ARG_END_PAGE);

            Log.d("getArguments", "startPage: " + startPage);
            Log.d("getArguments", "endPage: " + endPage);

            tvDay.setText(getArguments().getString(ARG_DAY));
            tvDate.setText(getArguments().getString(ARG_DATE));
            tvStartPage.setText(String.valueOf(startPage));
            tvEndPage.setText(String.valueOf(endPage));
            tvQuote.setText(getArguments().getString(ARG_QUOTE));
            tvThought.setText(getArguments().getString(ARG_THOUGHT));
        }

        // 뒤로 가기 버튼 클릭 시 닫기
        tvBack.setOnClickListener(v -> dismiss());

        return view;
    }

    // 모서리 둥글게 처리를 위한 테마 적용
    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }
}