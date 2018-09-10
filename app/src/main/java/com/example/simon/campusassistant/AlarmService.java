package com.example.simon.campusassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pengyu Guo, Xinmeng Lyu.
 */

public class AlarmService extends Service {

    private AlarmManager am;
    private PendingIntent pi;
    private Long time;
    private String title;
    private DatabaseHelper mRemindSQL;
    private long currentTime;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");//24小时制


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Log.v("mainActivity", "onCreate");
        currentTime = System.currentTimeMillis();

        Date date3 = new Date();
        date3.setTime(currentTime);
        System.out.println(simpleDateFormat.format(date3));
        Log.v("mainActivity",currentTime + " currentTime "+String.valueOf(date3));

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getAlarmTime();


        return START_REDELIVER_INTENT;    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getAlarmTime() {

        Log.v("mainActivity", "getAlarmTime");

        mRemindSQL= new DatabaseHelper(this,"clock.db", null, 1);
        SQLiteDatabase db = mRemindSQL.getWritableDatabase();
        Cursor cursor = db.query("clock_table", null, "TIME > ?", new String[]{String.valueOf(currentTime)}, null, null, null);
        if (cursor.moveToFirst()) { //遍历数据库的表，拿出一条，选择最近的时间赋值，作为第一条提醒数据。
            time = cursor.getLong(cursor.getColumnIndex("TIME"));
            title = cursor.getString(cursor.getColumnIndex("TITLE"));
            do {   if (time < cursor.getLong(cursor.getColumnIndex("TIME"))) {
                time = cursor.getLong(cursor.getColumnIndex("TIME"));
                title = cursor.getString(cursor.getColumnIndex("TITLE"));
            }
            } while (cursor.moveToNext());

            Log.v("mainActivity", "get time: " + time);
        } else {
            time = null;

            Log.v("mainActivity", "time is null.");
        }
        db.delete("clock_table", "TIME=?", new String[]{String.valueOf(time)});      //删除已经发送提醒的时间
        cursor.close();     //记得关闭游标，防止内存泄漏
        Intent startNotification = new Intent(this, AlarmReceiver.class);   //这里启动的广播，下一步会教大家设置
        startNotification.putExtra("TITLE", title);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);   //这里是系统闹钟的对象
        pi = PendingIntent.getBroadcast(this, 0, startNotification, PendingIntent.FLAG_UPDATE_CURRENT);     //设置事件
        if (time != null) {

            Date date2 = new Date();
            date2.setTime(time);
            System.out.println(simpleDateFormat.format(date2));

            Log.v("mainActivity", "提交事件，发送给 广播接收器"+String.valueOf(date2));
            am.set(AlarmManager.RTC_WAKEUP, time, pi);    //提交事件，发送给 广播接收器
        } else {
            //当提醒时间为空的时候，关闭服务，下次添加提醒时再开启
            stopService(new Intent(this, AlarmService.class));
        }

        if(this.startService(startNotification)!=null){
            Log.v("service","successfully");
        }
    }
}
