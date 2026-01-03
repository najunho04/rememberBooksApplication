package com.najunho.rememberbooks.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.Adapter.MyLibraryAdapter;
import com.najunho.rememberbooks.DataClass.MyLibraryBook;
import com.najunho.rememberbooks.R;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyLibraryAdapter adapter; // 라이브러리용 어댑터 (기존에 만든 것 활용)
    private List<MyLibraryBook> bookList;

    public static BookListFragment newInstance(int position) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putInt("tab_position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        // 레이아웃 인플레이트
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        int position = getArguments().getInt("tab_position");

        // 리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.rv_fragment_books);

        // 데이터 준비
        bookList = new ArrayList<>();
        MyLibraryBook book1 = new MyLibraryBook("데미안", "헤르만 헤세", "98", "알을 깨고 나오는 사람들의 이야기", "", "독서 완료");
        MyLibraryBook book2 = new MyLibraryBook("인간실격", "다자이 오사무", "98", "알을 깨고 나오는 사람들의 이야기", "", "독서 중");
        MyLibraryBook book3 = new MyLibraryBook("인간실격", "다자이 오사무", "98", "알을 깨고 나오는 사람들의 이야기", "", "보관 중"                                                                                                          );
        bookList.add(book1);
        bookList.add(book2);
        bookList.add(book3);

        loadDataByPosition(position);

        return view;
    }

    private void loadDataByPosition(int position) {
        List<MyLibraryBook> filteredBooks = new ArrayList<>();
        switch (position) {
            case 0 : // 전체
                filteredBooks.addAll(bookList);
                break;

            case 1 : // 완독 -> DB 연결 후에는 완독인 책들만 가져올 거임ㅇㅇ
                for (int i = 0; i < bookList.size(); i++){
                    if(bookList.get(i).getState().equals("독서 완료")) {
                        filteredBooks.add(bookList.get(i));
                    }
                }
                break;

            case 2 : // 독서중
                for (int i = 0; i < bookList.size(); i++){
                    if(bookList.get(i).getState().equals("독서 중")) {
                        filteredBooks.add(bookList.get(i));
                    }
                }
                break;

            case 3 : // 보관중
                for (int i = 0; i < bookList.size(); i++){
                    if(bookList.get(i).getState().equals("보관 중")) {
                        filteredBooks.add(bookList.get(i));
                    }
                }
                break;

        }
        // 어댑터 설정 및 레이아웃 매니저 확인
        if (recyclerView != null) {
            // XML에서 설정하지 않았다면 코드에서라도 지정해야 합니다.
            if (recyclerView.getLayoutManager() == null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            adapter = new MyLibraryAdapter(filteredBooks);
            recyclerView.setAdapter(adapter);
        }
    }
}
