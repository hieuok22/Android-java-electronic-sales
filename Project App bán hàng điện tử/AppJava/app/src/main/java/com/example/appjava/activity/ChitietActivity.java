package com.example.appjava.activity;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appjava.R;
import com.example.appjava.model.GioHang;
import com.example.appjava.model.SamPhamMoi;
import com.example.appjava.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;

import io.paperdb.Paper;

public class ChitietActivity extends AppCompatActivity {
    TextView tensp, giasp, mota;
    Button btnthem, btnytb;
    ImageView imghinhanh;
    Spinner spinner;
    Toolbar toolbar;
    SamPhamMoi samphammoi;
    NotificationBadge badge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitiet);
        initView();
        ActionToolBar();
        initData();
        initControl();
    }

    private void initControl() {
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themGioHang();
                Paper.book().write("giohang", Utils.manggiohang);
            }
        });
        btnytb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent youtube = new Intent(getApplicationContext(), YouTubeActivity.class);
                youtube.putExtra("linkvideo", samphammoi.getLinkvideo());
                startActivity(youtube);

            }
        });

    }

    private void themGioHang() {
        if(Utils.manggiohang.size() > 0) {
            boolean flag = false;
            int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
            for (int i =0; i<Utils.manggiohang.size(); i++) {
                if(Utils.manggiohang.get(i).getIdsp() == samphammoi.getId()) {
                    Utils.manggiohang.get(i).setSoluong(soluong + Utils.manggiohang.get(i).getSoluong());

                    flag = true;
                }
            }
            if(flag == false) {

                long gia = Long.parseLong(samphammoi.getGiasp());
                GioHang gioHang = new GioHang();
                gioHang.setGiasp(gia);
                gioHang.setSoluong(soluong);
                gioHang.setIdsp(samphammoi.getId());
                gioHang.setTensp(samphammoi.getTensp());
                gioHang.setHinhsp(samphammoi.getHinhanh());
                Utils.manggiohang.add(gioHang);
            }

        }else {
            int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
            long gia = Long.parseLong(samphammoi.getGiasp());
            GioHang gioHang = new GioHang();
            gioHang.setGiasp(gia);
            gioHang.setSoluong(soluong);
            gioHang.setIdsp(samphammoi.getId());
            gioHang.setTensp(samphammoi.getTensp());
            gioHang.setHinhsp(samphammoi.getHinhanh());
            Utils.manggiohang.add(gioHang);
        }
        int totalItem = 0;
        for(int i=0; i<Utils.manggiohang.size();i++) {
            totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private void initData() {
        samphammoi = (SamPhamMoi) getIntent().getSerializableExtra("chitiet");
        tensp.setText(samphammoi.getTensp());
        mota.setText(samphammoi.getMota());
        Glide.with(getApplicationContext()).load(samphammoi.getHinhanh()).into(imghinhanh);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        giasp.setText("Giá: "+decimalFormat.format(Double.parseDouble(samphammoi.getGiasp()))+ "Đ" );
        Integer[] so = new Integer[] {1,2,3,4,5,7,8,9,10};
        ArrayAdapter<Integer> adapterspin = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, so);
        spinner.setAdapter(adapterspin);
    }

    private void initView() {
        tensp = findViewById(R.id.txttensp);
        giasp = findViewById(R.id.txtgiasp);
        mota = findViewById(R.id.txtmotachitiet);
        btnthem = findViewById(R.id.btnaddgiohang);
        btnytb = findViewById(R.id.btnytb);

        spinner = findViewById(R.id.spinner);
        imghinhanh = findViewById(R.id.imgchitiet);
        toolbar = findViewById(R.id.toolbar);
        badge = findViewById(R.id.menu_sl);
        FrameLayout frameLayoutgiohang = findViewById(R.id.framegiohang);
        frameLayoutgiohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        if(Utils.manggiohang != null) {
            int totalItem = 0;
            for(int i=0; i<Utils.manggiohang.size();i++) {
                totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
            }

            badge.setText(String.valueOf(totalItem));

        }
    }
    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.manggiohang != null) {
            int totalItem = 0;
            for(int i=0; i<Utils.manggiohang.size();i++) {
                totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));

        }
    }
}