package com.example.syama.mymemopp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.syama.mymemopp.R;

public class MemoActivity extends AppCompatActivity {

    public static final String BUNDLE_KEY_URI = "uri";

    private static final int REQUEST_SETTING = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        // 指定されたメモのURIを取得する
        Uri uri = getIntent().getParcelableExtra(BUNDLE_KEY_URI);

        // 指定されたメモを読み込む
        MemoFragment memoFragment = (MemoFragment)getFragmentManager()
                .findFragmentById(R.id.MemoFragment);
        memoFragment.load(uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューを生成する
        getMenuInflater().inflate(R.menu.menu_memo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // 「設定」アクション
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_SETTING);
                return true;

            // 「保存」アクション
            case R.id.action_save:
                MemoFragment memoFragment = (MemoFragment)getFragmentManager().findFragmentById(R.id.MemoFragment);
                memoFragment.save();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTING && resultCode == RESULT_OK) {
            // 設定の変更を反映させる
            MemoFragment memoFragment = (MemoFragment)getFragmentManager()
                    .findFragmentById(R.id.MemoFragment);
            memoFragment.reflectSettings();
        }
    }
}
