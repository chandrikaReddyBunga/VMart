package vmart.example.mypc.vedasmart.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import vmart.example.mypc.vedasmart.R;

public class SideMenuFragment extends Fragment {
    View view;
    TextView allCategories_text;
    RecyclerView categoriesRecyclerview;
    public ActionBarDrawerToggle mtoggle;
    public DrawerLayout mdrawerLayout;
    FragmentDrawerListener fragmentDrawerListener;

    public SideMenuFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_navigation, container, false);
        init();
        return view;
    }
    /////Intialize the view
    public void init() {
        allCategories_text = view.findViewById(R.id.text_title);
        categoriesRecyclerview = view.findViewById(R.id.rvNavigation);
    }
    public void setFragmentDrawerListner(FragmentDrawerListener drawerListner) { this.fragmentDrawerListener = drawerListner; }

    public interface FragmentDrawerListener { }

    public void setup(int fragment1, DrawerLayout drawer, final Toolbar toolbar) {
        view = getActivity().findViewById(fragment1);
        mdrawerLayout = drawer;
        mtoggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        mdrawerLayout.addDrawerListener(mtoggle);
        mtoggle.setDrawerIndicatorEnabled(false);
        mdrawerLayout.post(new Runnable() {
            @Override
            public void run() { }
        });
    }
    public void setDrawerListener(FragmentDrawerListener listener) {
        this.fragmentDrawerListener = listener;
    }
    public static class MessageEvent {
        public final String message;
        public MessageEvent(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}
