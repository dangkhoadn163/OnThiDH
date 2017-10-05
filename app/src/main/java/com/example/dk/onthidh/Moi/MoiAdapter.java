package com.example.dk.onthidh.Moi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dk.onthidh.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by DK on 10/2/2017.
 */

public class MoiAdapter extends RecyclerView.Adapter<MoiAdapter.ViewHolder> {
    private Context mContext;
    private List<Moi> images;

    public MoiAdapter(Context mContext, List<Moi> images) {
        this.mContext = mContext;
        this.images = images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moi,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Moi m = images.get(position);
        Picasso.with(mContext)
                .load(m.getHinh())
                .skipMemoryCache()
                .placeholder(R.drawable.noimage)
                .into(holder.item_image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView item_image;
        public ViewHolder(View itemView) {
            super(itemView);
            item_image = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }
}
