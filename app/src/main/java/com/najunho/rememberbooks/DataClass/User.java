package com.najunho.rememberbooks.DataClass;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

public class User implements Serializable {
    private String nickName;
    private String email;

    // Firestore 서버 시간을 기준으로 자동으로 값이 채워지도록 설정
    @ServerTimestamp
    private Timestamp joinDate;
    private int readCount;
    private boolean disClosure = true;

    // 1. 기본 생성자 (Firestore 내부 객체 변환을 위해 필수)
    public User() {}

    // 2. 초기 세팅용 생성자
    public User(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
        // joinDate는 @ServerTimestamp가 처리하므로 생성자에서 뺄 수 있습니다.
    }

    // 3. Getter / Setter (Firestore가 접근할 수 있도록 Public)
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Timestamp getJoinDate() { return joinDate; }
    public void setJoinDate(Timestamp joinDate) { this.joinDate = joinDate; }

    public int getReadCount() {return readCount;}
    public void setReadCount(int readCount) {this.readCount = readCount;}

    public boolean getDisclosure() {return disClosure;}
    public void setDisclosure(boolean disClosure) {this.disClosure = disClosure;}

}