package com.manager.appjava.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.manager.appjava.R;
import com.manager.appjava.databinding.ActivityThemActivityBinding;
import com.manager.appjava.model.MessageModel;
import com.manager.appjava.model.SamPhamMoi;
import com.manager.appjava.retrofit.ApiBanhang;
import com.manager.appjava.retrofit.RetrofitClient;
import com.manager.appjava.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemSPActivity extends AppCompatActivity {
    Spinner spinner;
    int loai = 0;
    ApiBanhang apiBanhang;
    ActivityThemActivityBinding binding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String mediaPath;
    SamPhamMoi samPhamSua;
    boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemActivityBinding.inflate(getLayoutInflater());
        apiBanhang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanhang.class);
        setContentView(binding.getRoot());
        initView();
        initData();

        Intent intent = getIntent();
        samPhamSua = (SamPhamMoi) intent.getSerializableExtra("Sửa");
        if(samPhamSua == null) {
            //them moi
            flag = false;
        }else {
            //sua
            flag = true;
            binding.btnthem.setText("Sửa sản phẩm");
            //show data
            binding.mota.setText(samPhamSua.getMota());
            binding.giasp.setText(samPhamSua.getGiasp());
            binding.tensp.setText(samPhamSua.getTensp());
            binding.hinhanh.setText(samPhamSua.getHinhanh());
            binding.spinnerLoai.setSelection(samPhamSua.getLoai());

        }

    }

    private void initData() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Vui lòng chọn loại");
        stringList.add("Loại 1");
        stringList.add("Loại 2");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loai = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == false) {
                    themsanpham();
                }else {
                    suaSanPham();
                }

            }
        });
        binding.imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ThemSPActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    private void suaSanPham() {
        String str_ten = binding.tensp.getText().toString().trim();
        String str_gia = binding.giasp.getText().toString().trim();
        String str_mota = binding.mota.getText().toString().trim();
        String str_hinhanh = binding.hinhanh.getText().toString().trim();
        if(TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_hinhanh) || loai ==0) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        }else {
            compositeDisposable.add(apiBanhang.updatesp(str_ten, str_gia, str_hinhanh, str_mota, loai, samPhamSua.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if(messageModel.isSucces()) {
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable ->  {
                                Toast.makeText(getApplicationContext() ,"throwable.getMessage()", Toast.LENGTH_SHORT).show();
                            }
                    ));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath = data.getDataString();
        uploadMultipleFiles();
        Log.d("log", "onActivityResult: "+mediaPath);
    }

    private void themsanpham() {
        String str_ten = binding.tensp.getText().toString().trim();
        String str_gia = binding.giasp.getText().toString().trim();
        String str_mota = binding.mota.getText().toString().trim();
        String str_hinhanh = binding.hinhanh.getText().toString().trim();
        if(TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_hinhanh) || loai ==0) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        }else {
            compositeDisposable.add(apiBanhang.insertsp(str_ten, str_gia, str_hinhanh, str_mota, (loai-1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if(messageModel.isSucces()) {
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable ->  {
                                Toast.makeText(getApplicationContext() ,throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
        }
    }
   private  String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null,null,null, null);
        if(cursor == null) {
            result = uri.getPath();

        }else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();

        }
        return result;
   }
    private void uploadMultipleFiles() {

        Uri uri = Uri.parse(mediaPath);
        File file = new File(getPath(uri));
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        Call<MessageModel> call = apiBanhang.uploadFile(fileToUpload1);
        call.enqueue(new Callback< MessageModel >() {
            @Override
            public void onResponse(Call < MessageModel > call, Response< MessageModel > response) {
                MessageModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSucces()) {
                        binding.hinhanh.setText(serverResponse.getName());
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.v("Response", serverResponse.toString());
                }
            }
            @Override
            public void onFailure(Call < MessageModel > call, Throwable t) {
                Log.d("log", t.getMessage());
            }
        });
    }
    private void initView() {
        spinner = findViewById(R.id.spinner_loai);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}