package com.example.cse.tue_sol;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by erunn on 2017-06-22.
 */

public class ConfirmActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
    }

    public void selectFlag(View view){
        Fragment fragment;

        if(view == findViewById(R.id.up_ticket))
            fragment = new TicketFragment();
        else
            fragment = new SeasonTicketFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_scene, fragment);
        fragmentTransaction.commit();
    }
}
