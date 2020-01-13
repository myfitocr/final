package com.example.hello.ui.Tab2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.hello.FloatingWidgetService;
import com.example.hello.FloatingWidgetService2;
import com.example.hello.R;
import com.example.hello.ReSaveAdapter;
import com.example.hello.SizeClass;
import com.example.hello.ui.Tab1.HomeViewModel;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    RecyclerView recyclerView;
    ReSaveAdapter reSaveAdapter;
    ArrayList<String> uriList=new ArrayList<>();
    ArrayList<ArrayList<SizeClass>> saveList=new ArrayList<>();

    Uri recentUri;

    SizeClass test;
    ArrayList<SizeClass> testArray=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Images.Media._ID + " COLLATE LOCALIZED ASC";
        Cursor cursor = getContext().getContentResolver().query(uri,null,null,null,sortOrder);

        if(cursor.moveToLast()) {
            String idColumn=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            recentUri = Uri.parse("content://media/external/images/media/" + idColumn);
        }while (cursor.moveToNext());

        /**dataBase 에서 uriList 받아오기**/
        uriList.add(recentUri.toString());
        /**dataBase 에 recentUri 추가하기**/

        /**testCode**/
        test=new SizeClass("30");
        ArrayList<Float> Info = new ArrayList<>();
        Info.add((float) 94);
        Info.add((float) 37.5);
        Info.add((float) 28);
        Info.add((float) 25);
        Info.add((float) 18);
        test.setSizeInfo(Info);
        testArray.add(test);
        saveList.add(testArray);

        /**get saveList from database**/
        //recycler adapter
        recyclerView=view.findViewById(R.id.save_recycler);
        reSaveAdapter=new ReSaveAdapter(getContext(), uriList, saveList);
        System.out.println("saveList length"+saveList.size());
        recyclerView.setAdapter(reSaveAdapter);

        //layout manager
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper=new PagerSnapHelper();
        recyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerView);

        return view;
    }
    /*  start floating widget service  */
}