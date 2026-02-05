package com.najunho.rememberbooks.Activity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.ViewModel.MysettingViewModel;

public class MySettingActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView changeNickName, logout, exit;
    private TextView changeFont, changeTheme, changeAboundOfComment;
    private TextView sendBug, sendMes, commonQ;
    private TextView appChannel, mesToDeveloper, appScore;
    private FirebaseAuth mAuth;
    private MysettingViewModel vm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        mAuth = FirebaseAuth.getInstance();
        vm = new ViewModelProvider(this).get(MysettingViewModel.class);

        changeNickName = findViewById(R.id.changeNickName);
        changeNickName.setOnClickListener(v->{
            showNicknameDialog();
        });
        vm.getNickname().observe(this, nickName -> {
            Toast.makeText(this, "닉네임 변경 성공", Toast.LENGTH_SHORT).show();
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(v->{
            mAuth.signOut();
            finishAffinity();
        });

        exit = findViewById(R.id.exit);
        exit.setOnClickListener(v->{
            vm.withdrawAccount();
        });
        vm.getIsExit().observe(this, b -> {
            if (b) {
                //삭제 성공
                // 2. 로그아웃 처리 및 앱 종료
                mAuth.signOut();
                finishAffinity(); // 모든 액티비티 스택을 비우고 종료
                System.exit(0);   // 프로세스 강제 종료
            }else {
                Toast.makeText(this, "보안을 위해 다시 로그인 후 시도해주세요.", Toast.LENGTH_LONG).show();

                // 실패 시 재로그인 화면으로 보내거나 로그아웃 처리
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        changeFont = findViewById(R.id.changeFont);
        changeFont.setOnClickListener(v->{
            //개발 예정
            Toast.makeText(this, "개발 중 입니다.", Toast.LENGTH_LONG).show();
        });

        changeTheme = findViewById(R.id.changeTheme);
        changeTheme.setOnClickListener(v->{
            //개발 예정
            Toast.makeText(this, "개발 중 입니다.", Toast.LENGTH_LONG).show();
        });

        changeAboundOfComment = findViewById(R.id.changeAboundOfComment);
        changeAboundOfComment.setOnClickListener(v->{
            vm.changeDisclosure(mAuth.getCurrentUser().getUid());
        });
        vm.getIsSuccess().observe(this, b->{
            if (b){
                //변경 성공
                Toast.makeText(this, "공개 범위 변경 성공", Toast.LENGTH_SHORT).show();
            }
        });

        sendBug = findViewById(R.id.sendBug);
        sendBug.setOnClickListener( v->{
        });

        sendMes = findViewById(R.id.sendMes);
        commonQ = findViewById(R.id.commonQ);

        appChannel = findViewById(R.id.appChannel);
        appChannel.setOnClickListener(v->{
            goToDeveloperWebsite();
        });

        mesToDeveloper = findViewById(R.id.mesToDeveloper);
        appScore = findViewById(R.id.appScore);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v->{
            finish();
        });
    }

    private void showNicknameDialog() {
        // 1. 레이아웃 인플레이터로 뷰 생성
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_nickname, null);
        EditText etNickname = dialogView.findViewById(R.id.et_nickname);

        // 2. AlertDialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("닉네임 변경");

        // 3. 완료 버튼 클릭 리스너
        builder.setPositiveButton("완료", (dialog, which) -> {
            String newNickname = etNickname.getText().toString().trim();

            if (newNickname.length() < 2) {
                Toast.makeText(this, "닉네임은 2자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            // DB 저장 로직 호출
            updateNicknameInDB(newNickname);
            dialog.dismiss();
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        // 4. 다이얼로그 띄우기
        builder.create().show();
    }

    private void updateNicknameInDB(String newNickname) {
        vm.changeNickName(mAuth.getCurrentUser().getUid(), newNickname);
        Log.d("updateNicknameInDB", "닉네임 변경 시도: " + newNickname);
    }

    private void goToDeveloperWebsite() {
        // 이동하고자 하는 웹사이트 주소
        String url = "https://sites.google.com/view/booklog1030/%ED%99%88";

        try {
            // 1. ACTION_VIEW 인텐트 생성
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            // 2. 브라우저 앱 실행
            startActivity(intent);
        } catch (Exception e) {
            // 웹 브라우저 앱이 없거나 URL 형식이 잘못된 경우 예외 처리
            Toast.makeText(this, "웹사이트를 열 수 없습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
