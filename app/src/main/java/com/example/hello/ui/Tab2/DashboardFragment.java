package com.example.hello.ui.Tab2;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.hello.FloatingWidgetService;
import com.example.hello.FloatingWidgetService2;
import com.example.hello.R;

import static android.app.Activity.RESULT_OK;


public class DashboardFragment extends Fragment {
    Button createbtn;
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return root;
    }
    /*  start floating widget service  */


}