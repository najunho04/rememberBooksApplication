package com.najunho.rememberbooks.ViewPagerAdpater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.najunho.rememberbooks.Fragments.BookListFragment;

public class LibraryPagerAdapter extends FragmentStateAdapter {
    public LibraryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 포지션 번호만 넘겨서 프래그먼트가 알아서 데이터를 필터링하게 함
        return BookListFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
