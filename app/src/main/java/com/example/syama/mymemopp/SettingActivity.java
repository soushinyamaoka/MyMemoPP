package com.example.syama.mymemopp;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by SoushinYamaoka on 2018/04/01.
 */

public class SettingActivity extends Activity
        implements SettingFragment.SettingFragmentlistener{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //アクションバーに戻るボタンを設定する
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        //Resultはデフォルトでキャンセルを設定
        setResult(RESULT_CANCELED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            //戻るボタンを押されたら、画面を閉じる
            this.finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSettingChanged(){
        //設定が変更されたら、ResultにOKを設定
        setResult(RESULT_OK);
    }
}





















