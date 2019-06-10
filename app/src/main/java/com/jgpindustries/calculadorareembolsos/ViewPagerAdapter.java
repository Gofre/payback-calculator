package com.jgpindustries.calculadorareembolsos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ParticipantesFragment();
        } else if (position == 1) {
            fragment = new GastosFragment();
        } else if (position == 2) {
            fragment = new ReembolsosFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Participantes";
        } else if (position == 1) {
            title = "Gastos";
        } else if (position == 2) {
            title = "Reembolsos";
        }
        return title;
    }
}
