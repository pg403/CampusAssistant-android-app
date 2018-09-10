package com.example.simon.campusassistant;

/**
 * Created by Hairong Wang.
 */

import java.util.Calendar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.example.simon.campusassistant.ClassFragment.db;

public class MyDialog {
    private EditText course_name;
    private EditText course_address;
    private EditText course_teacher;
    private EditText course_week;
    //  private EditText course_time1,course_time2;
    private EditText course_count;


    private View view;
    private Context context;
    private LayoutInflater inflater;
    private Builder builder;
    MyAdapter adapter;
    MainActivity main;
    String s1 = "", s2 = "", s3 = "", s4 = "", s5 = "", s6 = "", s7 = "";
    private Cursor[] cursor= new Cursor[7];

    public MyDialog(Context context) {
        this.context = context;
        main = (MainActivity) context;
        adapter = new MyAdapter(context);
    }

    /*
    * 点击未编辑的课程列表跳出”添加课程“对话框
    */
    public void add(final int day, final int n) {
        //填装对话框的view
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.edit_schedule, null);
        findWidgetes();//取部件
        final Button course_time1 = (Button) view.findViewById(R.id.time1);
        final Button course_time2 = (Button) view.findViewById(R.id.time2);
        //为两个输入时间的按钮绑定监听器
        course_time1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimeSet_Dialog(course_time1);
            }
        });
        course_time2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimeSet_Dialog(course_time2);
            }
        });

        builder = new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Edit Course Information")
                .setView(view)
                .setPositiveButton("Enter", new OnClickListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!(s1 = course_name.getText().toString()).equals(""));
                        if (!(s2 = course_address.getText().toString()).equals(""));

                        if (!(s3 = course_teacher.getText().toString()).equals(""));
                        if (!(s4 = course_time1.getText().toString()).equals("")) s4 = s4 +"     -";
                        if (!(s5 = course_time2.getText().toString()).equals("")) ;
                        // if ((s5 = course_count.getText().toString()).equals("") || s1.equals("")) {
                        //      Toast.makeText(context, "请正确输入课程及节数！", 3000).show();
                        //     return;
                        //  }

                        // else {
                        //int i=Integer.parseInt(s5.trim());//i为节数
                        db.update(day,n+1,s1,s2,s3,s4,s5);


                        ClassFragment.cursor[day].requery();
                        ClassFragment.list[day].invalidate();


                    }

                })
                .setNegativeButton("Cancel", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                    }

                });
        builder.create().show();

    }


    public void modify(final int day,final int n){
        //填装对话框的view
        inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.edit_schedule,null);
        findWidgetes();//取部件
        final Button course_time1=(Button)view.findViewById(R.id.time1);
        final Button course_time2=(Button)view.findViewById(R.id.time2);
        //为两个输入时间的按钮绑定监听器
        course_time1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimeSet_Dialog(course_time1);
            }
        });
        course_time2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimeSet_Dialog(course_time2);
            }
        });
        cursor[day]=ClassFragment.cursor[day];
        //从数据库取出旧数据
        cursor[day].moveToPosition(n);
        String [] temp=new String[6];
        for(int i=0;i<6;i++) {
      //      temp[i]=cursor[day].getString(i+1);
        }
        //将旧数据显示在编辑对话框
        if(!temp[0].equals("")) course_name.setText(temp[0]);
        if(!temp[1].equals("")) course_address.setText(temp[1]);
        if(!temp[2].equals("")) course_teacher.setText(temp[2]);
        if(!temp[4].equals("")) course_time1.setText(temp[4]);
        course_time2.setText(temp[5]);
        // course_count.setText(temp[6]);
        view.invalidate();


        builder=new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Edit Course Information")
                .setView(view)
                .setPositiveButton("Enter",new OnClickListener(){

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(!(s1=course_name.getText().toString()).equals(""))
                            if(!(s2=course_address.getText().toString()).equals(""))
                                if(!(s3=course_teacher.getText().toString()).equals(""))
                                    if(!(s4=course_time1.getText().toString()).equals(""))s4=s4+"     -";
                        if(!(s5=course_time2.getText().toString()).equals(""));
                        // s5=course_count.getText().toString();
                        ClassFragment.cursor[day].moveToPosition(n);

                        db.update(day,n+1,s1,s2,s3,s4,s5);
                        ClassFragment.cursor[day].requery();
                        ClassFragment.list[day].invalidate();

                    }

                })

                .setNegativeButton("Cancel", new OnClickListener(){

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                    }

                });

        builder.create().show();

    }

    private void findWidgetes(){
        course_name=(EditText)view.findViewById(R.id.editText1);
        course_address=(EditText)view.findViewById(R.id.editText2);
        course_teacher=(EditText)view.findViewById(R.id.editText3);
        //    course_week=(EditText)view.findViewById(R.id.editText4);
//      course_time1=(EditText)view.findViewById(R.id.time1);
//      course_time2=(EditText)view.findViewById(R.id.time2);
        // course_count=(EditText)view.findViewById(R.id.jieshu);
    }


    public void TimeSet_Dialog(final TextView text){
        Calendar c = Calendar.getInstance();
        // 创建一个TimePickerDialog实例，并把它显示出来。
        new TimePickerDialog(main,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker tp, int hourOfDay,int minute){
                        //获取完整的时间，在只有一位的数字前面加0
                        StringBuffer s_hour = new StringBuffer();
                        StringBuffer s_minute = new StringBuffer();
                        s_hour.append(hourOfDay);
                        s_minute.append(minute);
                        if(hourOfDay<10){
                            s_hour.insert(0,"0");
                        }
                        if(minute<10){
                            s_minute.insert(0,"0");
                        }
                        //将结果显示在edit中
                        text.setText(s_hour.toString() + ":" + s_minute.toString());
                    }
                }
                //设置初始时间
                , c.get(Calendar.HOUR_OF_DAY)
                , c.get(Calendar.MINUTE)
                //true表示采用24小时制
                , true).show();
    }
}
