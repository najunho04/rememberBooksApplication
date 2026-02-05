package com.najunho.rememberbooks.Util;

import android.content.Context;

import android.content.Intent;
import android.util.Log;

import com.kakao.sdk.user.UserApiClient;
import com.najunho.rememberbooks.Activity.MainActivity;

import java.util.Collections;



public class KakaoSignInHelper {
    public interface OnKakaoLoginListener {
        void onKakaoLoginSuccess(String kakaoToken);
        void onKakaoLoginError(String errorMessage);
    }
    public static void loginToKakao(Context context, OnKakaoLoginListener listener) {
        // 카카오톡 설치 여부 확인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(context)) {
            UserApiClient.getInstance().loginWithKakaoTalk(context, (token, error) -> {
                if (error != null) {
                    Log.e("Kakao", "로그인 실패", error);
                    listener.onKakaoLoginError(error.getMessage());
                } else if (token != null) {
                    Log.i("Kakao", "로그인 성공! 액세스 토큰: " + token.getAccessToken());
                    listener.onKakaoLoginSuccess(token.getAccessToken());
                    // 다음 단계: Firebase 인증 및 Firestore 저장 로직 호출
                    // 4단계: Firebase 커스텀 인증 진행
                    //getFirebaseCustomToken(token.getAccessToken());
                }
                return null;
            });
        } else {
            // 카카오톡이 없으면 카카오 계정(웹뷰)으로 로그인 시도
            Log.d("Kakao", "카카오톡이 없음");
            listener.onKakaoLoginError("카카오톡이 없음");
        }
    }
    public static void getKakaoUserInfo() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e("Kakao", "사용자 정보 요청 실패", error);
            } else if (user != null) {
                // 카카오 고유 ID (문자열로 변환)
                String kakaoUid = String.valueOf(user.getId());

                // 이메일 및 프로필 추출
                String email = user.getKakaoAccount().getEmail() == null ? user.getKakaoAccount().getEmail() : "no email";
                String nickname = user.getKakaoAccount().getProfile().getNickname();

                Log.d("Kakao", "닉네임: " + nickname);

                // 다음 단계: Firebase 인증 및 Firestore 저장 로직 호출
                //processFirebaseLogin(kakaoUid, email, nickname);
            }
            return null;
        });
    }


}
