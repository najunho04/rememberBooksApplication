package com.najunho.rememberbooks.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.Activity.MainActivity;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Db.UserRepo;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Util.SaveBookCheck;
import com.najunho.rememberbooks.Util.TimeHelper;
import com.najunho.rememberbooks.ViewModel.SearchBookViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SearchReadDoneClickBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String ARG_SEARCH_RESULT = "search_result";
    private static final String ARG_USER_DATA = "user_data";
    private SearchResult searchResult;
    private TextView saveBtn;
    private EditText etComment, etScore;
    private TextView tvEndPage, tvDate;
    private FrameLayout layoutLoading;
    private Slider scoreSlider;
    private FirebaseAuth mAuth;
    private User currentUser;
    private SearchBookViewModel vm;


    public static SearchReadDoneClickBottomSheetFragment newInstance(SearchResult data, User user) {
        SearchReadDoneClickBottomSheetFragment fragment = new SearchReadDoneClickBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_RESULT, data);
        args.putSerializable(ARG_USER_DATA, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 해당 XML 파일을 인플레이트
        View view = inflater.inflate(R.layout.layouy_bottom_sheet_read_done_click_event, container, false);

        mAuth = FirebaseAuth.getInstance();
        tvDate = view.findViewById(R.id.tv_date);
        etComment = view.findViewById(R.id.et_comment);
        etScore = view.findViewById(R.id.et_score);
        tvEndPage = view.findViewById(R.id.tv_end_page);
        scoreSlider = view.findViewById(R.id.scoreSlider);
        layoutLoading = view.findViewById(R.id.layout_loading);

        //vm 초기화, observe
        vm = new ViewModelProvider(requireActivity()).get(SearchBookViewModel.class);
        vm.getSaveSuccess().observe(requireActivity(), event -> {
            SaveBookCheck check = event.getContentIfNotHandled();
            if (check != null){
                if (check.getState() == MyBook.STATE_DONE){
                    layoutLoading.setVisibility(View.GONE);
                    dismiss();
                }
            }
        });

        //Bundle 값 받기
        if(getArguments()!=null){
            searchResult = (SearchResult) getArguments().getSerializable(ARG_SEARCH_RESULT);
            currentUser = (User) getArguments().getSerializable(ARG_USER_DATA);
            Log.d("SearchReadDoneClickBottomSheetFragment", "getArguments");
            tvEndPage.setText(String.valueOf(searchResult.subInfo.itemPage));
        }

        //Slider 변경 감지 -> editText 변경
        scoreSlider.addOnChangeListener((slider, value, fromUser) -> {
            // value는 float이므로 (예: 80.0) 정수로 변환 (예: 80)
            int score = (int) value;
            etScore.setText(String.valueOf(score));
        });

        //editView 텍스트 변경 감지 -> Slider 변경
        etScore.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String input = s.toString();
                    if (!input.isEmpty()) {
                        int val = Integer.parseInt(input);
                        if (val >= 0 && val <= 100) {
                            scoreSlider.setValue((float) val);
                        }
                    }
                } catch (NumberFormatException e) {
                    // 숫자가 아닌 입력이 들어올 경우 처리
                    Log.e("addTextChangedListener", "it is not int type");
                }
            }
            // beforeTextChanged, onTextChanged는 빈칸으로 둠
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        //날짜 로직
        tvDate.setOnClickListener(v-> {
            TimeHelper.openDatePicker(tvDate, requireContext());
        });

        //save 로직
        saveBtn = view.findViewById(R.id.tv_save);
        saveBtn.setOnClickListener(v->{
            String readDate = tvDate.getText().toString();
            String comment = etComment.getText().toString();
            String scoreText = etScore.getText().toString();
            if (readDate.isEmpty() || comment.isEmpty() || scoreText.isEmpty()){
                Toast.makeText(requireContext(), "입력칸을 확인해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            int score = Integer.parseInt(scoreText);

            MyBook myBook = new MyBook(searchResult);
            myBook.setState(MyBook.STATE_DONE);
            myBook.setDateOfRead(readDate);
            myBook.setComment(comment);
            myBook.setScore(score);
            myBook.setReadPage(myBook.getPage());

            layoutLoading.setVisibility(View.VISIBLE);

            vm.saveFireStore(mAuth.getCurrentUser().getUid(), currentUser, myBook, searchResult.isbn13);
        });

        return view;
    }

}
