package com.manager.appjava.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.manager.appjava.R;
import com.manager.appjava.retrofit.ApiBanhang;
import com.manager.appjava.retrofit.RetrofitClient;
import com.manager.appjava.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ResetpassActivity extends AppCompatActivity {
    EditText email;
    AppCompatButton btnReset;
    ApiBanhang apiBanhang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        initView();
        initControll();
    }

    private void initControll() {
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString().trim();
                if(TextUtils.isEmpty(str_email)) {
                    Toast.makeText(ResetpassActivity.this, "Bạn chưa nhập email", Toast.LENGTH_SHORT).show();

                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    compositeDisposable.add(apiBanhang.resetPass(str_email)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                               userModel -> {
                                    if(userModel.isSuccess()) {
                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                        Intent intent  = new Intent(getApplicationContext(), DangNhapActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                   progressBar.setVisibility(View.INVISIBLE);
                               },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                            ));
                }
            }
        });
    }
    private void initView() {
        apiBanhang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanhang.class);
        email = findViewById(R.id.edtResetpass);
        btnReset = findViewById(R.id.btnResetpass);
        progressBar = findViewById(R.id.progressbar);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}