package com.najunho.rememberbooks.Fragments;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.ViewModel.RecordViewModel;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;


public class RecordBottomSheetFragment extends BottomSheetDialogFragment {
    private RecordViewModel recordViewModel;
    private static final String ARG_ID = "id";
    private static final String ARG_DAY = "day";
    private static final String ARG_DATE = "date";
    private static final String ARG_START_PAGE = "startPage";
    private static final String ARG_END_PAGE = "endPage";
    private static final String ARG_QUOTE = "quote";
    private static final String ARG_THOUGHT = "thought";
    private static final String ARG_STATE = "state";
    private String idData;
    private String dayData;
    private String dateData;
    private int startPageData;
    private int endPageData;
    private String quoteData;
    private String thoughtData;
    private String stateData;


    private EditText etDate, etStartPage, etEndPage, etQuote, etThought;
    private TextView tvDelete, tvSave, tvDay;

    public static RecordBottomSheetFragment newInstance(Record record, String state) {
        RecordBottomSheetFragment fragment = new RecordBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, record.getId());
        args.putString(ARG_DAY, record.getDay());
        args.putString(ARG_DATE, record.getDate());
        args.putInt(ARG_START_PAGE, record.getStartPage());
        args.putInt(ARG_END_PAGE, record.getEndPage());
        args.putString(ARG_QUOTE, record.getQuote());
        args.putString(ARG_THOUGHT, record.getThought());
        args.putString(ARG_STATE, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            idData = getArguments().getString(ARG_ID);
            dayData = getArguments().getString(ARG_DAY);
            dateData = getArguments().getString(ARG_DATE);
            startPageData = getArguments().getInt(ARG_START_PAGE);
            endPageData = getArguments().getInt(ARG_END_PAGE);
            quoteData = getArguments().getString(ARG_QUOTE);
            thoughtData = getArguments().getString(ARG_THOUGHT);
            stateData = getArguments().getString(ARG_STATE);
        }

        // XML 레이아웃 인플레이트
        View view = inflater.inflate(R.layout.layout_bottom_sheet_record, container, false);

        //부모 activity가 가지고 있는 viewmodel 가져오기
        recordViewModel = new ViewModelProvider(requireActivity()).get(RecordViewModel.class);


        // 뷰 초기화
        tvDay = view.findViewById(R.id.tv_day);
        tvDelete = view.findViewById(R.id.tv_delete);
        tvSave = view.findViewById(R.id.tv_save);

        etDate = view.findViewById(R.id.et_date);
        etStartPage = view.findViewById(R.id.et_start_page);
        etEndPage = view.findViewById(R.id.et_end_page);
        etQuote = view.findViewById(R.id.et_quote);
        etThought = view.findViewById(R.id.et_thought);

        //UI 세팅
        tvDay.setText(dayData);

        if(Objects.equals(stateData, "edit")){
            etDate.setText(dateData);
            etStartPage.setText(String.valueOf(startPageData));
            etEndPage.setText(String.valueOf(endPageData));
            etQuote.setText(quoteData);
            etThought.setText(thoughtData);
            tvSave.setText("수정");
            etStartPage.setEnabled(false);
        }else {
            etStartPage.setText(String.valueOf(startPageData));
            etStartPage.setEnabled(false);
            tvSave.setText("저장");
        }

        // 삭제 버튼 클릭
        tvDelete.setOnClickListener(v -> {
            // 삭제 로직 추가 (예: DB 삭제)
            Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // 저장 버튼 클릭
        tvSave.setOnClickListener(v -> {
            saveData();
        });

        return view;
    }

    // 데이터 저장 로직
    private void saveData() {
        String date = etDate.getText().toString();
        String startPageString = etStartPage.getText().toString();
        String endPageString = etEndPage.getText().toString();
        String quote = etQuote.getText().toString();
        String thought = etThought.getText().toString();

        int startPage;
        int endPage;
        try {
            startPage = Integer.parseInt(startPageString);
        } catch (NumberFormatException e) {
            startPage = 0; // 숫자가 아니거나 비어있을 때 기본값
        }

        try {
            endPage = Integer.parseInt(endPageString);
        } catch (NumberFormatException e) {
            endPage = 0; // 숫자가 아니거나 비어있을 때 기본값
        }

        //나중에 날짜 입력 칸 만들 예정. 현재는 텍스트 뷰로 대체
        if (date.isEmpty()) {
            Toast.makeText(getContext(), "날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startPage > endPage){
            Toast.makeText(getContext(), "시작 페이지가 종료 페이지보다 큽니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Record record = new Record(idData, dayData, date, quote, thought, startPage, endPage);
        Log.d("endPage", "endPage:" + record.getEndPage());

        if(Objects.equals(stateData, "edit")){
            recordViewModel.updateRecord(record);
            Toast.makeText(getContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
            dismiss(); // 다이얼로그 닫기
        }else {
            recordViewModel.addRecord(record);
            Toast.makeText(getContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
            dismiss(); // 다이얼로그 닫기
        }
    }

    // BottomSheet의 배경을 투명하게 하거나 스타일을 지정하고 싶을 때 사용
    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }
}