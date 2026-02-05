package com.najunho.rememberbooks.Retrofit;

import com.najunho.rememberbooks.DataClass.AladinResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladinApiService {
    @GET("ItemSearch.aspx")
    Call<AladinResponse> searchBook(
            @Query("ttbkey") String ttbkey,
            @Query("Query") String query,
            @Query("QueryType") String queryType,
            @Query("MaxResults") int maxResults,
            @Query("start") int start,
            @Query("SearchTarget") String searchTarget,
            @Query("output") String output,  // "js" 전달 예정
            @Query("Version") String version
    );

    // 2단계: 상품 조회 (선택한 책의 상세 정보 + 페이지 수 가져오기용)
    @GET("ItemLookUp.aspx")
    Call<AladinResponse> lookupBook(
            @Query("ttbkey") String ttbkey,
            @Query("ItemId") String isbn13,      // 선택한 책의 ISBN13 전달
            @Query("ItemIdType") String idType,  // "ISBN13"으로 고정
            @Query("output") String output, //js 로 고정
            @Query("Version") String version  // "20131101"
    );
}
