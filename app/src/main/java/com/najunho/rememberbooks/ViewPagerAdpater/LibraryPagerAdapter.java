package com.najunho.rememberbooks.ViewPagerAdpater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.Fragments.MyLibraryBooksFragment;

import java.util.List;

public class LibraryPagerAdapter extends FragmentStateAdapter {
    private List<MyBook> bookList;
    public LibraryPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<MyBook> bookList) {
        super(fragmentActivity);
        this.bookList = bookList;
    }

    // 데이터를 통째로 바꾸는 메서드
    public void setBooks(List<MyBook> newBooks) {
        bookList = newBooks;
        notifyDataSetChanged(); // 또는 DiffUtil을 사용하여 부드럽게 갱신
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 포지션 번호만 넘겨서 프래그먼트가 알아서 데이터를 필터링하게 함
        return MyLibraryBooksFragment.newInstance(position, bookList);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
