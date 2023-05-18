package com.manager.appjava.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.appjava.Interface.ItemClickListener;
import com.manager.appjava.R;
import com.manager.appjava.model.DonHang;
import com.manager.appjava.model.EventBus.DonHangEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private  RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> listdonhang;

    public DonHangAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);
        holder.txtdonhang.setText("Đơn hàng: " + donHang.getId());
        holder.diachi.setText("Địa chỉ: "+donHang.getDiachi());
        holder.trangthai.setText(trangThaiDon(donHang.getTrangthai()));


        LinearLayoutManager layoutManager = new LinearLayoutManager(
          holder.rechitiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        // adapter chitiet
        ChitietAdapter chitietAdapter = new ChitietAdapter(context,donHang.getItem());
        holder.rechitiet.setLayoutManager(layoutManager);
        holder.rechitiet.setAdapter(chitietAdapter);
        holder.rechitiet.setRecycledViewPool(viewPool);
        holder.setListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, Boolean isLongClick) {
                if(isLongClick) {
                    EventBus.getDefault().postSticky(new DonHangEvent(donHang));

                }
            }
        });

    }
private String trangThaiDon(int status) {
        String result = "";
        switch (status) {
            case 0:
                result = "Đơn hàng đang được xử lí";
                break;
            case 1:
                result = "Đơn hàng đã chấp nhận";
                break;
            case 2:
                result = "Đơn hàng đã giao cho đơn vị vận chuyển";
                break;
            case 3:
                result = "Thành công";
                break;
            case 4:
                result = "Đơn hàng đã hủy";
                break;

        }
        return result;
}
    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView txtdonhang, trangthai, diachi;
        RecyclerView rechitiet;
        ItemClickListener listener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang = itemView.findViewById(R.id.iddonhang);
            trangthai = itemView.findViewById(R.id.tinhtrang);
            diachi = itemView.findViewById(R.id.diachi_donhang);
            rechitiet = itemView.findViewById(R.id.recycleview_chitiet);
            itemView.setOnLongClickListener(this);
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onClick(v, getAdapterPosition(), true);
            return false;
        }
    }
}
