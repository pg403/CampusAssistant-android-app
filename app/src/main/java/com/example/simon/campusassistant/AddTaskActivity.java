package com.example.simon.campusassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Pengyu Guo
 */

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener{

    DatabaseHelper myDb;

    private Context context;
    private LinearLayout llDate, llTime;
    private TextView tvDate, tvTime;
    private int year, month, day, hour, minute;
    private StringBuffer date, time;
    private Switch switcher;


    private EditText taskInput;
    private EditText locationInput;
    private Button buttonAdd_1;

    private AlarmManager alarmManager=null;
    private PendingIntent pi=null;
    private Intent alarm_receiver=null;
    private SharedPreferences preferences=null;
    private SharedPreferences.Editor editor=null;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");//24小时制




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        myDb = new DatabaseHelper(this, "clock.db", null, 1);

        taskInput = (EditText)findViewById(R.id.editTextToDo);
        locationInput = (EditText)findViewById(R.id.editTextLocation);
        buttonAdd_1 = (Button)findViewById(R.id.buttonAdd1);
        switcher=(Switch)findViewById(R.id.open);

        context = this;
        date = new StringBuffer();
        time = new StringBuffer();
        initView();
        initDateTime();

        Intent intent=getIntent();
        final int orgRingerMode = intent.getIntExtra("mode_ringer", AudioManager.RINGER_MODE_NORMAL);

        alarmManager=(AlarmManager)getSystemService(Service.ALARM_SERVICE);
        alarm_receiver=new Intent(AddTaskActivity.this,AlarmReceiver.class);
        pi=PendingIntent.getBroadcast(AddTaskActivity.this,0,alarm_receiver,0);

        this.preferences =AddTaskActivity.this.getSharedPreferences("switch", Context.MODE_MULTI_PROCESS);
        this.editor=preferences.edit();
        Boolean remind_status=preferences.getBoolean("open",false);
        switcher.setChecked(false);



        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                Intent intent =new Intent(context, AlarmService.class);


                if (isChecked) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.set(year,month,day,hour,minute);
                    System.out.println(calendar.getTimeInMillis());
                    Log.v("mainActivity", "switch"+String.valueOf(calendar.getTimeInMillis()));

                    boolean isInserted = myDb.insertData(calendar.getTimeInMillis(),taskInput.getText().toString());
                    if (isInserted == true){
                        Toast.makeText(AddTaskActivity.this,"Data Inserted",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddTaskActivity.this,"Data Not Inserted",Toast.LENGTH_SHORT).show();

                        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),6000,pi);
                        //Toast.makeText(AddTaskActivity.this,"Successfully",Toast.LENGTH_LONG).show();

                    }


                } else {
                    alarmManager.cancel(pi);

                }
                AddTaskActivity.this.editor.putBoolean("open",isChecked);
                editor.commit();
            }
        });



        buttonAdd_1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),6000,pi);
                Toast.makeText(AddTaskActivity.this,"Successfully",Toast.LENGTH_LONG).show();

                Intent i = new Intent(AddTaskActivity.this, AlarmService.class);
                //intent.putExtra("key1", "value1");
                //intent.putExtra("key2", "value2");
                startService(i); // not startActivity!

                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(year,month,day,hour,minute);
                System.out.println(calendar.getTimeInMillis());
                Log.v("mainActivity", "button"+String.valueOf(calendar.getTimeInMillis()));

                Date date3 = new Date();
                date3.setTime(calendar.getTimeInMillis());
                System.out.println(simpleDateFormat.format(date3));
                Log.v("mainActivity", " button "+String.valueOf(date3));

                Intent intent = new Intent();
                intent.putExtra("task", taskInput.getText().toString());
                intent.putExtra("date",tvDate.getText().toString());
                intent.putExtra("time",tvTime.getText().toString());
                intent.putExtra("location",locationInput.getText().toString());
                setResult(RESULT_OK, intent);
                finish(); // calls onDestroy
            }
        });



    }

    private void initView() {

        llDate = (LinearLayout) findViewById(R.id.ll_date);
        tvDate = (TextView) findViewById(R.id.tv_date);
        llTime = (LinearLayout) findViewById(R.id.ll_time);
        tvTime = (TextView) findViewById(R.id.tv_time);
        llDate.setOnClickListener(this);
        llTime.setOnClickListener(this);
    }

    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_date:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (date.length() > 0) {
                            date.delete(0, date.length());
                        }

                        tvDate.setText(date.append(String.valueOf(month)).append("/").append(String.valueOf(day)).append("/").append(String.valueOf(year)));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_date, null);
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

                dialog.setTitle("Set Date Information");
                dialog.setView(dialogView);
                dialog.show();
                datePicker.init(year, month - 1, day, this);
                break;
            case R.id.ll_time:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (time.length() > 0) {
                            time.delete(0, time.length());
                        }
                        tvTime.setText(time.append(String.valueOf(hour)).append(":").append(String.valueOf(minute)));
                        dialog.dismiss();
                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog2 = builder2.create();
                View dialogView2 = View.inflate(context, R.layout.dialog_time, null);
                TimePicker timePicker = (TimePicker) dialogView2.findViewById(R.id.timePicker);
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minute);
                timePicker.setIs24HourView(true); //设置24小时制
                timePicker.setOnTimeChangedListener(this);
                dialog2.setTitle("Set Time Information");
                dialog2.setView(dialogView2);
                dialog2.show();
                break;
        }
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
    }
}
