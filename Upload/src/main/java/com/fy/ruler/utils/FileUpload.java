package com.fy.ruler.utils;

import android.util.ArrayMap;
import android.widget.TextView;

import com.fy.ruler.retrofit.LoadService;
import com.fy.ruler.retrofit.RequestUtils;
import com.fy.ruler.retrofit.up.LoadCallBack;
import com.fy.ruler.retrofit.up.UploadOnSubscribe;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FileUpload {
    public static void uploadFiles(List<String> files, final TextView textView) {
        UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe();
        ArrayMap<String, Object> params = new ArrayMap<>();
        params.put("filePathList", files);
        params.put("UploadOnSubscribe", uploadOnSubscribe);

        Observable.merge(Observable.create(uploadOnSubscribe), RequestUtils.create(LoadService.class).uploadFile(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoadCallBack<Object>() {
                    @Override
                    protected void onProgress(String percent) {
                        L.e("onProgress:" + percent);
                        textView.setText(percent + "%");
                    }

                    @Override
                    protected void onSuccess(Object t) {
                        L.e("onSuccess:" + t);
                    }
                });
    }
}
