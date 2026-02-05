package com.najunho.rememberbooks.Util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class AssetUtils {
    public static String getAssetJsonData(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)))) {
            // Java 8 이상 스트림 사용 시 효율적입니다.
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            Log.e("getAssetJsonData", "e: " + e);
            return "";
        }
    }
}
