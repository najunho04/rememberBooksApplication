package com.najunho.rememberbooks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.najunho.rememberbooks.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.najunho.rememberbooks.CloudFunctions.CloudFuncManager;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Util.GoogleSignInCallback;
import com.najunho.rememberbooks.Util.GoogleSignInHelper;
import com.najunho.rememberbooks.Util.KakaoSignInHelper;
import com.najunho.rememberbooks.ViewModel.LoginViewModel;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout googleLogin, kakaoLogin;
    private LoginViewModel loginViewModel;
    private FrameLayout layoutLoading;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //<app check>
        // 1. Firebase 초기화
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // 2. App Check 인스턴스 가져오기 및 Play Integrity 설정
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        //firebaseAppCheck.installAppCheckProviderFactory(
                //DebugAppCheckProviderFactory.getInstance());
        firebaseAppCheck.installAppCheckProviderFactory(
                          PlayIntegrityAppCheckProviderFactory.getInstance());

        setContentView(R.layout.activity_login);

        kakaoLogin = findViewById(R.id.btn_kakao_login);
        googleLogin = findViewById(R.id.google_login);
        layoutLoading = findViewById(R.id.layout_loading);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        Log.d("KakaoKeyHash", com.kakao.sdk.common.util.Utility.INSTANCE.getKeyHash(this));

        loginViewModel.getLoginSuccess().observe(this, isLoginSuccess -> {
            if (isLoginSuccess) {
                Log.d("LoginActivity", "로그인 성공. DB 추가 완료했습니다.");
                // 메타데이터 저장까지 완벽히 끝난 후 화면 이동
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //kakao login
        kakaoLogin.setOnClickListener(v->{
            layoutLoading.setVisibility(View.VISIBLE);
            //context 의존이 필요한 최소한의 로직만 액티비티에서 진행
            KakaoSignInHelper.loginToKakao(this, new KakaoSignInHelper.OnKakaoLoginListener() {
                @Override
                public void onKakaoLoginSuccess(String kakaoToken) {
                    loginViewModel.getFireBaseTokenAndLogin(kakaoToken);
                }
                @Override
                public void onKakaoLoginError(String errorMessage) {
                    Log.e("loginToKakao", errorMessage);
                }
            });
        });

        //google login
        googleLogin.setOnClickListener(v-> {
            layoutLoading.setVisibility(View.VISIBLE);
            //context 의존이 필요한 최소한의 로직만 액티비티에서 진행
            GoogleSignInHelper.signInWithGoogleAsync(this, BuildConfig.GOOGLE_CLIENT_ID, new GoogleSignInCallback() {
                @Override
                public void onSuccess(@NotNull String idToken) {
                    loginViewModel.firebaseAuthWithGoogle(idToken);
                }

                @Override
                public void onError(@NotNull String errorMessage) {
                    layoutLoading.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 1. 현재 기기에 저장된 Firebase 유저 정보 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 2. 유저 정보가 null이 아니라면 (이미 로그인 된 상태)
        if (currentUser != null) {
            Log.d("LoginActivity", "user: " + currentUser.getUid());
            updateUI(currentUser);
        }
    }

    // 메인 화면으로 이동하는 메서드
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class); // 또는 MyPageActivity
            startActivity(intent);
            finish(); // 중요: 뒤로가기 눌렀을 때 다시 로그인 화면으로 오지 않도록 종료
        }
    }

}
