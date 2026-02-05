package com.najunho.rememberbooks.ViewPagerAdpater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.najunho.rememberbooks.DataClass.Review;
import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.Fragments.SearchBookDetailFragment;
import com.najunho.rememberbooks.Fragments.SearchBookPage3Fragment;
import com.najunho.rememberbooks.Fragments.SearchBookReviewFragment;

import java.util.List;

public class SearchPagerAdapter extends FragmentStateAdapter {
    private SearchResult book;
    public SearchPagerAdapter(@NonNull FragmentActivity fragmentActivity, SearchResult book) {
        super(fragmentActivity);
        this.book = book;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return SearchBookDetailFragment.newInstance(book);
            case 1:
                return SearchBookReviewFragment.newInstance(book.isbn13);
            case 2:
                return SearchBookPage3Fragment.newInstance(book);
            default:
                return new SearchBookDetailFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
