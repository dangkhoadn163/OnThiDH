package com.example.dk.onthidh;

/**
 * Created by DK on 10/6/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyFileAdapter extends RecyclerView.Adapter<MyFileAdapter.ViewHolder> {
    private Context mContext;
    private List<MyFile> files;

    public MyFileAdapter(Context mContext, List<MyFile> files) {
        this.mContext = mContext;
        this.files = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyFile p = files.get(position);
        holder.txtv.setText(p.text);
        Picasso.with(mContext).load(p.image).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView txtv;
        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imvHinhAnh);
            txtv = (TextView) itemView.findViewById(R.id.txvTenFile);
        }
    }
}
