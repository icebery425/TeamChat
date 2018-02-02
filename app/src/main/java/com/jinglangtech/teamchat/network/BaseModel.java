package com.jinglangtech.teamchat.network;


import android.text.TextUtils;

import com.jinglangtech.teamchat.listener.BaseListener;
import com.jinglangtech.teamchat.model.ResponseInfo;
import java.util.List;

public class BaseModel {

    /**
     * 针对json字符串处理，可根据需要重载，或者创建其他方法处理
     * @param responseInfo
     * @param baseListener
     */
    public void dealJsonStr(ResponseInfo responseInfo, BaseListener baseListener){

        if (responseInfo.err == 0){
            if (responseInfo.data != null){
                Object infoObj = responseInfo.parseInfo(baseListener.getSubClass());
                List<Object> listObj = responseInfo.parseList(baseListener.getSubClass());
                baseListener.responseResult(infoObj,listObj, responseInfo.err,responseInfo.status);
            }else{
                baseListener.responseResult(null, null,responseInfo.err, responseInfo.status);
            }
        }else{
            baseListener.requestFailed(responseInfo.status,responseInfo.err, responseInfo.desc);
        }

    }

    public void dealJsonArrayStr(ResponseInfo responseInfo, BaseListener baseListener){

        if (responseInfo.status){
            if (responseInfo.data != null){
                Object infoObj = responseInfo.parseInfo(baseListener.getSubClass());
                List<Object> listObj = responseInfo.parseList(baseListener.getSubClass());
                baseListener.responseListResult(infoObj,listObj, responseInfo.pageinfo,responseInfo.err,responseInfo.status);
            }else{
                baseListener.responseListResult(null, null,null,responseInfo.err, responseInfo.status);
            }
        }else{
            baseListener.requestFailed(responseInfo.status,responseInfo.err, responseInfo.desc);
        }

    }

    /**
     * 处理比较简单的响应数据，例如 注销，信息修改保存等接口放回的数据.避免创建类
     *
     */
    public void dealSimpleStr(ResponseInfo responseInfo, BaseListener baseListener){

        if (responseInfo.status){
            if (responseInfo.data != null && responseInfo.data != null){
                baseListener.responseResult(responseInfo.data,null, responseInfo.err,responseInfo.status);
            }else{
                baseListener.responseResult(null,null, responseInfo.err,responseInfo.status);
            }
        }else{
            baseListener.requestFailed(responseInfo.status,responseInfo.err, responseInfo.desc);
        }

    }

    /**
     * 针对纯字符串处理，可根据需要重载，或者创建其他方法处理
     * @param  str
     * @param baseListener
     */
    public void dealStr(String str, BaseListener baseListener){

        if (!TextUtils.isEmpty(str)){

        }else{

        }

    }
}
