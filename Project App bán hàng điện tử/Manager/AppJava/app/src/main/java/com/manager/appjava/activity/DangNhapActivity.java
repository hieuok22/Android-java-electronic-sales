package com.manager.appjava.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangki, txtResetpass;
    EditText email, pass;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    AppCompatButton btndangnhap;
    ApiBanhang apiBanhang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControll();
    }

    private void initControll() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DangKiActivity.class);
                startActivity(intent);
            }
        });
        txtResetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetpassActivity.class);
                startActivity(intent);
            }
        });
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();

                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(str_pass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Pass", Toast.LENGTH_SHORT).show();
                } else {
                    //save
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);
                    if(firebaseUser !=  null) {
                        //user da co dang nhap firebase
                        dangNhap(str_email,str_pass);


                    }else {
                        //user da sigout
                        firebaseAuth.signInWithEmailAndPassword(str_email,str_pass)
                                .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {
                                            dangNhap(str_email,str_pass);

                                        }
                                    }
                                });
                    }


                }
            }
        });
    }

    private void initView() {
        Paper.init(this);
        apiBanhang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanhang.class);
        txtdangki = findViewById(R.id.txtdangki);
        txtResetpass = findViewById(R.id.txtResetpass);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //read data
        if(Paper.book().read("email") != null && Paper.book().read("pass") != null ) {
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
            if(Paper.book().read("islogin") != null) {
                boolean flag = Paper.book().read("islogin");
                if(flag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            dangNhap(Paper.book().read("email"),Paper.book().read("pass") );
                        }
                    }, 1000);
                }
            }
        }
    }

    private void dangNhap(String email, String pass) {


        compositeDisposable.add(apiBanhang.dangNhap(email,pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()) {
                                isLogin = true;
                                Paper.book().write("islogin", isLogin);
                                Utils.user_current = userModel.getResult().get(0);
                                // luu lai thong tin nguoi dung
                                Paper.book().write("user", userModel.getResult().get(0));
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },throwable -> {
                            Toast.makeText(getApplicationContext(),throwable.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null) {
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}