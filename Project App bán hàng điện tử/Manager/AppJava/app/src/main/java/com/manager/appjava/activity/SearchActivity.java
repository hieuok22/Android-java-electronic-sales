package com.manager.appjava.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.manager.appjava.R;
import com.manager.appjava.adapter.DienThoaiAdapter;
import com.manager.appjava.model.SamPhamMoi;
import com.manager.appjava.retrofit.ApiBanhang;
import com.manager.appjava.retrofit.RetrofitClient;
import com.manager.appjava.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    DienThoaiAdapter adapterDt;
    List<SamPhamMoi> samPhamMoiList;
    ApiBanhang apiBanhang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        ActionToolBar();
    }

    private void initView() {
        samPhamMoiList = new ArrayList<>();
        apiBanhang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanhang.class);
        editSearch = findViewById(R.id.editsearch);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycleview_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    samPhamMoiList.clear();
                    adapterDt = new DienThoaiAdapter(getApplicationContext(), samPhamMoiList);
                    recyclerView.setAdapter(adapterDt);
                }else {
                    getDataSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void getDataSearch(String s) {
        samPhamMoiList.clear();
        compositeDisposable.add(apiBanhang.search(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()) {
                                samPhamMoiList = sanPhamMoiModel.getResult();
                                adapterDt = new DienThoaiAdapter(getApplicationContext(), sanPhamMoiModel.getResult());
                                recyclerView.setAdapter(adapterDt);
                            }else {
//                                Toast.makeText(getApplicationContext(), sanPhamMoiModel.getMessage(), Toast.LENGTH_SHORT).show();
                                samPhamMoiList.clear();
                                adapterDt.notifyDataSetChanged();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));

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
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}