package com.teamchat.network;


import com.teamchat.model.ResponseInfo;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    //用户登陆
    @POST("api/login")
    Observable<ResponseInfo> login(@Body RequestBody route);

    //获取用户信息
    @GET("/api/userinfo/:{id}")
    Observable<ResponseInfo> getUserInfo(@Path("id") String uid);

    //编辑用户信息
    @PUT("api/username")
    Observable<ResponseInfo> modifyUserInfo(@Body RequestBody route);

    //修改密码
    @POST("api/userpsd")
    Observable<ResponseInfo> modifyPwd(@Body RequestBody route);

    //获取用户所在房间列表
    @GET("api/userroom/")
    Observable<ResponseInfo> getRoomList();

    //用户向房间发送消息
    @POST("api/msgtoroom")
    Observable<ResponseInfo> sendToRoom(@Body RequestBody route);

    //向用户发送消息
    @POST("api/msgtouser")
    Observable<ResponseInfo> sendToUser(@Body RequestBody route);

    //读取消息
    @GET("api/msg")
    Observable<ResponseInfo> getMessage();

    //消息已读
    @DELETE("api/readmsg")
    Observable<ResponseInfo> readMessage(@Body RequestBody route);



}