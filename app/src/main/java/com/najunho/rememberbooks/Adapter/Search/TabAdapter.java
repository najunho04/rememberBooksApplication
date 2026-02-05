package com.najunho.rememberbooks.Adapter.Search;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.najunho.rememberbooks.R;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabViewHolder> {

    // 어떤 탭이 눌렸는지 알리기 위한 인터페이스
    public interface OnTabClickListener {
        void onTabClick(int position);
    }

    private OnTabClickListener listener;
    private int selectedTab = 0; // 현재 선택된 탭 번호

    public TabAdapter(OnTabClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_section_tabs, parent, false);
        return new TabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        // 버튼 클릭 시 리스너 호출 및 색상 변경 로직
        holder.btnInfo.setOnClickListener(v -> handleTabClick(0));
        holder.btnOneLine.setOnClickListener(v -> handleTabClick(1));
        holder.btnDetail.setOnClickListener(v -> handleTabClick(2));

        // 선택 상태에 따른 배경색 업데이트 (선택된 탭만 진한 색)
        updateTabColors(holder);
    }

    private void handleTabClick(int tabIndex) {
        selectedTab = tabIndex;
        notifyItemChanged(0);
        if (listener != null) listener.onTabClick(tabIndex); //fragment UI 변경
    }

    private void updateTabColors(TabViewHolder holder) {
        int selectedColor = Color.parseColor("#CBBBA0");
        int unselectedColor = Color.parseColor("#E9E1D6");

        holder.btnInfo.setBackgroundTintList(ColorStateList.valueOf(selectedTab == 0 ? selectedColor : unselectedColor));
        holder.btnOneLine.setBackgroundTintList(ColorStateList.valueOf(selectedTab == 1 ? selectedColor : unselectedColor));
        holder.btnDetail.setBackgroundTintList(ColorStateList.valueOf(selectedTab == 2 ? selectedColor : unselectedColor));
    }

    @Override
    public int getItemCount() {
        return 1; // 탭 바 전체가 하나의 아이템
    }

    public void updateUi(int position){
        selectedTab = position;
        notifyItemChanged(0);
    }

    static class TabViewHolder extends RecyclerView.ViewHolder {
        Button btnInfo, btnOneLine, btnDetail;

        public TabViewHolder(@NonNull View itemView) {
            super(itemView);
            btnInfo = itemView.findViewById(R.id.btnBookInfo);
            btnOneLine = itemView.findViewById(R.id.btnOneLine);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}