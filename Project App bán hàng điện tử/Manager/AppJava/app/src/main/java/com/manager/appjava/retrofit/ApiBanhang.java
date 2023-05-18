package com.manager.appjava.retrofit;


import com.manager.appjava.model.DonHangModel;
import com.manager.appjava.model.LoaispModel;
import com.manager.appjava.model.MessageModel;
import com.manager.appjava.model.SanPhamMoiModel;
import com.manager.appjava.model.ThongKeModel;
import com.manager.appjava.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiBanhang {
    @GET("getloaisp.php")
    Observable<LoaispModel> getLoaiSp();

    @GET("getspmoi.php")
    Observable<SanPhamMoiModel> getSpMoi();

    @GET("thongke.php")
    Observable<ThongKeModel> getthongke();
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

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass (
            @Field("email") String email
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOder (
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

    @POST("xoa.php")
    @FormUrlEncoded
    Observable<MessageModel> xoaSanPham (
            @Field("id") int id
    );

    @POST("insertsp.php")
    @FormUrlEncoded
    Observable<MessageModel> insertsp (
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int loai

    );
    @POST("update.php")
    @FormUrlEncoded
    Observable<MessageModel> updatesp (
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int loai,
            @Field("id") int id

    );
    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> gettoken (
            @Field("status") int status,
            @Field("iduser") int iduser
            );
    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken (
            @Field("id") int id,
            @Field("token") String token
    );
    @POST("updatedonhang.php")
    @FormUrlEncoded
    Observable<MessageModel> updateOrder (
            @Field("id") int id,
            @Field("trangthai") int trangthai
    );
    @Multipart
    @POST("upload.php")
    Call<MessageModel> uploadFile(@Part MultipartBody.Part file);
}
