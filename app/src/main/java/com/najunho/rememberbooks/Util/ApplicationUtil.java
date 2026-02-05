package com.najunho.rememberbooks.Util;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.common.KakaoSdk;

public class ApplicationUtil extends Application {
    private static FirebaseFirestore firestore;

    @Override
    public void onCreate(){
        super.onCreate();

        // Firestore 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance();

        // 카카오 SDK 초기화
        KakaoSdk.init(this, "7880e7eef358bcae3a0da4463da6ed4c");

    }

    // 어디서든 DB에 접근할 수 있는 정적 메서드
    public static FirebaseFirestore getFirestore() {
        return firestore;
    }
}
