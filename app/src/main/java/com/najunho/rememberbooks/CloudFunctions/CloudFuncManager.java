package com.najunho.rememberbooks.CloudFunctions;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Db.UserRepo;

import java.util.HashMap;
import java.util.Map;

public class CloudFuncManager {
    public interface OnFirebaseLoginUsingKakao{
        void onLoginSuccess(String uid, String email, String nickname);
        void onLoginError(String errorMessage);
    }

    // Firebase Functions 호출 준비
    public static void getFirebaseCustomToken(String kakaoAccessToken, OnFirebaseLoginUsingKakao listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("token", kakaoAccessToken); // 백엔드 interface KakaoRequest { token: string } 대응

        // 2. 백엔드 함수 호출
        mFunctions
                .getHttpsCallable("verifyKakaoToken")
                .call(data)
                .addOnSuccessListener(result -> {
                    // 1. 백엔드 응답에서 데이터 추출
                    Map<String, Object> responseData = (Map<String, Object>) result.getData();
                    String customToken = (String) responseData.get("firebaseToken");

                    // 여기서 선언된 변수들은 이 블록 내부의 내부(inner) 리스너에서도 사용 가능!
                    String email = (String) responseData.get("email");
                    String nickname = (String) responseData.get("nickname");

                    if (email == null) email = "no email";

                    // 2. 추출한 토큰으로 로그인 시도 (여기서 바로 실행)
                    String finalEmail = email;
                    mAuth.signInWithCustomToken(customToken)
                            .addOnSuccessListener(authResult -> {

                                String uid = authResult.getUser().getUid();

                                // Auth 로그인 성공 후
                                if (authResult.getAdditionalUserInfo().isNewUser()) {
                                    // 1. 처음 가입한 유저인 경우에만 기본 메타데이터 저장
                                    UserRepo.addUser(uid, new User(nickname, finalEmail), task -> {
                                        listener.onLoginSuccess(uid, finalEmail, nickname);
                                    });
                                } else {
                                    // 2. 이미 가입된 유저라면 DB 저장 없이 바로 메인 화면으로 이동
                                    listener.onLoginSuccess(uid, finalEmail, nickname);
                                }
                            })
                            .addOnFailureListener(e -> {
                                // 로그인 실패 (토큰 만료 등)
                                listener.onLoginError(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    // Functions 호출 실패 (네트워크 오류, 서버 오류 등)
                    listener.onLoginError(e.getMessage());
                });
    }

    public static Task<String> callReadingCoach(String message, String isbn, String favoriteGenre, String currentBookTitle, String readingStatus) {
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("asia-northeast3");
        // 백엔드로 보낼 데이터 준비
        Map<String, Object> data = new HashMap<>();
        data.put("isbn13", isbn);
        data.put("userMessage", message);
        data.put("userName", "나준호");
        data.put("favoriteGenre", favoriteGenre);
        data.put("currentBookTitle", currentBookTitle);

        return mFunctions
                .getHttpsCallable("getGeminiResponse") // 함수 이름 일치해야 함
                .call(data)
                .continueWith(task -> {
                    Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                    Log.d("callReadingCoach", "result: " + result.get("answer"));
                    return (String) result.get("answer");
                });
    }


}
