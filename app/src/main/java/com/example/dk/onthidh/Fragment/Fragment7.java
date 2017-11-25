package com.example.dk.onthidh.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dk.onthidh.MyFile.MyFile;
import com.example.dk.onthidh.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;


public class Fragment7 extends Fragment {
    private TabLayout tabLayout1,tabLayout2;
    private ViewPager viewPager;
    ImageView imvsearch;
    MaterialSearchView searchview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment7,container,false);
        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        imvsearch= (ImageView) view.findViewById(R.id.imv_search);
        searchview = (MaterialSearchView)getActivity().findViewById(R.id.materialsearchviewfragment);
        setupViewPager(viewPager);
        tabLayout1 = (TabLayout)view.findViewById(R.id.tabs1);
        tabLayout2 = (TabLayout)view.findViewById(R.id.tabs2);
        tabLayout1.setupWithViewPager(viewPager);
        tabLayout2.setupWithViewPager(viewPager);
        imvsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("searchhhh","dit me tui bay");
                searchview.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        newText = newText.toLowerCase();
                        ArrayList<MyFile> newList = new ArrayList<MyFile>();
//                loadAll(newText, newList);
                        Log.d("searchhhh","dit me tui bay");
                        return true;
                    }
                });
            }
        });
        return view;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

