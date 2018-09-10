package com.example.simon.campusassistant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pengyu Guo.
 */

public class TaskAdapter  extends SimpleAdapter {
    private Context mContext;
    private List<HashMap<String, Object>>list = new ArrayList<HashMap<String, Object>>();

    public TaskAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        list = (List<HashMap<String, Object>>) data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view =  super.getView(position, convertView, parent);
        CheckBox myCheckBox = (CheckBox) view.findViewById(R.id.myCheckbox);
        LinearLayout myLinearLayout = (LinearLayout) view.findViewById(R.id.myLinear);
        myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                list.remove(position);
                notifyDataSetChanged();
                //ttaskFragment.saveFile(list);

            }
        });
        myLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                list.remove(position);
                notifyDataSetChanged();
                //ttaskFragment.saveFile(list);
                return true;
            }
        });
        return view;
    }
}
