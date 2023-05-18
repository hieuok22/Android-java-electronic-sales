package com.manager.appjava.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manager.appjava.R;
import com.manager.appjava.retrofit.ApiBanhang;
import com.manager.appjava.retrofit.RetrofitClient;
import com.manager.appjava.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {
    EditText email, pass, repass, mobile, username;
    AppCompatButton button;
    ApiBanhang apiBanhang;
    FirebaseAuth firebaseAuth;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        initView();
        initControll();
    }

    private void initControll() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKi();
            }
        });
    }

    private void dangKi() {
        String str_email = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_user = username.getText().toString().trim();

        if(TextUtils.isEmpty(str_email)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(str_pass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Pass", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(str_repass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập RePass", Toast.LENGTH_SHORT).show();

        } else if(TextUtils.isEmpty(str_mobile)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Moblie", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(str_user)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập User", Toast.LENGTH_SHORT).show();

        }else{
            if (str_pass.equals(str_repass)) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(str_email,str_pass)
                        .addOnCompleteListener(DangKiActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if(user !=  null) {
                                        postData( str_email,  str_pass,  str_user,  str_mobile, user.getUid());
                                    }
                                } else  {
                                    Toast.makeText(getApplicationContext(), "Email đã tồn tại hoặc không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }else {
                Toast.makeText(this, "Pass chưa khớp", Toast.LENGTH_SHORT).show();
            }
        }
    }
private  void postData(String str_email, String str_pass, String str_user, String str_mobile, String uid) {
    //post data
    compositeDisposable.add(apiBanhang.dangKi(str_email,str_pass,str_user,str_mobile,uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    userModel -> {
                        if(userModel.isSuccess()) {
                            Utils.user_current.setEmail(str_email);
                            Utils.user_current.setPass(str_pass);
                            Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                            startActivity(intent);
                            finish();
                        }else  {
                            Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();                                    }
                    },throwable -> {
                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            ));
}
    private void initView() {
        apiBanhang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanhang.class);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);
        mobile = findViewById(R.id.mobile);
        username = findViewById(R.id.username);
        button = findViewById(R.id.btndangki);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}