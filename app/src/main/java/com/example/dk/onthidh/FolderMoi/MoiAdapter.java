package com.example.dk.onthidh.FolderMoi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dk.onthidh.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoiAdapter extends RecyclerView.Adapter<MoiAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mois;

    public MoiAdapter(Context mContext, List<String> mois) {
        this.mContext = mContext;
        this.mois = mois;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moi,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String moi = mois.get(position);
        Picasso.with(mContext).load(moi).placeholder(R.drawable.noimage).into(holder.item_image);
    }

    @Override
    public int getItemCount() {
        return mois.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView item_image;
        public ViewHolder(View itemView) {
            super(itemView);
            item_image = (ImageView) itemView.findViewById(R.id.item_image);

        }
    }
}
