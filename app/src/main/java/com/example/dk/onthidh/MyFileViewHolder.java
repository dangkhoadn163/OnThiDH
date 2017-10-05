package com.example.dk.onthidh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by DK on 8/23/2017.
 */

public final class MyFileViewHolder extends RecyclerView.ViewHolder {

    public ImageView imvHinhAnh;
    public TextView txvTenFile,txvKey;

    public MyFileViewHolder(View itemView) {
        super(itemView);
        this.imvHinhAnh = (ImageView) itemView.findViewById(R.id.imvHinhAnh);
        this.txvTenFile = (TextView) itemView.findViewById(R.id.txvTenFile);
        this.txvKey = (TextView) itemView.findViewById(R.id.txvKey);
    }

    public void loadHinhAnh(Context context, String duongDan) {

        Picasso.with(context).load(duongDan).into(imvHinhAnh);
    }

    public void loadTenFile(String tenFile) {

        txvTenFile.setText(tenFile);
    }
    public void setActionClick(final String t){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),t +  "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
