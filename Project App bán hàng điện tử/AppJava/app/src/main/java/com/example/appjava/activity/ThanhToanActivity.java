package com.example.appjava.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appjava.R;
import com.example.appjava.model.CreateOrder;
import com.example.appjava.model.GioHang;
import com.example.appjava.model.NotiSenData;
import com.example.appjava.retrofit.ApiBanhang;
import com.example.appjava.retrofit.ApiPushNofication;
import com.example.appjava.retrofit.RetrofitClient;
import com.example.appjava.retrofit.RetrofitClientNoti;
import com.example.appjava.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import vn.momo.momo_partner.MoMoParameterNamePayment;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;
import vn.momo.momo_partner.AppMoMoLib;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txttongtien, txtsdt, txtemail;
    EditText edtdiachi;
    AppCompatButton btndahang,btnmomo ,btnzalo;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanhang apiBanhang;
    long tongtien;
    int totalItem;
    int iddonhang;

    private  String amount = "100000";
    private  String fee = "0";
    int enviroment =0;
    private String merchantName = "Thanh toán đơn hàng";
    private String merchantCode = "MOMOCOMU20200526";
    private String getMerchantNameLabel = "LeHieu";
    private  String description = "mua hàng online";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
        //zalo
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        initView();
        countItem();
        initControll();
    }

    private void countItem() {
        totalItem = 0;
        for(int i=0; i<Utils.mangmuahang.size();i++) {
            totalItem = totalItem+ Utils.mangmuahang.get(i).getSoluong();
        }
    }
    //Get token through MoMo app
    private void requestPayment(String iddonhang) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);

        Map<String, Object> eventValue = new HashMap<>();
        //client Required may cai doi tac do dang ki ben kia , có cái nào test thôi đc k a khopng
        // tkhong dang ki thi khong dung dc no , lấy đại của ai 1 cái đc k a

        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", iddonhang); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", iddonhang); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", "0"); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Lê Văn Hiếu");
            objExtraData.put("movie_format", "2D");
            objExtraData.put("ticket", "{\"ticket\":{\"01\":{\"type\":\"std\",\"price\":110000,\"qty\":3}}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());
        eventValue.put(MoMoParameterNamePayment.EXTRA_DATA, objExtraData.toString());
        eventValue.put(MoMoParameterNamePayment.REQUEST_TYPE, "payment");
        eventValue.put(MoMoParameterNamePayment.LANGUAGE, "vi");
        eventValue.put(MoMoParameterNamePayment.EXTRA, "");
        //Request momo app
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);
        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);
    }
    //Get token callback from MoMo app an submit to server side
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("thanhcong",data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response

                    compositeDisposable.add(apiBanhang.updateMomo(iddonhang, token)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        if (messageModel.isSuccess()) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    },
                                    throwable -> {
                                        Log.d("error", throwable.getMessage());
                                    }
                            ));





                String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
                        Log.d("Thành công","Không thành công");
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    Log.d("Thành công","Không thành công");
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("Thành công","Không thành công");
                } else {
                    //TOKEN FAIL
                    Log.d("Thành công","Không thành công");
                }
            } else {
                Log.d("Thành công","Không thành công");
            }
        } else {
            Log.d("Thành công","Không thành công");
        }
    }

    private void initControll() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien = getIntent().getLongExtra("tongtien", 0);
        txttongtien.setText(decimalFormat.format(tongtien));
        txtemail.setText(Utils.user_current.getEmail());
        txtsdt.setText(Utils.user_current.getMobile());


        btndahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_diachi = edtdiachi.getText().toString().trim();
                if (TextUtils.isEmpty(str_diachi)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                } else {
                    // post data
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();
                    Log.d("test", new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanhang.createOder(str_email, str_sdt, String.valueOf(tongtien), String.valueOf(id), str_diachi, totalItem, new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        PushNotiUser();
                                        Toast.makeText(getApplicationContext(), "Thành công", Toast.LENGTH_SHORT).show();
                                        //clear manggiohang bang cach chay qua mangmuahang va clear
                                        for (int i = 0; i < Utils.mangmuahang.size(); i++) {
                                            GioHang gioHang = Utils.mangmuahang.get(i);
                                            if (Utils.manggiohang.contains(gioHang)) {
                                                Utils.manggiohang.remove(gioHang);
                                            }
                                        }
                                        Utils.mangmuahang.clear();
                                        Paper.book().write("giohang", Utils.manggiohang);
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        });

        btnmomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_diachi = edtdiachi.getText().toString().trim();
                if (TextUtils.isEmpty(str_diachi)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                } else {
                    // post data
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();
                    Log.d("test", new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanhang.createOder(str_email, str_sdt, String.valueOf(tongtien), String.valueOf(id), str_diachi, totalItem, new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        PushNotiUser();
                                        Toast.makeText(getApplicationContext(), "Thanh toán momo", Toast.LENGTH_SHORT).show();
                                        //clear manggiohang bang cach chay qua mangmuahang va clear
                                        for (int i = 0; i < Utils.mangmuahang.size(); i++) {
                                            GioHang gioHang = Utils.mangmuahang.get(i);
                                            if (Utils.manggiohang.contains(gioHang)) {
                                                Utils.manggiohang.remove(gioHang);
                                            }
                                        }
                                        Utils.mangmuahang.clear();
                                        Paper.book().write("giohang", Utils.manggiohang);

                                        iddonhang = Integer.parseInt(messageModel.getIddonhang());
                                        requestPayment(messageModel.getIddonhang());

                                    },
                                    throwable -> {

                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        });
        btnzalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_diachi = edtdiachi.getText().toString().trim();
                if(TextUtils.isEmpty(str_diachi)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                }else {
                    // post data
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();
                    Log.d("test", new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanhang.createOder(str_email,str_sdt,String.valueOf(tongtien),String.valueOf(id),str_diachi,totalItem,new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        PushNotiUser();
                                        Toast.makeText(getApplicationContext(),"Thanh toán Zalo", Toast.LENGTH_SHORT).show();

                                        for (int i = 0; i < Utils.mangmuahang.size(); i++) {
                                            GioHang gioHang = Utils.mangmuahang.get(i);
                                            if (Utils.manggiohang.contains(gioHang)) {
                                                Utils.manggiohang.remove(gioHang);
                                            }
                                        }

                                        Utils.mangmuahang.clear();
                                        Paper.book().write("giohang", Utils.manggiohang);

                                        iddonhang = Integer.parseInt(messageModel.getIddonhang());
                                        requestZalo();

                                    },
                                    throwable -> {
                                        Log.d("loggg", "tai sao lai co s== null");
                                        Log.d("loggg", throwable.getMessage());
                                        //Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        });
    }

    private void requestZalo() {
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder("10000");
            String code = data.getString("return_code");
            Log.d("loggg", data.toString());

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                Log.d("loggg", token);
                ZaloPaySDK.getInstance().payOrder(ThanhToanActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String appTransID, String zpTransToken, String otaTransID) {
                        compositeDisposable.add(apiBanhang.updateMomo(iddonhang, token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messageModel -> {
                                            if (messageModel.isSuccess()) {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        },
                                        throwable -> {
                                            Log.d("error", throwable.getMessage());
                                        }
                                ));
                    }

                    @Override
                    public void onPaymentCanceled(String appTransID, String zpTransToken) {
                        // Xử lý khi người dùng hủy thanh toán
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String appTransID, String zpTransToken) {
                        Log.d("logg", "Khong thanh toan dc");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PushNotiUser() {
        //gettoken
        compositeDisposable.add(apiBanhang.gettoken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                   userModel -> {
                       if(userModel.isSuccess()) {
                           for (int i = 0; i<userModel.getResult().size();i++) {
                               Map<String, String> data = new HashMap<>();
                               data.put("title", "Thông báo");
                               data.put("body", "Bạn có đơn hàng mới");
                               NotiSenData notiSenData = new NotiSenData(userModel.getResult().get(i).getToken(), data);
                               ApiPushNofication apiPushNofication = RetrofitClientNoti.getInstance().create(ApiPushNofication.class);
                               compositeDisposable.add(apiPushNofication.sendNofitication(notiSenData)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(
                                               notiResponse -> {

                                               }, throwable -> {
                                                   Log.d("logg", throwable.getMessage());
                                               }
                                       ));
                           }
                       }

                   },throwable -> {
                       Log.d("loggg", throwable.getMessage());
                        }
                ));


    }

    private void initView() {
        apiBanhang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanhang.class);
        toolbar = findViewById(R.id.toolbar);
        txttongtien = findViewById(R.id.txttongtien);
        txtsdt = findViewById(R.id.txtsdt);
        txtemail = findViewById(R.id.txtemail);
        edtdiachi = findViewById(R.id.edtdiachi);
        btndahang = findViewById(R.id.btndathang);
        btnmomo = findViewById(R.id.btnmomo);
        btnzalo = findViewById(R.id.btnzalopay);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}