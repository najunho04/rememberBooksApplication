package com.najunho.rememberbooks.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.najunho.rememberbooks.CloudFunctions.CloudFuncManager;
import com.najunho.rememberbooks.Fragments.SummaryListFragment;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Util.AssetUtils;

import io.noties.markwon.Markwon;

public class TalkWithAIActivity extends AppCompatActivity {
    private TextView tvAiResponse, tvUserQuestion, tvExampleQ1, tvExampleQ2, tvExampleQ3;
    private EditText etPrompt;
    private ImageButton btnSideView;
    private NestedScrollView scrollView;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private Markwon markwon;
    private GenerativeModel gm;
    private GenerativeModelFutures model;
    private String isbn13;
    private String favoriteGenre;
    private String currentBookTitle;
    private String readingStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_with_ai);

        isbn13 = getIntent().getStringExtra("isbn13");
        favoriteGenre = getIntent().getStringExtra("favoriteGenre");
        currentBookTitle = getIntent().getStringExtra("currentBookTitle");
        readingStatus = getIntent().getStringExtra("readingStatus");

        if(isbn13 == null){
            isbn13 = "";
        }

        // 1. Markwon 인스턴스 생성
        markwon = Markwon.create(this);

        btnSideView = findViewById(R.id.btn_side_view);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        tvUserQuestion = findViewById(R.id.tvUserQuestion);
        tvExampleQ1 = findViewById(R.id.example_q1);
        tvExampleQ2 = findViewById(R.id.example_q2);
        tvExampleQ3 = findViewById(R.id.example_q3);
        etPrompt = findViewById(R.id.etPrompt);
        btnSend = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);

        tvExampleQ1.setOnClickListener(v->{
            etPrompt.setText(tvExampleQ1.getText());
            btnSend.callOnClick();
        });
        tvExampleQ2.setOnClickListener(v->{
            etPrompt.setText(tvExampleQ2.getText());
            btnSend.callOnClick();
        });
        tvExampleQ3.setOnClickListener(v->{
            etPrompt.setText(tvExampleQ3.getText());
            btnSend.callOnClick();
        });


        btnSend.setOnClickListener(v->{
            String userPrompt = etPrompt.getText().toString().trim();
            if (userPrompt.isEmpty()){
                return;
            }
            tvUserQuestion.setText(userPrompt);

            scrollView.smoothScrollTo(0, 0);

            // 로딩 시작
            progressBar.setVisibility(View.VISIBLE);
            btnSend.setEnabled(false); //클릭 비활성화
            etPrompt.setText("");
            tvAiResponse.setText("답변을 생성 중 입니다.");

            CloudFuncManager.callReadingCoach(userPrompt, isbn13, favoriteGenre, currentBookTitle, readingStatus)
                    .addOnSuccessListener(answer -> {
                        // answer는 이미 Task에서 가공된 String(AI 답변)입니다.
                        Log.d("Activity", "AI 답변: " + answer);

                        // UI 업데이트 (TextView에 띄우기 등)
                        markwon.setMarkdown(tvAiResponse, answer);
                        progressBar.setVisibility(View.GONE);
                        btnSend.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        // 네트워크 에러나 서버 에러 처리
                        Log.e("Activity", "호출 실패: " + e);
                        Toast.makeText(this, "AI와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnSend.setEnabled(true);
                    });
        });

        btnSideView.setOnClickListener(v->{
            SummaryListFragment fragment = SummaryListFragment.newInstance(isbn13);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // 들어올 때
                            R.anim.slide_out_right, // 나갈 때 (상대방)
                            R.anim.slide_in_right,  // 뒤로가기로 다시 올 때
                            R.anim.slide_out_right  // 뒤로가기로 나갈 때
                    )
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null) // 뒤로가기 버튼 지원
                    .commit();
        });
    }
}
