package com.example.dk.onthidh.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dk.onthidh.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;


public class Fragment7 extends Fragment {
    private TabLayout tabLayout1,tabLayout2,tabLayout3,tabLayout4;
    private ViewPager viewPager;
    Toolbar toolbar;
    MaterialSearchView searchview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment7,container,false);
        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        searchview = (MaterialSearchView)getActivity().findViewById(R.id.materialsearchview);
        setupViewPager(viewPager);
        tabLayout1 = (TabLayout)view.findViewById(R.id.tabs1);
        tabLayout2 = (TabLayout)view.findViewById(R.id.tabs2);
/*        tabLayout3 = (TabLayout)view.findViewById(R.id.tabs3);
        tabLayout4 = (TabLayout)view.findViewById(R.id.tabs4);*/
        tabLayout1.setupWithViewPager(viewPager);
        tabLayout2.setupWithViewPager(viewPager);
/*        tabLayout3.setupWithViewPager(viewPager);
        tabLayout4.setupWithViewPager(viewPager);*/
        return view;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
/*        adapter.addFragment(new OneFragment(), "THREE");
        adapter.addFragment(new TwoFragment(), "FOUR");*/
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
    private void search(){
        searchview.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), "hi", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                Toast.makeText(getActivity(), "hi", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        getActivity().getMenuInflater().inflate(R.menu.search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Search : {
                searchview.setMenuItem(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }


}

