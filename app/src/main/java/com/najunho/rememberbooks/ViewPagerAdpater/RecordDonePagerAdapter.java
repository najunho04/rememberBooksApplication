package com.najunho.rememberbooks.ViewPagerAdpater;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.najunho.rememberbooks.DataClass.Record;
import com.najunho.rememberbooks.Fragments.RecordDoneListFragment;

import java.util.List;

public class RecordDonePagerAdapter extends FragmentStateAdapter {
    private final List<Record> recordList;
    public RecordDonePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Record> recordList) {
        super(fragmentActivity);
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("createFragment", "success");
        Record record = recordList.get(position);
        return RecordDoneListFragment.newInstance(record, position, recordList.size());
    }

    @Override
    public int getItemCount() {
        return recordList != null ? recordList.size() : 0;
    }
}
