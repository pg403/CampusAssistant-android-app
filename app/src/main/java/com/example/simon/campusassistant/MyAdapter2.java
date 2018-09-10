package com.example.simon.campusassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jianzhe Hu.
 */

public class MyAdapter2 extends ArrayAdapter<StateVO> {
    private Context mContext;
    private ArrayList<StateVO> listState;
    private MyAdapter2 myAdapter;
    private boolean isFromView = false;

    public MyAdapter2(Context context, int resource, List<StateVO> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<StateVO>) objects;
        this.myAdapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView
                    .findViewById(R.id.text);
            holder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();
                //Toast.makeText(mContext, " "+getPosition, Toast.LENGTH_SHORT).show();
                switch (getPosition){
                    case 1:
                        if (isChecked)
                            MyMapFragment.addbusstop();
                        else
                            MyMapFragment.removebusstop();
                        break;
                    case 2:
                        if (isChecked)
                            MyMapFragment.addcenter();
                        else
                            MyMapFragment.removecenter();
                        break;
                    case 3:
                        if (isChecked)
                            MyMapFragment.addclassroom();
                        else
                            MyMapFragment.removeclassroom();
                        break;
                    case 4:
                        if (isChecked)
                            MyMapFragment.addlibrary();
                        else
                            MyMapFragment.removelibrary();
                        break;
                    case 5:
                        if (isChecked)
                            MyMapFragment.addfood();
                        else
                            MyMapFragment.removefood();
                        break;
                    case 6:
                        if (isChecked)
                            MyMapFragment.addsport();
                        else
                            MyMapFragment.removesport();
                        break;
                    case 7:
                        if (isChecked)
                            MyMapFragment.addyours();
                        else
                            MyMapFragment.removeyours();
                        break;
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}