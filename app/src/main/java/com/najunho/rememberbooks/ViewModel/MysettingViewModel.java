package com.najunho.rememberbooks.ViewModel;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.najunho.rememberbooks.Db.UserRepo;

public class MysettingViewModel extends ViewModel {
    private MutableLiveData<String> _nickname = new MutableLiveData<>();
    public LiveData<String> nickname = _nickname;

    public LiveData<String> getNickname() {
        return nickname;
    }

    private MutableLiveData<Boolean> _isSuccess = new MutableLiveData<>();
    public LiveData<Boolean> isSuccess = _isSuccess;
    public LiveData<Boolean> getIsSuccess() {
        return isSuccess;
    }

    private MutableLiveData<Boolean> _isExit = new MutableLiveData<>();
    public LiveData<Boolean> isExit = _isExit;
    public LiveData<Boolean> getIsExit() {
        return isExit;
    }
    public void changeNickName(String userId,String nickName){
        UserRepo.changeNickName(userId, nickName, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("changeNickName", "result: " + task.isSuccessful());
                if(task.isSuccessful()){
                    _nickname.setValue(nickName);
                }
            }
        });
    }

    public void changeDisclosure(String userId){
        UserRepo.changeDisclosure(userId, new UserRepo.OnCheckListener() {
            @Override
            public void onSuccess(boolean success) {
                _isSuccess.setValue(true);
            }

            @Override
            public void onError(Exception e) {
                _isSuccess.setValue(false);
                Log.e("changeDisclosure", e.getMessage());
            }
        });
    }

    public void withdrawAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // 1. Firebase Auth에서 유저 삭제 시도
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // 삭제 성공
                                Log.d("Withdraw", "User account deleted successfully.");
                                _isExit.setValue(true);
                            } else {
                                // 삭제 실패 (대부분 '보안상 재인증 필요' 이슈)
                                Log.e("Withdraw", "User account deletion failed.", task.getException());
                                _isExit.setValue(false);
                            }
                        }
                    });
        }
    }
}
