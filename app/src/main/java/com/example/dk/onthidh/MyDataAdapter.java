package com.example.dk.onthidh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by DK on 9/23/2017.
 */

public class MyDataAdapter extends BaseAdapter {

    Context myContext;
    List<MyData> arrImage;

    public MyDataAdapter(Context myContext, List<MyData> arrImage) {
        this.myContext = myContext;
        this.arrImage = arrImage;
    }

    @Override
    public int getCount() {
        if (arrImage != null)
            return arrImage.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int i) {
        return arrImage.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = new ViewHolder();

//        View mView = new View(myContext);
//        mView = (LayoutInflater.from(myContext).inflate(R.layout.item,null));
        if (view != null) {

        } else {

            MyData myData = arrImage.get(i);

            holder = new ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.item, null));

            if (myData.text.equals("hello")) {
                holder.load(myData.text, myData.image);
            } else {

                holder.load(myData.text, myData.image);
            }

//            ((TextView)mView.findViewById(R.id.txvTenFile)).setText(myData.text);
//            Picasso.with(myContext).load(myData.image).into((ImageView) mView.findViewById(R.id.imvHinhAnh));
        }
        return holder.view;
    }
    private class ViewHolder {
        View view;

        public ViewHolder() {
        }

        public ViewHolder(View view) {
            this.view = view;
        }

        public void load(String text, String hinh) {
            TextView txtResult = (TextView) view.findViewById(R.id.txvTenFile);
            ImageView imgTest = (ImageView) view.findViewById(R.id.imvHinhAnh);

            txtResult.setText(text);
            Picasso.with(myContext).load(hinh).into(imgTest);
        }
    }
}
