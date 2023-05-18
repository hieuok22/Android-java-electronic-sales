package com.manager.appjava.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manager.appjava.Interface.ItemClickListener;
import com.manager.appjava.R;
import com.manager.appjava.activity.ChitietActivity;
import com.manager.appjava.model.EventBus.SuaXoaEvent;
import com.manager.appjava.model.SamPhamMoi;
import com.manager.appjava.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder> {
    Context context;
    List<SamPhamMoi> array;

    public SanPhamMoiAdapter(Context context, List<SamPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi, parent, false);

        return new MyViewHolder(item);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SamPhamMoi samPhamMoi = array.get(position);
        holder.txtten.setText(samPhamMoi.getTensp());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtgia.setText("Giá: "+decimalFormat.format(Double.parseDouble(samPhamMoi.getGiasp()))+ "Đ" );
        if(samPhamMoi.getHinhanh().contains("http")) {
            Glide.with(context).load(samPhamMoi.getHinhanh()).into(holder.imghinhanh);
        }else {
            String hinh = Utils.BASE_URL+"images/"+samPhamMoi.getHinhanh();
            Glide.with(context).load(hinh).into(holder.imghinhanh);

        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, Boolean isLongClick) {
                if(!isLongClick) {
                    //click
                    Intent intent = new Intent(context, ChitietActivity.class);
                    intent.putExtra("chitiet", samPhamMoi);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else  {
                    EventBus.getDefault().postSticky(new SuaXoaEvent(samPhamMoi));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        TextView txtgia, txtten;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtgia = itemView.findViewById(R.id.itemsp_gia);
            txtten = itemView.findViewById(R.id.itemsp_ten);
            imghinhanh = itemView.findViewById(R.id.itemsp_image);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v ,getAdapterPosition(),false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0,0,getAdapterPosition(), "Sửa");
            menu.add(0,1,getAdapterPosition(), "Xóa");

        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v ,getAdapterPosition(),true);
            return false;
        }
    }
}
