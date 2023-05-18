package com.manager.appjava.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.manager.appjava.R;
import com.manager.appjava.adapter.LoaiSpAdapter;
import com.manager.appjava.adapter.SanPhamMoiAdapter;
import com.manager.appjava.model.LoaiSp;
import com.manager.appjava.model.SamPhamMoi;
import com.manager.appjava.model.User;
import com.manager.appjava.retrofit.ApiBanhang;
import com.manager.appjava.retrofit.RetrofitClient;
import com.manager.appjava.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerlayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanhang apiBanhang;
    List<SamPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanhang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanhang.class);
        Paper.init(this);
        if(Paper.book().read("user") != null) {
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        getToken();

        Anhxa();
        ActionBar();

        if(isConnected(this)) {
            ActionviewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEvenClick();
        }else {
            Toast.makeText(getApplicationContext(), "Không có internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }
    }
    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(!TextUtils.isEmpty(s)) {
                            compositeDisposable.add(apiBanhang.updateToken(Utils.user_current.getId(),s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {

                                            }, throwable -> {
                                                Log.d("log", throwable.getMessage());
                                            }
                                    ));

                        }
                    }
                });
    }

    private void getEvenClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i) {
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        dienthoai.putExtra("loai", 1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        laptop.putExtra("loai", 2);
                        startActivity(laptop);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        Intent quanli = new Intent(getApplicationContext(), QuanLiActivity.class);
                        startActivity(quanli);
                        break;

                    case 7:
                        Intent chat = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(chat);
                        break;
                    case 8:
                        Intent thongke = new Intent(getApplicationContext(), ThongKeActivity.class);
                        startActivity(thongke);
                        break;
                    case 9:
                        //xoa key user
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanhang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()) {
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với server"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
                ));
    }

    private void getLoaiSanPham() {
            compositeDisposable.add(apiBanhang.getLoaiSp()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            loaispModel  -> {
                                if(loaispModel.isSuccess()) {
                                    mangloaisp = loaispModel.getResut();
                                    mangloaisp.add(new LoaiSp("Quản lí", "https://hieuok22.000webhostapp.com/8.png"));
                                    mangloaisp.add(new LoaiSp("Chat", "https://hieuok22.000webhostapp.com/9.png"));
                                    mangloaisp.add(new LoaiSp("Thống kê", "https://hieuok22.000webhostapp.com/10.png"));

                                    mangloaisp.add(new LoaiSp("Đăng xuất", "https://hieuok22.000webhostapp.com/7.png"));
                                    loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                                    listViewManHinhChinh.setAdapter(loaiSpAdapter);
                                }
                            }
                            ,
                            throwable -> {
                                // Xử lý ngoại lệ ở đây
                                throwable.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Lỗi khi tải danh sách loại sản phẩm", Toast.LENGTH_LONG).show();
                            }
                    ));
        }
        private void ActionviewFlipper() {
        List<String> manquangcao = new ArrayList<>();

            manquangcao.add("https://hieuok22.000webhostapp.com/laptop1.png");
            manquangcao.add("https://hieuok22.000webhostapp.com/laptop1.png");
            manquangcao.add("https://hieuok22.000webhostapp.com/laptop3.png");
            manquangcao.add("https://hieuok22.000webhostapp.com/laptop4.png");
            manquangcao.add("https://hieuok22.000webhostapp.com/laptop5.png");

            for(int i = 0; i <manquangcao.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(manquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }
    private void ActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.natigationview);
        drawerlayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();

        if(Utils.manggiohang == null) {
            Utils.manggiohang = new ArrayList<>();
        } else {
            int totalItem = 0;
            for(int i=0; i<Utils.manggiohang.size();i++) {
                totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);

            }
        });
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i=0; i<Utils.manggiohang.size();i++) {
            totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //nho them quyen vao khong bi loi
        NetworkInfo moblie = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null && wifi.isConnected()) || (moblie
        != null && moblie.isConnected())  ){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}