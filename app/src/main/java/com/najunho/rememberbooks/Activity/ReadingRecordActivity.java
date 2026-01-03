package com.najunho.rememberbooks.Activity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.Adapter.RecordAdapter;
import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.Fragments.RecordBottomSheetFragment;
import com.najunho.rememberbooks.Fragments.RecordDoneBottomSheetFragment;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.ViewModel.RecordViewModel;

import java.util.ArrayList;
import java.util.UUID;

public class ReadingRecordActivity extends AppCompatActivity {
    private EditText oneLineEdit, editScore;
    private TextView saveBtn;
    private Button addItemuButton;
    private RecordViewModel recordVM;
    private RecyclerView rv;
    private RecordAdapter recordAdapter;
    private int itemCount = 0;
    private int lastEndPage = 0;
    private boolean isReadDone = false;
    private int allpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_ing);

        addItemuButton = findViewById(R.id.add_item_button);
        saveBtn = findViewById(R.id.save_btn);
        oneLineEdit = findViewById(R.id.oneLineEdit);
        editScore = findViewById(R.id.editScore);

        oneLineEdit.setInputType(InputType.TYPE_NULL);
        oneLineEdit.setFocusable(false);
        oneLineEdit.setFocusableInTouchMode(false);
        oneLineEdit.setCursorVisible(false);

        editScore.setInputType(InputType.TYPE_NULL);
        editScore.setFocusable(false);
        editScore.setFocusableInTouchMode(false);
        editScore.setCursorVisible(false);

        //test용 -> 원래 DB에서 가져온 book 데이터 들어갈 예정
        allpage = 100;

        recordVM = new ViewModelProvider(this).get(RecordViewModel.class);

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
                RecordBottomSheetFragment bottomSheet = RecordBottomSheetFragment.newInstance(record, "edit");
                bottomSheet.show(getSupportFragmentManager(), "EditBottomSheet");
            }

            @Override
            public void onDeleteClick(Record record) {
                //삭제 로직
                recordVM.deleteRecord(record);
                itemCount--;
            }
        });
        rv.setAdapter(recordAdapter);

        recordVM.getRecordList().observe(this, records -> {
            // 데이터가 변경될 때마다 ListAdapter의 submitList 호출
            Log.d("LIST_TEST", "Observer received List HashCode: " + records.hashCode());
            Log.d("getRecordList", "record size: " + records.size());
            Log.d("getRecordList", "record Day: " + records.toString());
            recordAdapter.submitList(new ArrayList<>(records));

            int lastIndex = records.size() -1;
            if (lastIndex >= 0){
                lastEndPage = records.get(lastIndex).getEndPage();
                Log.d("getRecordList", "lastEndPage: " + lastEndPage);
            }

            if (lastEndPage == allpage){
                isReadDone = true;

                oneLineEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                oneLineEdit.setFocusable(true);
                oneLineEdit.setFocusableInTouchMode(true);
                oneLineEdit.setCursorVisible(true);

                editScore.setInputType(InputType.TYPE_CLASS_TEXT);
                editScore.setFocusable(true);
                editScore.setFocusableInTouchMode(true);
                editScore.setCursorVisible(true);

                saveBtn.setVisibility(View.VISIBLE);
                Toast.makeText(this, "마지막으로 한줄평과 점수를 작성해보세요!", Toast.LENGTH_SHORT).show();
            }
        });

        //추가 버튼 클릭 시
        addItemuButton.setOnClickListener(v->{
            String id = UUID.randomUUID().toString();
            Record newRecord = new Record(id, "DAY " + (++itemCount), null, null, null, lastEndPage, 0);
            //bottomSheet -> item 작성
            RecordBottomSheetFragment bottomSheet = RecordBottomSheetFragment.newInstance(newRecord, "record");
            bottomSheet.show(getSupportFragmentManager(), "RecordBottomSheet");
        });

        saveBtn.setOnClickListener(v->{
            Log.d("saveBtn", "save logic");
            Toast.makeText(this, "독서 완료", Toast.LENGTH_SHORT).show();
        });

        oneLineEdit.setOnClickListener(v->{
            if(!isReadDone){
                Toast.makeText(this, "독서를 완료한 후 한줄평을 작성해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        editScore.setOnClickListener(v->{
            if(!isReadDone) {
                Toast.makeText(this, "독서를 완료한 후 점수를 작성해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
