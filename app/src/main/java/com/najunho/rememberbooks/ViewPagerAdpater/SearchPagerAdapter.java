package com.najunho.rememberbooks.ViewPagerAdpater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.najunho.rememberbooks.Fragments.FirstSearchFragment;
import com.najunho.rememberbooks.Fragments.SecondSearchFragment;

public class SearchPagerAdapter extends FragmentStateAdapter {
    public SearchPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FirstSearchFragment();
            case 1:
                return new SecondSearchFragment();
            case 2:
                //추후...
                return new FirstSearchFragment();
            default:
                return new FirstSearchFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
