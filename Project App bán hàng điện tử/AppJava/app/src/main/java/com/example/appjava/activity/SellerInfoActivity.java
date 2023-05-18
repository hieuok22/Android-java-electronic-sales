package com.example.appjava.activity;

// SellerInfoActivity.java

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appjava.R;

public class SellerInfoActivity extends AppCompatActivity {
    private TextView textViewName, textViewAddress, textViewPhone;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_info);

        textViewName = findViewById(R.id.textViewName);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewPhone = findViewById(R.id.textViewPhone);

        // Lấy thông tin của người bán hàng từ Intent hoặc từ nguồn dữ liệu khác
        String sellerName = "Lê Văn Hiếu";
        String sellerAddress = "Hòa Hải - Ngũ Hành Sơn - Đà Nẵng";
        String sellerPhone = "0399824061";

        // Hiển thị thông tin của người bán hàng
        textViewName.setText(sellerName);
        textViewAddress.setText(sellerAddress);
        textViewPhone.setText(sellerPhone);
    }
}
