package com.example.syama.mymemopp;

import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * メモ管理用のContentProvider
 */

public class MemoProvider extends ContentProvider {

    //<authority>
    private static final String AUTHORITY =
            "com.example.syama.mymemopp.memo";
    //<path>
    private static final String CONTENT_PATH = "files";

    //MINEタイプのプレフィックス
    //複数要素にはvnd.android.cursor.dirを
    //単一要素にはvnd.android.cursor.itemを使用することとされている
    public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir/";
    public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item/";

    //独自のMIMEタイプを設定する
    public static final String MIME_ITEM = "vnd.memoapp.memo";
    public static final String MIME_TYPE_MULTIPLE = MIME_DIR_PREFIX + MIME_ITEM;
    public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + MIME_ITEM;

    //このContentProviderがハンドルするURI
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);

    //メモリストのリクエスト
    private static final int URI_MATCH_MEMO_LIST = 1;
    //単一のメモのリクエスト
    private static final int URI_MATCH_MEMO_ITEM = 2;

    //URIとの一致をチェックするUriMathcer
    private static final UriMatcher sMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //idが指定されていない場合
        sMatcher.addURI(AUTHORITY, CONTENT_PATH, URI_MATCH_MEMO_LIST);
        //idが指定されている場合
        sMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", URI_MATCH_MEMO_ITEM);
    }

    //データの保管に使用するデータベース
    private SQLiteDatabase mDatebase;

    @Override
    public boolean onCreate() {
        //SQLiteDatabaseオブジェクトを取得する
        MemoDBHelper helper = new MemoDBHelper(getContext());
        mDatebase = helper.getWritableDatabase();
        return true;
    }

    private boolean checkSignaturePermission() {
        // 自プロセスのPID
        int myPid = android.os.Process.myPid();
        // 呼び出し元のPID
        int callingPid = Binder.getCallingPid();

        // 自身で使用しているなら、チェック不要
        if (myPid == callingPid) {
            return true;
        }

        Context context = getContext();

        // onCreate()が呼ばれていない段階だと、getContext()はnullを返す
        if (context == null) {
            return false;
        }

        PackageManager packagemanager = context.getPackageManager();

        // 自身のパッケージ名
        String myPackage = context.getPackageName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // API Level 19以上なら、getCallingPackage()が使用できる
            String callingPackage = getCallingPackage();

            // PackageManager.checkSignaturesは、2つのアプリの署名を比較して、
            // SIGNATURE_MATCH （一致する）
            // SIGNATURE_NO_MATCH （一致しない）
            // SIGNATURE_UNKNOWN_PACKAGE（そのパッケージのアプリが見つからない）
            // などを返す
            // ここでは、一致している場合にtrueを返す
            return packagemanager.checkSignatures(myPackage, callingPackage)
                    == PackageManager.SIGNATURE_MATCH;
        }

        // API Levelが19未満の場合、プロセスIDから該当するパッケージをリストアップする。
        ActivityManager activitymanager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        HashSet<String> callerPackages = new HashSet<>();

        // 実行中のプロセス情報の一覧を取得する
        List<ActivityManager.RunningAppProcessInfo> processes
                = activitymanager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            // 呼び出し元のプロセスIDと一致するパッケージをHashSetに入れていく
            if (processInfo.pid == callingPid) {
                // 1プロセスに1パッケージとは限らない
                Collections.addAll(callerPackages, processInfo.pkgList);
            }
        }

        // 該当するパッケージの中に署名が一致するものがあればtrueを、そうでなければfalseを返す
        for (String packageName : callerPackages) {
            if (packagemanager.checkSignatures(myPackage, packageName)
                    == PackageManager.SIGNATURE_MATCH)
                return true;
        }
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {

        //URIが正しいことをチェックしておく
        int match = sMatcher.match(uri);

        Cursor cursor;
        switch (match) {
            case URI_MATCH_MEMO_LIST:
                cursor = mDatebase.query(MemoDBHelper.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case URI_MATCH_MEMO_ITEM:
                //IDがURIで指定されている場合
                //URIの最後のセグメントにIDがつく
                String id = uri.getLastPathSegment();

                cursor = mDatebase.query(MemoDBHelper.TABLE_NAME,
                        //条件にIDを追加する
                        projection, MemoDBHelper._ID + "=" + id
                                + (TextUtils.isEmpty(selection) ?
                                "" : " AND (" + selection + ")"),
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("invalid uri: " + uri);
        }

        //指定したURIへの通知イベントを受信するようにする
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        //呼び出し元の署名をチェックする
        if (!checkSignaturePermission()) {
            //署名が異なる場合にはSecurityExceptionをスローする
            throw new SecurityException();
        }

        //入力値の検証を行う
        if (!validateInput(values)) {
            //入力値がおかしい場合にはIllegalArgumentExceptionをスローする
            throw new IllegalArgumentException("invalid values");
        }

        //URIが正しいことをチェックしておく
        int match = sMatcher.match(uri);

        //IDを指定してinsertはおかしいので、IDなし以外は例外をスローする
        if (match == URI_MATCH_MEMO_LIST) {
            long id = mDatebase.insertOrThrow(MemoDBHelper.TABLE_NAME,
                    null, values);

            if (id >= 0) {
                //渡されたURIに、IDを付けて返す
                Uri newuUri = Uri.withAppendedPath(CONTENT_URI,
                        String.valueOf(id));
                //変更を通知する
                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(newuUri, null);
                }

                return newuUri;
            } else {
                //insertに失敗した場合
                return null;
            }
        }

        throw new IllegalArgumentException("invalid uri: " + uri);
    }

    private boolean validateInput(ContentValues values) {
        // 本来であれば、入力値の検証をここで行います。
        return true;
    }
}
