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
import android.widget.Toast;

/**
 * Created by SoushinYamaoka on 2018/04/01.
 */

public class MemoFragment extends Fragment{
    private MemoEditText mMemoEditText;

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
    public void save(){
        if(mMemoUri != null){
            //MemoのURIがあるということは、すでに一度保存したか、読み込んできたもの
            //そのため、新規作成ではなく更新する
            MemoRepository.update(getActivity(), mMemoUri,
                    mMemoEditText.getText().toString());
        } else {
            //新規作成
            MemoRepository.create(getActivity(),
                    mMemoEditText.getText().toString());
        }
        //「保存しました」と表示する
        Toast.makeText(getActivity(), "保存しました", Toast.LENGTH_SHORT).show();
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
















