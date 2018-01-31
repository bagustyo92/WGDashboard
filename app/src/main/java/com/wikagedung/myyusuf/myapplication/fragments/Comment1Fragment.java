package com.wikagedung.myyusuf.myapplication.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bagus on 25/07/2017.
 */

public class Comment1Fragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Comment1Fragment";

    private String username;
    private String password;
    private String mParam1;
    private String mParam2;

    private EditText writeComment;
    private ImageButton sendButton;
    private TextView textComment;


    public static Comment1Fragment newInstance(String param1, String param2){
        Comment1Fragment fragment = new Comment1Fragment();
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

    private void initComment(){

    }
//
//    public void  loadData(JSONObject jsonData){
//        try{
//
//        }
//    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        this.username = mParam1;
//        this.password = mParam2;
//
//        NetProfitActivity netProfitActivity = (NetProfitActivity) getActivity();
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(netProfitActivity.getApplicationContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
//        return ;
//    }

    private void setCommentText(String groupName, JSONObject jsonData) throws JSONException {
        JSONObject group = jsonData.getJSONObject(groupName);
        String name =  group.getString("name");
        String comment = group.getString("comment");
        String date = group.getString("time_stamp");

        textComment = (TextView) getView().findViewById(android.R.id.text1);
    }
}
