package com.example.hello;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class PantsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    TessBaseAPI tess;
    String dataPath = "";
    ArrayList<SizeClass> sizeClasses=new ArrayList<>();
    SizeClass sizeClass;

    ArrayList<String> items = new ArrayList<>();
    ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    SizeClass myFit = new SizeClass("myFit"); //사용자 사이즈 정보
    RecyclerView recyclerView;
    ReSizeAdapter reSizeAdapter;

    ArrayList<SizeClass> unchangedSize = new ArrayList<>();
    ArrayList<SizeClass> checkedSize = new ArrayList<>();
    ArrayList<SizeClass> unchangedSizeBase = new ArrayList<>();

    Bitmap b;

    public static final BitmapFactory.Options options = new BitmapFactory.Options();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pants);

        dataPath = getFilesDir() + "/tesseract/";
        checkFile(new File(dataPath+"tessdata/"),"kor");
        checkFile(new File(dataPath+"tessdata/"),"eng");

        String lang = "kor+eng";
        tess = new TessBaseAPI();
        tess.init(dataPath,lang);

        Uri uri_1 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};
        ContentResolver res = getApplicationContext().getContentResolver();
        Cursor imageCursor = getContentResolver().query(uri_1, projection, null, null, null);
        if(imageCursor.moveToLast()){
            do {
                Uri photoUri = Uri.withAppendedPath(uri_1, imageCursor.getString(0));
                //photoUri = MediaStore.setRequireOriginal(photoUri);
                if (photoUri != null) {
                    ParcelFileDescriptor fd = null;
                    try {
                        fd = res.openFileDescriptor(photoUri, "r");

                        //크기를 얻어오기 위한옵션 ,
                        //inJustDecodeBounds값이 true로 설정되면 decoder가 bitmap object에 대해 메모리를 할당하지 않고, 따라서 bitmap을 반환하지도 않는다.
                        // 다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.

                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFileDescriptor(
                                fd.getFileDescriptor(), null, options);
                        int scale = 0;
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = scale;

                        b = BitmapFactory.decodeFileDescriptor(
                                fd.getFileDescriptor(), null, options);

                        if (b != null) {
                            // finally rescale to exactly the size we need
                        }

                    } catch (FileNotFoundException e) {
                    } finally {
                        try {
                            if (fd != null)
                                fd.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }while (imageCursor.moveToNext());
        }

        b = ARGBBitmap(b);
        sizeClasses=processImage(b);
        unchangedSizeBase=sizeClasses;

        for (int j = 0; j < sizeClasses.size(); j++) {
            items.add(sizeClasses.get(j).getSizeName());
        }

        CheckBox cb0 = findViewById(R.id.cb0);
        CheckBox cb1 = findViewById(R.id.cb1);
        CheckBox cb2 = findViewById(R.id.cb2);
        CheckBox cb3 = findViewById(R.id.cb3);
        CheckBox cb4 = findViewById(R.id.cb4);
        CheckBox cb5 = findViewById(R.id.cb5);
        CheckBox cb6 = findViewById(R.id.cb6);
        CheckBox cb7 = findViewById(R.id.cb7);
        CheckBox cb8 = findViewById(R.id.cb8);
        CheckBox cb9 = findViewById(R.id.cb9);

        checkBoxes.add(cb0);
        checkBoxes.add(cb1);
        checkBoxes.add(cb2);
        checkBoxes.add(cb3);
        checkBoxes.add(cb4);
        checkBoxes.add(cb5);
        checkBoxes.add(cb6);
        checkBoxes.add(cb7);
        checkBoxes.add(cb8);
        checkBoxes.add(cb9);

        for (int k = 0; k < 10; k++) {
            checkBoxes.get(k).setOnCheckedChangeListener(this);
            checkBoxes.get(k).setVisibility(View.INVISIBLE);
        }

        for (int l = 0; l < items.size(); l++) {
            checkBoxes.get(l).setVisibility(View.VISIBLE);
            checkBoxes.get(l).setText(items.get(l));
        }

        /**사용자 정보 sizeClass 에서 sizeInfo 받아오기**/
        ArrayList<Float> Info = new ArrayList<>();
        Info.add((float) 94);
        Info.add((float) 37.5);
        Info.add((float) 28);
        Info.add((float) 25);
        Info.add((float) 18);
        myFit.setSizeInfo(Info);

        checkedSize.add(myFit);

        //recycler adapter
        /**column 너비 조정 필요**/
        recyclerView=findViewById(R.id.recycler_size);
        reSizeAdapter=new ReSizeAdapter(getApplicationContext(),checkedSize);
        recyclerView.setAdapter(reSizeAdapter);

        //layout manager
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        LinearLayout linearLayout=findViewById(R.id.pantsImage);
        ViewEx viewEx = new ViewEx(getApplicationContext(),checkedSize);
        linearLayout.addView(viewEx);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        unchangedSize = unchangedSizeBase;
        checkedSize.clear();
        checkedSize.add(myFit);

        //체크박스를 클릭해서 상태가 바뀌었을 경우 호출되는 callback method
        for (int m = 0; m < checkBoxes.size(); m++) {
            if (checkBoxes.get(m).isChecked()) {
                checkedSize.add(unchangedSize.get(m));
            }
        }

        recyclerView=findViewById(R.id.recycler_size);
        reSizeAdapter=new ReSizeAdapter(getApplicationContext(),checkedSize);
        recyclerView.setAdapter(reSizeAdapter);

        //layout manager
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        LinearLayout linearLayout=findViewById(R.id.pantsImage);
        linearLayout.removeAllViews();
        ViewEx viewEx = new ViewEx(getApplicationContext(),checkedSize);
        linearLayout.addView(viewEx);
    }

    public ArrayList<SizeClass> processImage(Bitmap bitmap){
        String result = null;
        tess.setImage(bitmap);
        result = tess.getUTF8Text();

        String target="보세요\n";
        if(!result.contains(target)){
            target="MY\n";
        }

        int target_num=result.indexOf(target);
        String size;
        size= result.substring(target_num+4);

        sizeClasses=getSizeInfo(size);

        return sizeClasses;
    }

    /**추가적인 끝조건 위해서 필요하면 사용**/
    private char getEndOfSize(String size){
        for(char c:size.toCharArray()){
            if (c>=32 && c<=126 || c==10){
            }else return c;
        }
        return 0;
    }

    private ArrayList<SizeClass> getSizeInfo(String getSize){
        String[] array=getSize.split("\n");
        String[] getType=array[0].split(" ");

        for(int i=0;i<array.length;i++){
            String[] getSizeInfo=array[i].split(" ");
            sizeClass=new SizeClass(getSizeInfo[0]);

            ArrayList<Float> info=new ArrayList<>();
            if (getSizeInfo.length == 6) {
                for(int j=1;j<getSizeInfo.length;j++){
                    Float size=Float.parseFloat(getSizeInfo[j]);

                    if(size>110.0) size=size/10;
                    info.add(size);
                }
                sizeClass.setSizeInfo(info);
                sizeClasses.add(sizeClass);
            } else break;
        }
        return sizeClasses;
    }

    private void checkFile(File dir,String lang){
        if(!dir.exists()&&dir.mkdirs()){
            copyFiles(lang);
        }
        if(dir.exists()){
            String datafilePath = dataPath + "/tessdata/"+lang + ".traineddata";
            File datafile= new File(datafilePath);
            if(!datafile.exists()){
                copyFiles(lang);
            }
        }
    }

    private void copyFiles(String lang) {
        try {
            String filepath = dataPath + "/tessdata/" + lang + ".traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap ARGBBitmap(Bitmap img) {
        return img.copy(Bitmap.Config.ARGB_8888,true);
    }
}

