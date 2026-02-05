package com.najunho.rememberbooks.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://www.aladin.co.kr/ttb/api/";
    private static Retrofit retrofit = null;

    // 싱글톤 인스턴스 반환 메서드
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기
                    .build();
        }
        return retrofit;
    }

    // 서비스 인터페이스 생성을 편리하게 하기 위한 제네릭 메서드
    public static <T> T createService(Class<T> serviceClass) {
        return getClient().create(serviceClass);
    }
}