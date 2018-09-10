package com.example.simon.campusassistant;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.Manifest;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Xinmeng Lyu, Jianzhe Hu, Hairong Wang, Pengyu Guo.
 */

public class MainActivity extends Activity implements OnClickListener,ClassFragment.OnFragmentInteractionListener, TaskFragment.OnFragmentInteractionListener, ContentFragment.OnFragmentInteractionListener {
    private LinearLayout mTabHome;
    private LinearLayout mTabClass;
    private LinearLayout mTabTask;
    private LinearLayout mTabNews;
    private LinearLayout mTabMap;

    private ContentFragment mContent;
    private ClassFragment mClass;
    private TaskFragment mTask;
    private NewsFragment mNews;
    private MyMapFragment mMap;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;

    private static final int REQ_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Toast.makeText(getApplication(), "permission aquired", Toast.LENGTH_SHORT).show();
        }

        mTabClass = (LinearLayout) findViewById(R.id.tab_bottom_class);
        mTabTask = (LinearLayout) findViewById(R.id.tab_buttom_task);
        mTabNews = (LinearLayout) findViewById(R.id.tab_buttom_news);
        mTabMap = (LinearLayout) findViewById(R.id.tab_buttom_map);
        mTabHome = (LinearLayout) findViewById(R.id.tab_bottom_home);
        mTabClass.setOnClickListener(this);
        mTabTask.setOnClickListener(this);
        mTabNews.setOnClickListener(this);
        mTabMap.setOnClickListener(this);
        mTabHome.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();

        setDefaultFragment();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            while (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            }
            Toast.makeText(getApplication(), "permission aquired", Toast.LENGTH_SHORT).show();
        }
        // com.test.db 是程序的包名，请根据自己的程序调整
        // /data/data/com.test.db/
        // databases 目录是准备放 SQLite 数据库的地方，也是 Android 程序默认的数据库存储目录
        // 数据库名为 test.db
        String DB_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath() + "/"
                + getPackageName()+"/databases/";  //在手机里存放数据库的位置
        String DB_NAME = "mapDB.db";
        Log.v("asd",DB_PATH+DB_NAME);

        // 检查 SQLite 数据库文件是否存在
        if ((new File(DB_PATH + DB_NAME)).exists() == false) {
            // 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
            File f = new File(DB_PATH);
            // 如 database 目录不存在，新建该目录
            if (!f.exists()) {
                f.mkdir();
            }

            try {
                // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
                InputStream is = getBaseContext().getAssets().open(DB_NAME);
                // 输出流
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);

                // 文件写入
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                // 关闭文件流
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 下面测试 /data/data/com.test.db/databases/ 下的数据库是否能正常工作
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = database.rawQuery("select * from busstop", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String strtest = cursor.getString(1);
            // 看输出的信息是否正确
            Log.v("asd",strtest);
            Toast.makeText(this, "database loaded", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    @Override
    protected void onStart(){
        super.onStart();
        checklogin();
    }

    public void checklogin(){
        currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser==null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else{
            Log.v("loginTest", currentUser.getEmail());
        }
    }


    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mContent = new ContentFragment();
        transaction.replace(R.id.id_content, mContent);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        switch (v.getId()) {
            case R.id.tab_bottom_home:
                if (mContent == null) {
                    mContent = new ContentFragment();
                }
                transaction.replace(R.id.id_content, mContent);
                break;
            case R.id.tab_bottom_class:
                if (mClass == null) {
                    mClass = new ClassFragment();
                }
                transaction.replace(R.id.id_content, mClass);
                break;
            case R.id.tab_buttom_task:
                if (mTask == null) {
                    mTask = new TaskFragment();
                }
                transaction.replace(R.id.id_content, mTask);
                break;
            case R.id.tab_buttom_news:
                if (mNews == null) {
                    Intent intent = new Intent(this, NewsActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tab_buttom_map:
                if (mMap == null) {
                    mMap = new MyMapFragment();
                }
                transaction.replace(R.id.id_content, mMap);
                break;
        }

        transaction.commit();
    }


    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.v("test","data ="+requestCode);

        if (requestCode == REQ_CODE) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            if (mTask == null)
            {
                mTask = new TaskFragment();
            }
            transaction.replace(R.id.id_content, (Fragment) mTask, "tttask");

            if (intent!=null){
                // came back from SecondActivity
                String data1 = intent.getStringExtra("task");
                String data2 = intent.getStringExtra("date");
                String data3 = intent.getStringExtra("time");
                String data4 = intent.getStringExtra("location");

                TaskFragment fragment = (TaskFragment)fm.findFragmentByTag("tttask");

                fragment.addList(data1,data2,data3,data4);

            }

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Intent intent = new Intent(this,AddTaskActivity.class);
        startActivityForResult(intent, REQ_CODE);
    }
}
