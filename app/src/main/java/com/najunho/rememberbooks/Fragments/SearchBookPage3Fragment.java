package com.najunho.rememberbooks.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.R;

import java.text.DecimalFormat;

public class SearchBookPage3Fragment extends Fragment {

    private static final String ARG_BOOK = "book";
    private SearchResult book;

    public static SearchBookPage3Fragment newInstance(SearchResult book) {
        SearchBookPage3Fragment fragment = new SearchBookPage3Fragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_book_page3, container, false);

        // 1. 뷰 초기화 (XML ID 연결)
        TextView tvTitle = view.findViewById(R.id.tv_detail_title);
        TextView tvAuthor = view.findViewById(R.id.tv_detail_author);
        TextView tvPubDate = view.findViewById(R.id.tv_detail_publish_date);
        TextView tvIsbn = view.findViewById(R.id.tv_isbn13);
        TextView tvPrice = view.findViewById(R.id.tv_sale);
        TextView tvStock = view.findViewById(R.id.tv_stockState);
        TextView tvLink = view.findViewById(R.id.tv_link);

        // 2. 데이터 가져오기 및 UI 적용
        if (getArguments() != null) {
            book = (SearchResult) getArguments().getSerializable(ARG_BOOK);

            if (book != null) {
                Log.d("SearchBookPage3Fragment", "Book Title: " + book.title);

                // 기본 텍스트 세팅
                tvTitle.setText(book.title);
                tvAuthor.setText(book.author);
                tvPubDate.setText(book.pubDate);
                tvIsbn.setText(book.isbn13);

                // [가공 1] 판매가 : 천단위 콤마 포맷 적용 (예: 15,000원)
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(book.priceSales) + "원";
                tvPrice.setText(formattedPrice);

                // [가공 2] 재고상태 : 값이 비어있으면 "재고 있음", 아니면 해당 상태 표시
                if (book.stockStatus == null || book.stockStatus.isEmpty()) {
                    tvStock.setText("재고 있음");
                } else {
                    tvStock.setText(book.stockStatus);
                }

                // [가공 3] 링크 : URL 표시 및 클릭 시 브라우저 이동
                tvLink.setText(book.link);
                tvLink.setOnClickListener(v -> {
                    if (book.link != null && !book.link.isEmpty()) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.link));
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "링크를 열 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        return view;
    }
}