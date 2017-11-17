package com.example.dk.onthidh.MyFile;

/**
 * Created by DK on 10/6/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dk.onthidh.Activity.ListTest;
import com.example.dk.onthidh.Activity.Test;
import com.example.dk.onthidh.R;

import java.util.ArrayList;
import java.util.List;

public class MyFileAdapter extends RecyclerView.Adapter<MyFileAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MyFile> files;
    View item;

    public MyFileAdapter(Context mContext, ArrayList<MyFile> files) {
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
//        Picasso.with(mContext).load(p.image).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView txtv;
        public TextView txva;
        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imvHinhAnh);
            txtv = (TextView) itemView.findViewById(R.id.txvTenFile);
            txva = (TextView) itemView.findViewById(R.id.txvAnswer);

        }
    }
    public void setfilter(ArrayList<MyFile> newList){
        this.files = new ArrayList<>();
        this.files.addAll(newList);
        Log.d("filesssssssssssss", files.size() + "");
        this.notifyDataSetChanged();
    }


}
