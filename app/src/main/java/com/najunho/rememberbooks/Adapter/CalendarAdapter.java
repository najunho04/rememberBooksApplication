package com.najunho.rememberbooks.Adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.imageview.ShapeableImageView;
import com.najunho.rememberbooks.DataClass.MyBook;
import com.najunho.rememberbooks.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<Calendar> dayList;
    private Map<String, List<MyBook>> bookMap;
    private Calendar currentMonthCalendar; // 이번 달인지 판별용
    private OnDayClickListener listener;

    // 클릭 인터페이스 정의
    public interface OnDayClickListener {
        void onDayClick(Calendar day, List<MyBook> books);
    }

    public CalendarAdapter(List<Calendar> dayList, Map<String, List<MyBook>> bookMap, Calendar currentMonthCalendar, OnDayClickListener listener) {
        this.dayList = dayList;
        this.bookMap = bookMap;
        this.currentMonthCalendar = currentMonthCalendar;
        this.listener = listener;
    }

    // 데이터 갱신용 메서드
    public void updateData(List<Calendar> newDays, Map<String, List<MyBook>> newMap, Calendar newMonth) {
        this.dayList = newDays;
        this.bookMap = newMap;
        this.currentMonthCalendar = newMonth;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Calendar day = dayList.get(position);

        // 1. 날짜 표시
        holder.tvDay.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));

        // 2. 이번 달이 아니면 흐리게 처리 (Visual styling)
        if (day.get(Calendar.MONTH) != currentMonthCalendar.get(Calendar.MONTH)) {
            holder.tvDay.setTextColor(Color.GRAY);
            holder.itemView.setAlpha(0.3f);
        } else {
            holder.tvDay.setTextColor(Color.BLACK);
            holder.itemView.setAlpha(1.0f);
        }

        // 3. 오늘 날짜 강조 (선택사항)
        // ... (필요 시 구현)

        // 4. 책 이미지 매핑
        String key = getKeyFromCalendar(day); // "2026.01.29"

        // 초기화 (재사용 문제 방지)
        holder.ivCover.setImageDrawable(null);
        holder.tvBadge.setVisibility(View.GONE);

        if (bookMap.containsKey(key)) {
            List<MyBook> books = bookMap.get(key);
            if (books != null && !books.isEmpty()) {
                // Glide로 이미지 로드
                Glide.with(holder.itemView.getContext())
                        .load(books.get(0).getCover())
                        .transform(new CenterCrop(), new RoundedCorners(8)) // CenterCrop 후 라운드 처리
                        .placeholder(R.drawable.default_book_cover_v4) // 로딩 중 이미지
                        .into(holder.ivCover);

                // 2권 이상이면 배지 표시
                if (books.size() > 1) {
                    holder.tvBadge.setVisibility(View.VISIBLE);
                    holder.tvBadge.setText("+" + (books.size() - 1));
                }
            }
        }else {
            // #EAE4DB 색상으로 배경만 채우기
            int myColor = Color.parseColor("#EAE4DB");

            Glide.with(holder.itemView.getContext())
                    .load("") // 실제 URL이 없을 때 빈 문자열 혹은 null
                    .fallback(new ColorDrawable(myColor)) // 데이터가 null일 때
                    .placeholder(new ColorDrawable(myColor)) // 로딩 중일 때
                    .error(new ColorDrawable(myColor)) // 에러 났을 때
                    .transform(new CenterCrop(), new RoundedCorners(8))
                    .into(holder.ivCover);
        }

        // 5. 클릭 이벤트
        holder.itemView.setOnClickListener(v -> {
            List<MyBook> books = bookMap.get(key);
            listener.onDayClick(day, books);
        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvBadge;
        ShapeableImageView ivCover;
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvBadge = itemView.findViewById(R.id.tv_badge);
            ivCover = itemView.findViewById(R.id.iv_book_cover);
        }
    }

    // 키 생성 헬퍼 (MainActivity와 동일한 로직 사용 필수)
    private String getKeyFromCalendar(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d.%02d.%02d", year, month, day);
    }
}