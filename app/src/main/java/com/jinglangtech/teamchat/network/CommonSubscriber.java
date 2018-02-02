package com.jinglangtech.teamchat.network;


import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.ResponseInfo;

/**
 * 公共请求回调，根据需要创建其他 BaseSubscriber 子类
 */
public class CommonSubscriber extends BaseSubscriber{


    public CommonSubscriber(BaseListener baseListener){
        super(baseListener);
    }

    //子线程回调
    @Override
    public void onStart() {
    }

    @Override
    public void onCompleted() {
    }

    //主线程回调
    @Override
    public void onNext(ResponseInfo responseInfo) {
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }
}
