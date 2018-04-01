package com.example.syama.mymemopp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import java.util.Collections;
import java.util.Set;

/**
 * Created by SoushinYamaoka on 2018/04/01.
 */

public class SettingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    //変更イベントをアクティビティなどに通知する
    public interface SettingFragmentlistener{
        void onSettingChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //ファイル名を指定する
        getPreferenceManager().setSharedPreferencesName(
                SettingRepository.PREF_FILE_NAME);

        //Preferencesの設定ファイルを指定
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //サマリーを更新
        setTypefaceSummary(getPreferenceManager().setSharedPreferences());
        setPrefixSummary(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onResume(){
        super.onResume();

        //SharedPreferencesの値が変更されたイベントを監視するリスナーを登録
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();

        //リスナーの登録を解除
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs,
                                          String key){
        //アクティビティを取得
        Activity activity = getActivity();

        //ActivityがSettingFragmentListenerを実装しているのであれば通知する
        if (activity instanceof SettingFragmentlistener){
            SettingFragmentlistener listener =
                    (SettingFragmentlistener)activity;

            //アクティビティに変更通知
            listener.onSettingChanged();
        }

        //サマリーに反映する
        if(activity.getString(R.string.key_text_style).equals(key)){
            setTypefaceSummary(sharedPreferences);
        } else if (activity.getString(R.string.key_file_name_prefix)
                .equals(key)){
            setPrefixSummary(sharedPreferences);
        }
    }

    private void setTypefaceSummary(SharedPreferences sharedPreferences){
        String key = getActivity().getString(R.string.key_text_style);

        Preference preference = findPreference(key);

        Set<String> selected = sharedPreferences.getStringSet(key,
                Collections.<String>emptySet());
        preference.setSummary(TextUtils.join("/", selected.toArray()));
    }

    private void setPrefixSummary(SharedPreferences sharedPreferences){
        String key = getActivity().getString(R.string.key_file_name_prefix);
        Preference preference = findPreference(key);

        String prefix = sharedPreferences.getString(key, "");
        preference.setSummary(prefix);
    }
}














