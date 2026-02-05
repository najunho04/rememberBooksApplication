package com.najunho.rememberbooks.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.najunho.rememberbooks.DataClass.DiscussionLog;
import com.najunho.rememberbooks.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.noties.markwon.Markwon;

public class DiscussionDetailFragment extends BottomSheetDialogFragment {

    private static final String ARG_LOG = "discussion_log";
    private DiscussionLog log;
    private Markwon markwon;

    public static DiscussionDetailFragment newInstance(DiscussionLog log) {
        DiscussionDetailFragment fragment = new DiscussionDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOG, log);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_discussion_detail, container, false);

        TextView tvTimestamp = view.findViewById(R.id.tvTimestamp);
        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        TextView tvAnswer = view.findViewById(R.id.tvAnswer);
        markwon = Markwon.create(requireContext());

        if (getArguments() != null) {
            log = (DiscussionLog) getArguments().getSerializable(ARG_LOG);
            if (log != null) {
                markwon.setMarkdown(tvQuestion, log.getQuestion());
                markwon.setMarkdown(tvAnswer, log.getAnswer());

                // 시간 포맷팅 (long -> String)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
                tvTimestamp.setText(sdf.format(new Date(log.getTimestamp())));
            }
        }

        return view;
    }
}
