package com.example.dk.onthidh.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.dk.onthidh.MyFile.MyFile;
import com.example.dk.onthidh.MyFile.MyFileAdapter;
import com.example.dk.onthidh.R;
import com.google.firebase.database.DatabaseReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;


public class Fragment7 extends Fragment {
    ArrayList<MyFile> files;
    MyFileAdapter adapter;
    ArrayList<String> keys;
    private RecyclerView rcvData;
    String uid;
    private Toolbar toolbar;
    private TabLayout tabLayout1,tabLayout2;
    private ViewPager viewPager;
    private MaterialSearchView searchview;
    DatabaseReference rootDatabase;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment7,container,false);
        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        searchview = (MaterialSearchView)view.findViewById(R.id.materialsearchviewfragment);
        setupViewPager(viewPager);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar_search);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        tabLayout1 = (TabLayout)view.findViewById(R.id.tabs1);
        tabLayout2 = (TabLayout)view.findViewById(R.id.tabs2);
        tabLayout1.setupWithViewPager(viewPager);
        tabLayout2.setupWithViewPager(viewPager);
//        search();
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
    private void search() {
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
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.toolbar_search, menu);
        MenuItem item = menu.findItem(R.id.Search);
        searchview.setMenuItem(item);

    }

}

