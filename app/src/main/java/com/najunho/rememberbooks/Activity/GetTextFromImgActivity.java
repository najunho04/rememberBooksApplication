package com.najunho.rememberbooks.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.najunho.rememberbooks.R;

import java.io.IOException;

public class GetTextFromImgActivity  extends AppCompatActivity {
    private TextRecognizer textRecognizer;
    private TextView getImgBtn, backBtn, saveBtn;
    private EditText editText;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_text_from_img);

        //ML kit 초기화
        textRecognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

        //Launcher result callback
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        processImageTag(imageUri);
                        Glide.with(GetTextFromImgActivity.this)
                                .load(imageUri)
                                .centerCrop()
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(imageView);
                    }
                }
        );

        getImgBtn = findViewById(R.id.tv_get_img);
        backBtn = findViewById(R.id.tv_back);
        saveBtn = findViewById(R.id.tv_save);
        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);

        saveBtn.setOnClickListener(v->{
            //RecordBottomSheetFragment에 텍스트 전달해야 됨..
            String resultText = editText.getText().toString();
            if (resultText.isEmpty()){
                Toast.makeText(this, "텍스트를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
            // 1. 결과를 담을 Intent 생성
            Intent resultIntent = new Intent();
            resultIntent.putExtra("OCR_RESULT", resultText); // 키값 설정

            // 2. 결과 상태와 데이터 설정
            setResult(Activity.RESULT_OK, resultIntent);

            // 3. 현재 Activity 종료 (B로 돌아감)
            finish();
        });

        getImgBtn.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*"); // 이미지 타입만 선택하도록 설정
            resultLauncher.launch(intent);
        });

        backBtn.setOnClickListener(v->{
            finish();
        });
    }

    // 갤러리에서 선택된 이미지 URI를 통해 텍스트를 가져오는 메서드
    private void processImageTag(Uri imageUri) {
        try {
            // 1. URI로부터 ML Kit용 InputImage 객체 생성
            InputImage image = InputImage.fromFilePath(this, imageUri);

            // 2. 이미지 프로세싱 시작
            textRecognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            // [성공] 인식된 전체 텍스트 가져오기
                            String resultText = visionText.getText();
                            /*/
                            텍스트 블록별로 더 상세하게 처리하고 싶다면:
                            for (Text.TextBlock block : visionText.getTextBlocks())
                            {String blockText = block.getText();}
                            /*/

                            // TODO: 추출된 텍스트(resultText)를 Firestore에 저장하거나 다음 단계로 전달
                            Log.d("MLKIT", "추출 성공: " + resultText);
                            editText.setText(resultText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // [실패] 에러 로그 출력 및 사용자 알림
                            Log.e("MLKIT", "텍스트 인식 실패: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
