package com.najunho.rememberbooks.Util;

import android.os.SystemClock;
import android.view.View;

public abstract class OnSingleClickListener implements View.OnClickListener {
    // 클릭 간격 (1초)
    private static final long MIN_CLICK_INTERVAL = 1000;
    private long lastClickTime = 0;

    //추상 메서드 : 리스너 생성 시 구현 필요
    public abstract void onSingleClick(View v);

    @Override
    public final void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - lastClickTime;
        lastClickTime = currentClickTime;

        // 제한 시간보다 차이가 크면 실행
        if (elapsedTime > MIN_CLICK_INTERVAL) {
            onSingleClick(v);
        }
    }
}