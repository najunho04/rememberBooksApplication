package com.najunho.rememberbooks.Util

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Java에서 사용할 콜백 인터페이스 (Java에서 익명 클래스로 쉽게 구현 가능)
interface GoogleSignInCallback {
    fun onSuccess(idToken: String)
    fun onError(errorMessage: String)
}

object GoogleSignInHelper {
    @JvmStatic
    fun signInWithGoogleAsync(activity: Activity, webClientId: String, callback: GoogleSignInCallback) {
        // 코루틴을 메인(UI) 스레드에서 실행
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val idToken = signInWithGoogleSuspend(activity, webClientId)
                Log.d("afterIdToken", "IdToken : $idToken")
                if (idToken != null) {
                    callback.onSuccess(idToken)
                } else {
                    callback.onError("Failed to obtain Google ID token")
                }
            } catch (e: Exception) {
                Log.e("GoogleSignInHelper", "signIn failed", e)
                callback.onError(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun signInWithGoogleSuspend(activity: Activity, webClientId: String): String? {
        val credentialManager = CredentialManager.create(activity)

        //google API 옵션 생성
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .build()

        //req 요청
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            // suspend 함수 - Kotlin 코루틴 안에서만 호출 가능/ 구글 서버에 req 전송
            //-> 구글 인증 UI req and 생성
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = activity
            )

            // 결과 처리: res에서 받은 credential가 구글 인증용 credential이면 -> idToken 생성
            val credential = result.credential
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    return googleIdTokenCredential.idToken
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("GoogleSignInHelper1", "Invalid google id token response", e)
                    null
                }
            } else {
                Log.e("GoogleSignInHelper2", "Unexpected credential type: ${credential?.javaClass}")
                null
            }
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignInHelper3", "getCredential failed", e)
            null
        }
        catch (e: GetCredentialCancellationException){
            Log.e("GoogleSignInHelper4", "getCredential cancelled", e)
            null
        }
    }
}