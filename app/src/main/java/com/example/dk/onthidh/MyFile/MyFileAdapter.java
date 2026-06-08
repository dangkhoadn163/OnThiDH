package com.example.dk.onthidh.MyFile;

/**
 * Created by DK on 10/6/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.dk.onthidh.R;

import java.util.ArrayList;

public class MyFileAdapter extends RecyclerView.Adapter<MyFileAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MyFile> files;
    private OnItemClickListener listener;

    public MyFileAdapter(Context mContext, ArrayList<MyFile> filesr) {
        this.mContext = mContext;
        this.files = filesr;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        //Picasso.with(mContext).load(p.image).into(holder.img);
    }

    @Override
    public int getItemCount() {

        return files == null ? 0 : files.size() ;
    }

    public MyFile getItem(int position) {
        if (files == null || position < 0 || position >= files.size()) {
            return null;
        }
        return files.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView txtv,txva;
        public ViewHolder(final View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imvHinhAnh);
            txtv = (TextView) itemView.findViewById(R.id.txvTenFile);
            txva = (TextView) itemView.findViewById(R.id.txvAnswer);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });

        }
    }
    public void setfilter(ArrayList<MyFile> newList){
        this.files = new ArrayList<>();
        this.files.addAll(newList);
        Log.d("filesssssssssssss", files.size() + "");
        this.notifyDataSetChanged();
    }
}
