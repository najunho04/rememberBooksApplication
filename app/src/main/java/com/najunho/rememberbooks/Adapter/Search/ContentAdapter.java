package com.najunho.rememberbooks.Adapter.Search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.najunho.rememberbooks.DataClass.SearchResult;
import com.najunho.rememberbooks.R;
import com.najunho.rememberbooks.ViewPagerAdpater.SearchPagerAdapter;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    public interface onSelectedFragmentListener{
        void onSelectedFragment(int position);
    }
    private onSelectedFragmentListener listener;

    private FragmentActivity fragmentActivity;
    private ViewPager2 viewPager;
    private SearchResult book;

    public ContentAdapter(FragmentActivity fragmentActivity, SearchResult book, onSelectedFragmentListener listener) {
        this.fragmentActivity = fragmentActivity;
        this.listener = listener;
        this.book = book;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_section_content, parent, false);
        return new ContentViewHolder(view, fragmentActivity, listener, book);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        if(viewPager == null){
            viewPager = holder.viewPager;
        }
    }

    @Override
    public int getItemCount() {
        return 1; // ViewPager 영역은 하나만 존재
    }

    // 외부(Activity)에서 특정 페이지로 이동시키기 위한 메서드
    public void setCurrentItem(int item) {
        if (viewPager != null) {
            viewPager.setCurrentItem(item, true);
        }
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager;

        //bind로직에 viewPager 초기화 할 경우 재사용할 때마다 초기화됨 -> 이슈 발생 가능성 높음
        //따라서 viewholder 초기화 시에 viewPager 함께 초기화. 이를 위해 viewHolder 파라미터 확장 (fragmentactivity, listener)
        public ContentViewHolder(@NonNull View itemView, FragmentActivity fragmentActivity,onSelectedFragmentListener listener, SearchResult book) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.viewPagerContent);

            // 1. ViewPager2에 어댑터 연결 (단 한 번)
            SearchPagerAdapter pagerAdapter = new SearchPagerAdapter(fragmentActivity, book);
            viewPager.setAdapter(pagerAdapter);

            // 2. 페이지 변경 콜백 등록 (단 한 번)
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    if (listener != null) {
                        listener.onSelectedFragment(position);
                    }
                }
            });
        }
    }
}