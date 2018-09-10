package com.example.simon.campusassistant;

/**
 * Created by Xinmeng Lyu.
 */

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TitleFragment extends Fragment
{

    private ImageButton mLeftMenu;
    private TextView mLogout;
    private FirebaseAuth mFirebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        mLeftMenu = (ImageButton) view.findViewById(R.id.id_title_left_btn);
        mLeftMenu.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),
                        "we can use as login fragment ! ",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();

        mLogout = (TextView) view.findViewById(R.id.logout_textView);
        mLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                if (currentUser==null){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }else{
                    Log.v("loginTest", currentUser.getEmail());
                }
            }
        });
        return view;
    }
}