package com.example.simon.campusassistant;

/**
 * Created by Hairong Wang
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClassFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static ListView list[] = new ListView[7];
    private TabHost tabs   = null;
    public static DataBase db;
    public static Cursor[] cursor=new Cursor[7];
    public SimpleCursorAdapter adapter;
    public SharedPreferences pre;

    //声明一个SharedPreferences对象，用来保存switch组件的开关信息
    private SharedPreferences preferences = null;
    //editor对象用来向preferences中写入数据
    private SharedPreferences.Editor editor = null;
    private Switch switch_quietButton;

    public ClassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassFragment newInstance(String param1, String param2) {
        ClassFragment fragment = new ClassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        db=new DataBase(getActivity());
        pre=getActivity().getSharedPreferences("firstStart",Context.MODE_PRIVATE);
        /*
         * 判断程序是否第一次运行，如果是创建数据库表
         */
        if(pre.getBoolean("firstStart", true)){
            SingleInstance.createTable();
            (pre.edit()).putBoolean("firstStart",false).commit();
        }
        super.onActivityCreated(saveInstanceState);
        list[0] = (ListView)getActivity().findViewById(R.id.list0);
        list[1] = (ListView)getActivity().findViewById(R.id.list1);
        list[2] = (ListView)getActivity().findViewById(R.id.list2);
        list[3] = (ListView)getActivity().findViewById(R.id.list3);
        list[4] = (ListView)getActivity().findViewById(R.id.list4);
        list[5] = (ListView)getActivity().findViewById(R.id.list5);
        list[6] = (ListView)getActivity().findViewById(R.id.list6);
        tabs  = (TabHost)getActivity().findViewById(R.id.tabhost);

        tabs.setup();

        TabHost.TabSpec  spec = null;
        addCard(spec,"tag1",R.id.list0,"SU");
        addCard(spec,"tag2",R.id.list1,"MO");
        addCard(spec,"tag3",R.id.list2,"TU");
        addCard(spec,"tag4",R.id.list3,"WE");
        addCard(spec,"tag5",R.id.list4,"TH");
        addCard(spec,"tag6",R.id.list5,"FR");
        addCard(spec,"tag7",R.id.list6,"SA");

        TabWidget tabWidget = tabs.getTabWidget();
        for(int i=0;i<tabWidget.getChildCount();i++){
            TextView tv = (TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(0xff004499);
        }
        tabs.setCurrentTab(ShareMethod.getWeekDay());

        for(int i=0;i<7;i++){
            cursor[i]=this.db.select(i);
            list[i].setAdapter(adapter(i));
        }

        final AudioManager audioManager = (AudioManager)getActivity().getSystemService(Service.AUDIO_SERVICE);
        //获取手机之前设置好的铃声模式,该数据将用来传递给activity_set
        final int orgRingerMode = audioManager.getRingerMode();
        Intent intent = getActivity().getIntent();



        switch_quietButton = (Switch)getActivity().findViewById(R.id.switch_quiet);

        //指定该SharedPreferences数据可以跨进称调用
        this.preferences = getActivity().getSharedPreferences("switch", Context.MODE_MULTI_PROCESS);
        this.editor = preferences.edit();

        Boolean quiet_status = preferences.getBoolean("switch_quiet", false);
        switch_quietButton.setChecked(quiet_status);
        switch_quietButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //启动自动静音的service
                Intent intent = new Intent(getActivity(),SetQuietService.class);
                intent.setAction("zyb.org.service.QUIET_SERVICE");

                if(isChecked){
                    if(getActivity().startService(intent) != null)
                        Toast.makeText(getActivity(), "Successfully open, calls will be automatically converted to vibration mode during the class.", Toast.LENGTH_LONG).show();
                    else{
                        Toast.makeText(getActivity(), "Failed to open successfully, please try again", Toast.LENGTH_LONG).show();
                        switch_quietButton.setChecked(false);
                    }
                }
                else{
                    if(getActivity().stopService(intent))
                        Toast.makeText(getActivity(), "Successfully closed, revert to the original bell mode", Toast.LENGTH_LONG).show();
                    else{
                        Toast.makeText(getActivity(), "Failure to close successfully, please try again", Toast.LENGTH_LONG).show();
                        switch_quietButton.setChecked(true);
                    }
                    audioManager.setRingerMode(orgRingerMode);
                }
                //将开关信息数据保存进preferences中
                editor.putBoolean("switch_quiet", isChecked);
                editor.commit();
            }
        });

        for( int day=0;day<7;day++){

            list[day].setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        final int id, long arg3) {
                    final int currentDay = tabs.getCurrentTab();
                    final int n = id;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setTitle("Options");
                    TextView tv = (TextView) arg1.findViewById(R.id.ltext0);
                    Log.i("Test", (tv.getText().toString().equals("")) + "");
                    //如果课程栏目为空就启动添加对话框
                    if ((tv.getText()).toString().equals("")) {
                        //通过数组资源为对话框中的列表添加选项内容，这里只有一个选项
                        builder.setItems(R.array.edit_options1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //如果单击了该列表项，则跳转到编辑课程信息的界面
                                if (which == 0) {
                                    new MyDialog(getActivity()).add(currentDay, n);
                                }
                            }
                        });
                        builder.create().show();
                    } else {
                        builder.setItems(R.array.edit_options2, new DialogInterface.OnClickListener() {

                            @SuppressWarnings("deprecation")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //如果单击了该列表项，则跳转到编辑课程信息的界面
                                if (which == 0) {
                                    new MyDialog(getActivity()).add(currentDay, n);
                                }
                                if (which == 1) {
                                    cursor[currentDay].moveToPosition(n);
                                    db.deleteData(currentDay, n  + 1);
                                    cursor[currentDay].requery();
                                    list[currentDay].invalidate();
                                }
                            }
                        });
                        builder.create().show();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void addCard(TabHost.TabSpec spec,String tag,int id,String name){
        spec = tabs.newTabSpec(tag);
        spec.setContent(id);
        spec.setIndicator(name);
        tabs.addTab(spec);
    }


    @SuppressWarnings("deprecation")
    public SimpleCursorAdapter adapter(int i){
        return new SimpleCursorAdapter(getActivity(), R.layout.list_v2,cursor[i],new String[]{"_id","classes","location",
                "teacher","time1","time2"},new int[]{R.id.number,R.id.ltext0,R.id.ltext1,R.id.ltext6,R.id.ltext7,R.id.ltext8} );
    }

    static class SingleInstance{
        static SingleInstance si;
        private SingleInstance(){
            for(int i=0;i<7;i++){
                db.createTable(i);
            }
        }
        static SingleInstance createTable(){
            if(si==null)
                return si=new SingleInstance();
            return null;
        }
    }

}
