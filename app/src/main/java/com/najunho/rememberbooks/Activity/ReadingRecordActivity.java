package com.najunho.rememberbooks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.najunho.rememberbooks.Adapter.RecordAdapter;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.Db.MyBookRepo;
import com.najunho.rememberbooks.Db.RecordRepo;
import com.najunho.rememberbooks.Fragments.RecordBottomSheetFragment;
import com.najunho.rememberbooks.Fragments.RecordDoneBottomSheetFragment;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.Util.BottomNavHelper;
import com.najunho.rememberbooks.Util.OnSingleClickListener;
import com.najunho.rememberbooks.Util.TimeHelper;
import com.najunho.rememberbooks.ViewModel.RecordViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingRecordActivity extends AppCompatActivity {
    private TextView bookTitle, bookAuthor, bookDateOfRead, bookRemainingPage, textRemainingPage, bookRemainingPage2;
    private TextView remainLabel, oneLineLabel, scoreLabel;
    private ImageView ivBookCover;
    private ProgressBar readProgressBar;
    private EditText oneLineEdit, editScore;
    private TextView saveBtn;
    private Button addItemuButton;
    private Slider scoreSlider;
    private RecordViewModel recordVM;
    private RecyclerView rv;
    private RecordAdapter recordAdapter;
    private int itemCount = 0;
    private int lastEndPage = 0;
    private boolean isReadDone = false;
    private boolean isExistBook = false;
    private int allpage = -1;
    private String isbn13;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_ing);
        mAuth = FirebaseAuth.getInstance();
        Log.d("getUid", "uid: " + mAuth.getCurrentUser().getUid());


        bookTitle = findViewById(R.id.book_title);
        bookAuthor = findViewById(R.id.book_author);
        bookDateOfRead = findViewById(R.id.book_date_of_read);
        bookRemainingPage = findViewById(R.id.book_remaining_page);
        textRemainingPage = findViewById(R.id.text_remaining_page);
        bookRemainingPage2 = findViewById(R.id.book_remaining_page2);
        ivBookCover = findViewById(R.id.iv_book_cover);
        readProgressBar = findViewById(R.id.readProgressBar);
        scoreSlider = findViewById(R.id.scoreSlider);

        remainLabel = findViewById(R.id.remainLabel);
        oneLineLabel = findViewById(R.id.oneLineLabel);
        scoreLabel = findViewById(R.id.scoreLabel);

        addItemuButton = findViewById(R.id.add_item_button);
        saveBtn = findViewById(R.id.save_btn);
        oneLineEdit = findViewById(R.id.oneLineEdit);
        editScore = findViewById(R.id.editScore);

        //하단 바 Intent 관리
        BottomNavHelper.setupBottomNav(this);

        recordVM = new ViewModelProvider(this).get(RecordViewModel.class);

        //ui setup
        // 1. Intent로 넘겨받은 데이터가 있는지 확인
        MyBook bookFromIntent = (MyBook) getIntent().getSerializableExtra("myBook");
        String beforeActivity = getIntent().getStringExtra("beforeActivity");

        // [경로 A] 즉시 UI 세팅 (네트워크 대기 없음) (검색창에서 온 책)
        if ("searchBook".equals(beforeActivity)) {
            setupUI(bookFromIntent);
            isExistBook = true;
        }
        // [경로 B] 즉시 UI 세팅 + State 1로 변경 (내 서재 보관중 책)
        else if ("myLibrary".equals(beforeActivity) && bookFromIntent.getState() == MyBook.STATE_SAVED){
            setupUI(bookFromIntent);
            recordVM.updateState(bookFromIntent.getIsbn13()); //isUpdateSuccess observe trigger
        }
        // [경로 C] isbn13으로 DB 조회 + records 로드 (내 서재 독서 중 책)
        else if ("myLibrary".equals(beforeActivity) && bookFromIntent.getState() == MyBook.STATE_READING) {
            loadMyBookFromIsbn13(mAuth.getCurrentUser().getUid(), bookFromIntent.getIsbn13());
            isExistBook = true;
        } else {
            // [경로 D] 메인에서 진입 시: DB에서 최근 MyBook 로드 + records 로드
            loadLatestBookFromFirestore();
        }

        //어댑터 세팅
        rv = findViewById(R.id.rv_record);
        rv.setLayoutManager(new LinearLayoutManager(this));
        recordAdapter = new RecordAdapter(new RecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Record record) {
                //클릭 로직
                RecordDoneBottomSheetFragment bottomSheet = RecordDoneBottomSheetFragment.newInstance(record);
                Log.d("onItemClick", record.toString());
                bottomSheet.show(getSupportFragmentManager(), "DoneBottomSheet");
            }

            @Override
            public void onEditClick(Record record, int position) {
                //수정 로직
                RecordBottomSheetFragment bottomSheet = RecordBottomSheetFragment.newInstance(record, "edit", isbn13);
                bottomSheet.show(getSupportFragmentManager(), "EditBottomSheet");
            }

            @Override
            public void onDeleteClick(Record record, int position) {
                //삭제 로직
                boolean isLastItem = (position == recordAdapter.getItemCount() - 1);
                if (isLastItem){
                    recordVM.deleteRecord(record, isbn13);
                    itemCount--;
                }else {
                    Toast.makeText(ReadingRecordActivity.this, "마지막 기록만 삭제할 수 있습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rv.setAdapter(recordAdapter);

        //recordVM Observe
        recordVM.isUpdateSuccess().observe(this, success -> {
            if (success == true){
                Log.d("isUpdateSuccess", "success");
                isExistBook = true;
            }
        });

        recordVM.getErrorMessage().observe(this, error ->{
            Log.e("getErrorMessage", error);
            Toast.makeText(this, "예상치 못한 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
        });

        //MyBook Load Observe
        recordVM.getMyBook().observe(this, book -> {
            if (book == null) {
                isExistBook = false;
                oneLineEdit.setVisibility(View.GONE);
                editScore.setVisibility(View.GONE);
                oneLineLabel.setVisibility(View.GONE);
                scoreLabel.setVisibility(View.GONE);
                Toast.makeText(this, "책을 먼저 등록해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            updateUI(book); // 공통 UI 세팅 호출
            Log.d("getRecords", "getMyBook observe start");
            recordVM.getRecords(book.getIsbn13());
            isExistBook = true;
        });

        //records Observe
        recordVM.getRecordList().observe(this, records -> {
            //myBook data 없을때
            if (records==null){
                Log.d("getRecords", "getRecordList null ");
                return;
            }
            Log.d("getRecords", "getRecordList observe start");
            recordAdapter.submitList(new ArrayList<>(records));

            //myBook o, records 없을 때
            if (records.isEmpty()){
                lastEndPage = 0;
                int readPercent = getReadPercent(allpage, lastEndPage);
                bookRemainingPage.setText("남은 독서량: " + (100 - readPercent) + "%");
                bookRemainingPage2.setText("남은 페이지: " + lastEndPage + "/" + allpage + "p");
                textRemainingPage.setText(readPercent + "%");
                readProgressBar.setProgress(readPercent);
                return;
            }

            Log.d("getRecordList", "allPage: " + allpage);
            Log.d("getRecordList", "lastEndPage: " + lastEndPage);
            itemCount = records.size();

            //lastPage 계산
            int lastIndex = records.size() -1;
            if (lastIndex >= 0){
                lastEndPage = records.get(lastIndex).getEndPage();
                Log.d("getRecordList", "lastEndPage: " + lastEndPage);
            }

            //UI 재세팅
            bookRemainingPage.setVisibility(View.VISIBLE);
            int readPercent = getReadPercent(allpage, lastEndPage);
            bookRemainingPage.setText("남은 독서량: " + (100 - readPercent) + "%");
            bookRemainingPage2.setText("남은 페이지: " + lastEndPage + "/" + allpage + "p");
            textRemainingPage.setText(readPercent + "%");
            readProgressBar.setProgress(readPercent);

            //독서완료 시 -> 한줄평, 점수 입력 가능
            if (lastEndPage == allpage) {
                isReadDone = true;
                disableEditing(true, oneLineEdit);
                disableEditing(true, editScore);
                scoreSlider.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "마지막으로 한줄평과 점수를 작성해보세요!", Toast.LENGTH_SHORT).show();
            }
        });

        //record save observe
        recordVM.isSaveSuccess().observe(this, Boolean -> {
            if (Boolean == true){
                Toast.makeText(ReadingRecordActivity.this, "저장했습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ReadingRecordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //추가 버튼 클릭 시
        addItemuButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (!isExistBook){
                    Toast.makeText(ReadingRecordActivity.this, "책을 먼저 추가해주세요", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReadingRecordActivity.this, SearchBookListActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                String id = UUID.randomUUID().toString();
                Record newRecord = new Record(id, "DAY " + (++itemCount), null, null, null, lastEndPage, 0);
                //bottomSheet -> item 작성
                RecordBottomSheetFragment bottomSheet = RecordBottomSheetFragment.newInstance(newRecord, "record", isbn13);
                bottomSheet.show(getSupportFragmentManager(), "RecordBottomSheet");
            }
        });

        //저장 버튼
        saveBtn.setOnClickListener(v->{
            String scoreTesxt = editScore.getText().toString();
            String comment = oneLineEdit.getText().toString();
            if (scoreTesxt.isEmpty()){
                Toast.makeText(this, "점수를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (comment.isEmpty()){
                Toast.makeText(this, "한줄평을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            int score = Integer.parseInt(scoreTesxt);

            recordVM.endReading(isbn13, score, comment);
        });

        //한줄평 편집 여부 확인
        oneLineEdit.setOnClickListener(v->{
            Log.d("oneLineEdit", "test1");
            if(!isReadDone){
                Log.d("oneLineEdit", "test2");
                Toast.makeText(this, "독서를 완료한 후 한줄평을 작성해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        //점수 입력 편집 여부 확인
        editScore.setOnClickListener(v->{
            if(!isReadDone) {
                Toast.makeText(this, "독서를 완료한 후 점수를 작성해주세요", Toast.LENGTH_SHORT).show();
            }
        });
        //editView 텍스트 변경 감지 -> Slider 변경
        editScore.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String input = s.toString();
                    if (!input.isEmpty()) {
                        int val = Integer.parseInt(input);
                        if (val >= 0 && val <= 100) {
                            scoreSlider.setValue((float) val);
                        }
                    }
                } catch (NumberFormatException e) {
                    // 숫자가 아닌 입력이 들어올 경우 처리
                    Log.e("addTextChangedListener", "it is not int type");
                }
            }
            // beforeTextChanged, onTextChanged는 빈칸으로 둠
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        //Slider 변경 감지 -> editText 변경
        scoreSlider.addOnChangeListener((slider, value, fromUser) -> {
            // value는 float이므로 (예: 80.0) 정수로 변환 (예: 80)
            int score = (int) value;
            editScore.setText(String.valueOf(score));
        });

    }

    // 1. Intent 데이터를 처리하는 함수 -> searchActivity에서 독서 중 클릭 시
    private void setupUI(MyBook bookFromIntent) {
        updateUI(bookFromIntent);
    }

    // 2. 최신 도서를 Firestore에서 가져오는 함수 -> 하단 바 클릭 시
    private void loadLatestBookFromFirestore() {
        recordVM.loadLatestBook();
    }

    // 3. ISBN으로 도서를 가져오는 함수 -> 내 서재에서 book 클릭 시
    private void loadMyBookFromIsbn13(String userId, String isbn13){
        recordVM.loadMyBookByIsbn13(userId, isbn13);
    }

    public int getReadPercent(int page, int readPage) {
        // 전체 페이지가 0인 경우 (나누기 오류 방지)
        if (page <= 0) return 0;

        // (남은 페이지 / 전체 페이지) * 100
        // 정수 나눗셈 방지를 위해 한쪽을 float로 형변환
        double percent = ((double) readPage / page) * 100;

        // 소수점 반올림 처리 후 int로 반환
        return (int) Math.round(percent);
    }

    private void updateUI(MyBook book) {
        if (book == null) return;

        // 데이터 변수 할당
        isbn13 = book.getIsbn13();
        allpage = book.getPage();
        lastEndPage = book.getReadPage();

        // 퍼센트 계산
        int readPercent = getReadPercent(allpage, lastEndPage);

        Log.d("updateUI", "readOfDate: " + book.getDateOfRead());

        // 텍스트 및 프로그레스바 세팅
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookDateOfRead.setText(book.getDateOfRead() + " ~ 진행중");
        bookRemainingPage.setText("남은 독서량: " + (100 - readPercent) + "%");
        bookRemainingPage2.setText("남은 페이지: " + book.getReadPage() + "/" + book.getPage() + "p");
        textRemainingPage.setText(readPercent + "%");
        readProgressBar.setProgress(readPercent);

        // 이미지 로드
        Glide.with(this)
                .load(book.getCover())
                .into(ivBookCover);

        // 입력 필드 비활성화 (공통 설정)
        disableEditing(false,oneLineEdit);
        disableEditing(false,editScore);
    }

    // EditText 입력을 막는 보조 함수 (중복 제거)
    private void disableEditing(boolean b,EditText editText) {
        if (b){
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }else {
            editText.setInputType(InputType.TYPE_NULL);
        }
        editText.setFocusable(b);
        editText.setFocusableInTouchMode(b);
        editText.setCursorVisible(b);
    }
}
