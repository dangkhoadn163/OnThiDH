package com.example.dk.onthidh.Fragment;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.example.dk.onthidh.R;

/**
 * Created by DK on 11/1/2017.
 */

public class Fragment4 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment4,container,false);
        return view;//super.onCreateView(inflater, container, savedInstanceState);
    }
}

