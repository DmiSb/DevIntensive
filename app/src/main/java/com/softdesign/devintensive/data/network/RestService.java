package com.softdesign.devintensive.data.network;

import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserEditPhotoRes;
import com.softdesign.devintensive.data.network.res.UserEditRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserLoginRes;
import com.softdesign.devintensive.data.network.res.UserModel;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 *
 */
public interface RestService {

    @POST("login")
    Call<UserModelRes> loginUser (@Body UserLoginReq req);

    @Multipart
    @POST("user/{userId}/publicValues/profilePhoto")
    Call<UserEditPhotoRes> editUserPhoto(
            @Path("userId") String userId,
            @Part MultipartBody.Part bodyPart);

    @GET("user/{userId}")
    Call<UserLoginRes> checkToken(@Path("userId") String userId);

    @GET("user/list?orderBy=rating")
    Call<UserListRes> getUserList();
}
