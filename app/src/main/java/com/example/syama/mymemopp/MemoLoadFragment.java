package com.example.syama.mymemopp;

import android.app.ListFragment;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

/**
 * Created by SoushinYamaoka on 2018/04/01.
 */

public class MemoLoadFragment extends ListFragment {

    //ユーザーが一覧をタップしたイベントのコールバック
    public interface MemoLoadFragmentListener{
        void onMemoSelected(@NonNull Uri uri);
    }

    //アクティビティがインターフェイスを実装しているかチェックする
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if (!(context instanceof MemoLoadFragmentListener)){
            //アクティビティがMemoLoadFragmentListenerを実装していない場合
            throw new RuntimeException(context.getClass().getSimpleName()
                + " does not implement MemoLoadFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);

        //ヘッダーを追加する
        View heaader = LayoutInflater.from(getActivity()).inflate(
                R.layout.memo_list_create, null);
        getListView().addHeaderView(heaader);

        //データベースを検索する
        Cursor cursor = MemoRepository.query(getActivity());

        //アダプターをセットする
        MemoAdapter mAdapter = new MemoAdapter(getActivity(), cursor, true);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        if (position == 0){
            //ヘッダーの場合
            ((MemoLoadFragmentListener)getActivity()).onMemoSelected(null);

        } else {
            //リストの項目の場合
            Uri selectedItem =
                    ContentUris.withAppendedId(MemoProvider.CONTENT_URI, id);
            ((MemoLoadFragmentListener)getActivity()).onMemoSelected(selectedItem);
        }
    }
}





















