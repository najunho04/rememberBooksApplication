package com.najunho.rememberbooks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.Adapter.SearchBookListAdapter;
import com.najunho.rememberbooks.DataClass.AladinResponse;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Retrofit.AladinApiService;
import com.najunho.rememberbooks.Retrofit.RetrofitClient;
import com.najunho.rememberbooks.Util.BookCheckResult;
import com.najunho.rememberbooks.Util.BottomNavHelper;
import com.najunho.rememberbooks.ViewModel.SearchBookListViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBookListActivity extends AppCompatActivity {
    private ImageView searchBtn;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private ImageButton btnMenuSearch, btnMenuLibrary, btnMenuHome, btnMenuRecord, btnMenuMypage;
    private SearchBookListAdapter adapter;
    private List<SearchResult> searchBookList;
    private SearchBookListViewModel viewModel;
    private String isbn13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_booklist);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_bar), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // 최상위 레이아웃에 시스템 바만큼 패딩 적용
            v.setPadding(insets.left, insets.top, insets.right, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        searchBtn = findViewById(R.id.search_btn);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.rv_search_booklist);
        btnMenuSearch = findViewById(R.id.btn_menu_search);
        btnMenuLibrary = findViewById(R.id.btn_menu_library);
        btnMenuHome = findViewById(R.id.btn_menu_home);
        btnMenuRecord = findViewById(R.id.btn_menu_record);
        btnMenuMypage = findViewById(R.id.btn_menu_mypage);

        viewModel = new ViewModelProvider(this).get(SearchBookListViewModel.class);

        //하단 바 Intent 관리
        BottomNavHelper.setupBottomNav(this);

        searchBookList = new ArrayList<>();

        //recyclerview, adapter setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchBookListAdapter(searchBookList, position -> {
            Log.d("onClick", "success");
            String isbn13 = searchBookList.get(position).isbn13;
            //이미 등록한 책인지 확인
            viewModel.existBook(isbn13);
        });
        recyclerView.setAdapter(adapter);

        //search Observe
        viewModel.getResult().observe(this, searchResult -> {
            if (searchResult != null) {
                adapter.setSearchBookList(searchResult);
                Log.d("getResult", "observe succcess");
                searchBookList = searchResult;
            }
        });

        //event Observe : 새로운 책 선택 시 -> SearchBookActivity로 이동
        viewModel.getNavigateToDetail().observe(this, event -> {
            // 1. 여기서 '처음' 꺼낼 때만 isbn 값이 나오고,
            // 화면 회전 후 다시 올 때는 null이 나옵니다.
            BookCheckResult result = event.getContentIfNotHandled();
            Log.d("getNavigateToDetail", "observe..");

            if (result != null) {
                // 이제 result 안에는 isbn13과 isExist가 모두 들어있습니다!
                if (result.isExist) {
                    // 이미 있는 책인 경우
                    Toast.makeText(this, "이미 존재하는 책입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 새로 등록 가능한 책인 경우 화면 이동
                    Intent intent = new Intent(this, SearchBookActivity.class);
                    intent.putExtra("isbn13", result.isbn13);
                    startActivity(intent);
                }
            }else {
                //이미 1번 observe 한 event임
                Log.d("getNavigateToDetail", "already observed event");
            }
        });

        //search btn
        searchBtn.setOnClickListener(v->{
            String searchQuery = etSearch.getText().toString();
            if (searchQuery.isEmpty()){return;}

            viewModel.searchBookList(searchQuery);
        });
    }
}
