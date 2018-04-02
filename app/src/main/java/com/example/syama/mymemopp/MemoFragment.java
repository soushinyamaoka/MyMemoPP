package com.example.syama.mymemopp;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.AsyncTaskLoader;
import android.app.LoaderManager;
import android.widget.Toast;
import android.content.Loader;
import android.support.annotation.Nullable;
import android.widget.Toast;

import static com.example.syama.mymemopp.MemoActivity.BUNDLE_KEY_URI;

/**
 * Created by SoushinYamaoka on 2018/04/01.
 */

public class MemoFragment extends Fragment implements LoaderManager.LoaderCallbacks{
    private MemoEditText mMemoEditText;

    private Uri mMemoUri;

    private static final int LOADER_SAVE_MEMO = 1;
    private static final int LOADER_LOAD_MEMO = 2;

    private static final String BUNDLE_KEY_URI = "uri";
    private static final String BUNDLE_KEY_TEXT = "text";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState){
        //レイアウトXMLからViewを生成
        View view = inflater.inflate(R.layout.fragment_memo, container, false);

        mMemoEditText = (MemoEditText)view.findViewById(R.id.Memo);

        return view;
    }

    //設定を反映する
    public void reflectSettings(){
        Context context = getActivity();

        if (context != null) {
            //SharedPreferencesから値を取得して、設定を反映する
            setFontSize(SettingPrefUtil.getFontSize(context));
            setTypeface(SettingPrefUtil.getTypeface(context));
            setMemoColor(SettingPrefUtil.isScreenReverse(context));
        }
    }

    //文字サイズの設定を反映する
    private void setFontSize(float fontSizePx){
        mMemoEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizePx);
    }

    //文字装飾の設定を反映する
    private void setTypeface(int typeface){
        mMemoEditText.setTypeface(Typeface.DEFAULT, typeface);
    }

    //色の反転の設定を反映する
    private void setMemoColor(boolean reverse){
        int backgroundColor = reverse ? Color.BLACK : Color.WHITE;
        int textColor = reverse ? Color.WHITE : Color.BLACK;

        mMemoEditText.setBackgroundColor(backgroundColor);
        mMemoEditText.setTextColor(textColor);
    }

    //保存する
    public void save() {
        // URIとメモ内容をBundleに詰める
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_URI, mMemoUri);
        bundle.putString(BUNDLE_KEY_TEXT, mMemoEditText.getText().toString());

        // 保存用のLoaderを要求する
        getLoaderManager().restartLoader(LOADER_SAVE_MEMO, bundle, this).forceLoad();
    }

    //読み込む
    public void load(Uri uri){
        //「現在のURI」を変更する
        mMemoUri = uri;

        if (uri != null){
            //メモを読み込む
            String memo = MemoRepository.findMemoByUri(getActivity(), uri);

            //EditTextに反映する
            mMemoEditText.setText(memo);
        } else {
            //URIがnullの場合には、メモをクリアするだけ
            mMemoEditText.setText(null);
        }
    }
}

















