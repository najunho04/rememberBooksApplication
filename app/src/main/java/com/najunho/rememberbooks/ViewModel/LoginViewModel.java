package com.najunho.rememberbooks.ViewModel;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.najunho.rememberbooks.Activity.LoginActivity;
import com.najunho.rememberbooks.Activity.MainActivity;
import com.najunho.rememberbooks.CloudFunctions.CloudFuncManager;
import com.najunho.rememberbooks.DataClass.User;
import com.najunho.rememberbooks.Db.UserRepo;

public class LoginViewModel extends ViewModel {
    // 1. 로그인 성공 여부를 담는 데이터 (LiveData)
    private MutableLiveData<Boolean> _loginSuccess = new MutableLiveData<>(false);
    public LiveData<Boolean> loginSuccess = _loginSuccess;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public LiveData<Boolean> getLoginSuccess(){
        return _loginSuccess;
    }

    // 4. Firebase Authentication에 구글 계정 등록
    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AuthResult authResult = task.getResult();
                        FirebaseUser user = authResult.getUser();

                        if (user != null){
                            // 2. 기본 정보 가져오기 (FirebaseUser에서 직접 추출)
                            String uid = user.getUid();
                            String email = user.getEmail();
                            String nickname = user.getDisplayName(); // 구글 프로필 이름
                            Log.d("firebaseAuthWithGoogle", "login success: " + uid);
                            Log.d("firebaseAuthWithGoogle", "email: " + email);
                            Log.d("firebaseAuthWithGoogle", "nickname:" + nickname);

                            // 3. 신규 유저 여부 확인 (Google 로그인 시 매우 정확함)
                            boolean isNewUser = authResult.getAdditionalUserInfo().isNewUser();

                            // Auth 로그인 성공 후
                            if (isNewUser) {
                                // 1. 처음 가입한 유저인 경우에만 기본 메타데이터 저장
                                UserRepo.addUser(uid, new User(nickname, email), task2 -> {
                                    _loginSuccess.setValue(true);
                                });
                            } else {
                                // 2. 이미 가입된 유저라면 DB 저장 없이 바로 메인 화면으로 이동
                                _loginSuccess.setValue(true);
                            }
                        }
                    } else {
                        _loginSuccess.setValue(false);
                    }
                }).addOnFailureListener(e->{
                    Log.e("firebaseAuthWithGoogle", "login failed : ", e);
                });
    }

    public void getFireBaseTokenAndLogin(String kakaoToken){
        //kakaoToken -> AuthToken 교환 + user DB 저장
        CloudFuncManager.getFirebaseCustomToken(kakaoToken, new CloudFuncManager.OnFirebaseLoginUsingKakao() {
            @Override
            public void onLoginSuccess(String uid, String email, String nickname) {
                Log.d("getFirebaseCustomToken", "login success: " + uid);
                Log.d("getFirebaseCustomToken", "email: " + email);
                Log.d("getFirebaseCustomToken", "nickname:" + nickname);
                _loginSuccess.setValue(true);
            }

            @Override
            public void onLoginError(String errorMessage) {
                Log.e("getFirebaseCustomToken", errorMessage);
                _loginSuccess.setValue(false);
            }
        });
    }

}