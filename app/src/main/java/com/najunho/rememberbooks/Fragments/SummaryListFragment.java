package com.najunho.rememberbooks.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.najunho.rememberbooks.Adapter.DiscussionAdapter;
import com.najunho.rememberbooks.DataClass.DiscussionLog;
import com.najunho.rememberbooks.Db.DiscussionRepo;
import com.najunho.rememberbooks.R;

import java.util.ArrayList;
import java.util.List;

public class SummaryListFragment extends Fragment {
    private float initialX;
    private static final int SWIPE_THRESHOLD = 300;
    private DiscussionAdapter adapter;
    private FirebaseAuth mAuth;
    private ListenerRegistration registration;
    private static final String ARG_ISBN13 = "isbn13";
    private String isbn13;

    public static SummaryListFragment newInstance(String isbn13) {
        SummaryListFragment fragment = new SummaryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ISBN13, isbn13); // 데이터 담기
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // View Binding 초기화
        View view = inflater.inflate(R.layout.fragment_summary_list, container, false);

        if (getArguments() != null) {
            isbn13 = getArguments().getString(ARG_ISBN13);
        }
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new DiscussionAdapter((position, log) -> {
            //Click Event
            DiscussionDetailFragment bottomSheet = DiscussionDetailFragment.newInstance(log);
            bottomSheet.show(getParentFragmentManager(), "DiscussionDetailFragment");
        });

        ImageButton btnClose = view.findViewById(R.id.btnClose);
        RecyclerView rvSummaryList = view.findViewById(R.id.rvSummaryList);

        rvSummaryList.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSummaryList.setAdapter(adapter);

        // 리스너 등록 및 변수에 저장
        registration = DiscussionRepo.observeDiscussionLogs(mAuth.getCurrentUser().getUid(), isbn13, new DiscussionRepo.OnLogsLoadedListener() {
            @Override
            public void onLogsLoaded(List<DiscussionLog> logs) {
                // 프래그먼트가 살아있을 때만 안전하게 어댑터 업데이트
                if (adapter != null) {
                    adapter.submitList(new ArrayList<>(logs));
                    Log.d("onLogsLoaded", "success");
                    Log.d("onLogsLoaded", "logs: " + logs);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("onError", "error" + e);
            }
        });

        //닫기 버튼 설정
        btnClose.setOnClickListener(v -> {
            dismissFragment();
        });

        //프래그먼트의 배경(root view)을 터치했을 때 제스처 감지
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("ACTION_DOWN", "success");
                    initialX = event.getX();
                    // 1. DOWN 시점에 true를 리턴해야 이후 MOVE, UP 이벤트를 받을 수 있습니다.
                    return true;

                case MotionEvent.ACTION_UP:
                    Log.d("ACTION_UP", "success");
                    float finalX = event.getX();
                    float deltaX = finalX - initialX;

                    // 2. 스와이프 임계값을 넘었는지 확인
                    if (deltaX > SWIPE_THRESHOLD) {
                        Log.d("SWIPE_THRESHOLD", "success");
                        dismissFragment();
                    } else {
                        // 3. 스와이프가 아니라면 '클릭'으로 간주하고 performClick 호출
                        Log.d("SWIPE_THRESHOLD", "fail");
                        v.performClick();
                    }
                    return true;
            }
            return false;
        });

    }

    private void dismissFragment() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                    .remove(this)
                    .commit();
            // 백스택에서도 제거 (뒤로가기 시 문제 방지)
            getParentFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 프래그먼트가 닫힐 때 리스너 해제 (중요!)
        if (registration != null) {
            Log.d("registration", "remove");
            registration.remove();
        }
    }
}