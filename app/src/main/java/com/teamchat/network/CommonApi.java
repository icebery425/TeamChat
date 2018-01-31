package com.teamchat.network;


import com.teamchat.model.ResponseInfo;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * GET , POSET 请求传参方式不同
 * 若有参数是可选的，当参数不需要传时，设置参数值为null，则框架会自动忽略
 *
 *  @FormUrlEncoded
    @POST("warehouse/productsend.json")
    Observable<ResponseInfo> outSku(@Field("id") String id, @Field("warehouseId") int warehouseId, @Field("storeId") int storeId, @Field("items") String items);
 */
public interface CommonApi {

    @POST("user/applogin")
    Observable<ResponseInfo> loginEx(@Body RequestBody route);

    @POST("base/sendVerifyCode")
    Observable<ResponseInfo> getVerifyCode(@Body RequestBody route);

    @POST()
    Observable<ResponseInfo> getVerifyCodeEx(@Url String url, @Body RequestBody route);

    @POST("user/logout")
    Observable<ResponseInfo> logout();

    @POST("user/forgetPassword")
    Observable<ResponseInfo> forgetPwd(@Body RequestBody route);

    @GET("schoollist.xml")
    Observable<String> schoolUrl();

    @GET("base/schoollist")
    Observable<ResponseInfo> schoolList();

    @GET("base/teacherlist")
    Observable<ResponseInfo> teacherList(@Query("schoolUid") String schoolId, @Query("keyword") String keyword);


    //通知详情
    @GET("notice/{uid}")
    Observable<ResponseInfo> getMsgDetail(@Path("uid") String uid);

    //事件地点
    @GET("teacher/address")
    Observable<ResponseInfo> getEventAddressList();

    /**
     * 返回问卷调查列表
     * @return
     */
    @GET("questionnaire/list")
    Observable<ResponseInfo> questionnaireList();

    //获取事件类型
    @GET("event/type")
    Observable<ResponseInfo> getEventTypeList();

    /**
     * 返回问卷调查详情
     * @return
     */
    @GET("questionnaire/{uid}")
    Observable<ResponseInfo> questionnaireDetail(@Path("uid") String uid);


    /**
     * 提交问卷
     * @return
     */
    @POST("questionnaire/commit")
    Observable<ResponseInfo> questionnaireCommit(@Body RequestBody route);

    /**
     * 获取最新通告
     */
    @GET("base/newnotice")
    Observable<ResponseInfo> getLatestNotice();




}