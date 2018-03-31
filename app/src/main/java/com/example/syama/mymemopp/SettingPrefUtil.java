package com.example.syama.mymemopp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import java.util.Collections;
import java.util.Set;

/**
 * Created by SoushinYamaoka on 2018/03/31.
 */

public class SettingPrefUtil {

    //保存先ファイル名
    public static final String PREF_FILE_NAME = "settings";

    //ファイル名プレフィックスのKEY
    private static final String KEY_FILE_NAME_PREFIX = "file.name.prefix";
    //未設定時のデフォルト値
    private static final String KEY_FILE_NAME_PREFIX_DEFAULT = "memo";

    //ファイル名プレフィックスの値を取得する
    private static String getFileNamefix(Context context){
        //SharedPreferencesを取得
        SharedPreferences sp = context.getSharedPreferences(
                PREF_FILE_NAME, Context.MODE_PRIVATE);

        //SharedPreferencesから設定値を取得する
        return sp.getString(KEY_FILE_NAME_PREFIX,
                KEY_FILE_NAME_PREFIX_DEFAULT);
    }

    private static final String KEY_TEXT_SIZE = "text.size";
    private static final String TEXT_SIZE_LARGE = "text.size.large";
    public static final String TEXT_SIZE_MEDIUM = "text.size.medium";
    public static final String TEXT_SIZE_SMALL = "text.size.small";

    private static final String KEY_TEXT_STYLE = "text.style";
    private static final String TEXT_STYLE_BOLD = "text.style.bold";
    private static final String TEXT_STYLE_ITALIC = "text.style.italic";
    private static final String KEY_SCREEN_REVERSE = "screen.reverse";

    //フォントサイズを取得する
    public static float getFontSize(Context context){
        //SharedPreferencesを取得
        SharedPreferences sp = context.getSharedPreferences(
                PREF_FILE_NAME, Context.MODE_PRIVATE);
        //現在の設定値
        String storedSize = sp.getString(KEY_TEXT_SIZE, TEXT_SIZE_MEDIUM);

        //設定値に応じて、実際のテキストサイズを返す
        switch (storedSize){
            case TEXT_SIZE_LARGE:
                return context.getResources().getDimension(
                        R.dimen.settings_text_size_large);
            case TEXT_SIZE_MEDIUM:
                return context.getResources().getDimension(
                        R.dimen.settings_text_size_medium);
            case TEXT_SIZE_SMALL:
                return context.getResources().getDimension(
                        R.dimen.settings_text_size_small);
            default:
                return context.getResources().getDimension(
                        R.dimen.settings_text_size_medium);
        }
    }

    //文字装飾の設定を取得する
    public static int getTypeface(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                PREF_FILE_NAME, Context.MODE_PRIVATE);
        Set<String> storedTypeface = sp.getStringSet(KEY_TEXT_STYLE,
                Collections.<String>emptySet());

        //EditTextに設定するビットフラグに変換する
        int typefaceBit = Typeface.NORMAL;
        for(String value : storedTypeface){
            switch (value){
                case TEXT_STYLE_ITALIC:
                    typefaceBit |= Typeface.NORMAL;
                    break;
                case TEXT_STYLE_BOLD:
                    typefaceBit |= Typeface.BOLD;
                    break;
            }
        }

        return typefaceBit;
    }

    //画面の明暗を反転するかどうか
    public static boolean isScreenReverse(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_SCREEN_REVERSE, false);
    }
}