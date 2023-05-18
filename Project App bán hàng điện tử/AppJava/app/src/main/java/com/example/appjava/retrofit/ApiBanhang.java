package com.example.appjava.retrofit;


import com.example.appjava.model.DonHangModel;
import com.example.appjava.model.LoaispModel;
import com.example.appjava.model.MessageModel;
import com.example.appjava.model.SamPhamMoi;
import com.example.appjava.model.SanPhamMoiModel;
import com.example.appjava.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiBanhang {
    @GET("getloaisp.php")
    Observable<LoaispModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    //POST DATA
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> getSanPham (
        @Field("page") int page,
        @Field("loai") int loai
    );
    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi (
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid
    );
    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangNhap (
            @Field("email") String email,
            @Field("pass") String pass
    );
    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken (
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("updatemomo.php")
    @FormUrlEncoded
    Observable<MessageModel> updateMomo (
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass (
            @Field("email") String email
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<MessageModel> createOder (
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") String id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang (
            @Field("iduser") int id
    );
    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamMoiModel> search (
            @Field("search") String search
    );

    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> gettoken (
            @Field("status") int status
    );

}
