package com.najunho.rememberbooks.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.najunho.rememberbooks.R;

public class FirstSearchFragment  extends Fragment {
    private TextView title, author, publisher, description, category, pages;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        title = view.findViewById(R.id.tv_detail_title);
        author = view.findViewById(R.id.tv_detail_author);
        publisher = view.findViewById(R.id.tv_detail_publisher);
        description = view.findViewById(R.id.tv_detail_description);
        category = view.findViewById(R.id.tv_detail_category);
        pages = view.findViewById(R.id.tv_detail_pages);

        return view;
    }
}
