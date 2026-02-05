package com.najunho.rememberbooks.Util;

public class Event<T> {
    private T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        if (content == null) {
            throw new IllegalArgumentException("null content in Event");
        }
        this.content = content;
    }

    /**
     * 데이터를 반환하고 '처리됨' 상태로 바꿉니다.
     * 이미 처리되었다면 null을 반환합니다.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * 처리 여부와 상관없이 값을 확인만 합니다.
     */
    public T peekContent() {
        return content;
    }

    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}