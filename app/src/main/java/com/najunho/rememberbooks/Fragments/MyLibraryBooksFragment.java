package com.najunho.rememberbooks.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.Activity.ReadDoneRecordActivity;
import com.najunho.rememberbooks.Activity.ReadingRecordActivity;
import com.najunho.rememberbooks.Adapter.MyLibrary.MyLibraryAdapter;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Util.TimeHelper;

import java.util.ArrayList;
import java.util.List;

public class MyLibraryBooksFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyLibraryAdapter adapter; // 라이브러리용 어댑터 (기존에 만든 것 활용)
    private List<MyBook> bookList;
    private static final String ARG_TAB_POSITION = "tab_position";
    private static final String ARG_BOOK_LIST = "book_list";
    private int position;
    private long lastClickTime = 0;


    public static MyLibraryBooksFragment newInstance(int position, List<MyBook> bookList) {
        MyLibraryBooksFragment fragment = new MyLibraryBooksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_POSITION, position);
        // List를 ArrayList로 캐스팅하여 전달
        args.putSerializable(ARG_BOOK_LIST, new ArrayList<>(bookList));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        // 레이아웃 인플레이트
        View view = inflater.inflate(R.layout.fragment_my_library_book_list, container, false);
        if(getArguments() != null) {
            position = getArguments().getInt(ARG_TAB_POSITION);
            bookList = (List<MyBook>) getArguments().getSerializable(ARG_BOOK_LIST);
        }

        // 리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.rv_fragment_books);

        loadDataByPosition(position);

        return view;
    }

    private void loadDataByPosition(int position) {
        List<MyBook> filteredBooks = new ArrayList<>();
        switch (position) {
            case 0 : // 전체
                filteredBooks.addAll(bookList);
                break;

            case 1 : // 완독 -> DB 연결 후에는 완독인 책들만 가져올 거임ㅇㅇ
                for (int i = 0; i < bookList.size(); i++){
                    if(bookList.get(i).getState() == MyBook.STATE_DONE) {
                        filteredBooks.add(bookList.get(i));
                    }
                }
                break;

            case 2 : // 독서중
                for (int i = 0; i < bookList.size(); i++){
                    if(bookList.get(i).getState() == MyBook.STATE_READING) {
                        filteredBooks.add(bookList.get(i));
                    }
                }
                break;

            case 3 : // 보관중
                for (int i = 0; i < bookList.size(); i++){
                    if(bookList.get(i).getState() == MyBook.STATE_SAVED) {
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

            adapter = new MyLibraryAdapter(filteredBooks, new MyLibraryAdapter.OnClickListener() {
                @Override
                public void onClickReading(MyBook book) {
                    // 1초 이내 중복 클릭 방지 로직
                    if (SystemClock.uptimeMillis() - lastClickTime < 1000) return;
                    lastClickTime = SystemClock.uptimeMillis();

                    //Reading Activity로 이동
                    Log.d("MyLibraryAdapter", "onClickReading");
                    Intent intent = new Intent(requireContext(), ReadingRecordActivity.class);
                    intent.putExtra("myBook", book);
                    intent.putExtra("beforeActivity", "myLibrary");
                    startActivity(intent);
                }

                @Override
                public void onClickDoneRead(MyBook book) {
                    // 1초 이내 중복 클릭 방지 로직
                    if (SystemClock.uptimeMillis() - lastClickTime < 1000) return;
                    lastClickTime = SystemClock.uptimeMillis();

                    //Read_Done Activity로 이동
                    Log.d("MyLibraryAdapter", "onClickDoneRead");
                    // Fragment는 Context가 직접 없으므로 requireContext() 사용
                    Intent intent = ReadDoneRecordActivity.newIntent(requireContext(), book.getIsbn13());
                    startActivity(intent);
                }

                @Override
                public void onClickSaved(MyBook book) {
                    // 1초 이내 중복 클릭 방지 로직
                    if (SystemClock.uptimeMillis() - lastClickTime < 1000) return;
                    lastClickTime = SystemClock.uptimeMillis();

                    //Reading Activity로 이동
                    Log.d("MyLibraryAdapter", "onClickSaved");
                    book.setDateOfRead(TimeHelper.getCurrentDate());
                    Intent intent = new Intent(requireContext(), ReadingRecordActivity.class);
                    intent.putExtra("myBook", book);
                    intent.putExtra("beforeActivity", "myLibrary");
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }
}
