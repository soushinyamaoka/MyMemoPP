package com.example.syama.mymemopp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by SoushinYamaoka on 2018/04/01.
 */

public class MemoActivity extends Activity {

    public static final String BUNDLE_KEY_URI = "uri";

    private static final int REQUEST_SETTIND = 0;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_memo);

        //指定されたメモのURIを取得する
        Uri uri = getIntent().getParcelableExtra(BUNDLE_KEY_URI);

        //指定されたメモを読み込む
        MemoFragment memoFragment = (MemoFragment)getFragmentManager()
                .findFragmentById(R.id.MemoFragment);
        memoFragment.load(uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //メニューを生成する
        getMenuInflater().inflate(R.menu.menu_memo, menu);
        return true;
    }

    //メニューアイテムが押された
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //メニューアイテムのIDを取得
        int id = item.getItemId();

        switch (id){
            //「設定」アクション
            case R.id.action_settings:
                //設定アクティビティへ遷移する
                Intent intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_SETTIND);
                return true;

            //「保存」アクション
            case R.id.action_save:
                //保存する
                MemoFragment memoFragment = (MemoFragment)getFragmentManager()
                        .findFragmentById(R.id.MemoFragment);
                memoFragment.save();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent date){
        if(requestCode == REQUEST_SETTIND &&resultCode == RESULT_OK){
            //設定の変更を反映させる
            MemoFragment memoFragment = (MemoFragment)getFragmentManager()
                    .findFragmentById(R.id.MemoFragment);
            memoFragment.reflectSettings();
        }
    }
}





















