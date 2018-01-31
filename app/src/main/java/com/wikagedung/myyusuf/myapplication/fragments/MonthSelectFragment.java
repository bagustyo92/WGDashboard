package com.wikagedung.myyusuf.myapplication.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wikagedung.myyusuf.myapplication.MainActivity;
import com.wikagedung.myyusuf.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MonthSelectFragment.OnMonthSelectFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MonthSelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthSelectFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    private OnMonthSelectFragmentInteractionListener mListener;

    private Button monthButton1;
    private Button monthButton2;
    private Button monthButton3;
    private Button monthButton4;
    private Button monthButton5;
    private Button monthButton6;

    public MonthSelectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthSelectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthSelectFragment newInstance(int param1, String param2) {
        MonthSelectFragment fragment = new MonthSelectFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_month_select, container, false);

        monthButton1 = (Button)view.findViewById(R.id.month_button_1);
        monthButton2 = (Button)view.findViewById(R.id.month_button_2);
        monthButton3 = (Button)view.findViewById(R.id.month_button_3);
        monthButton4 = (Button)view.findViewById(R.id.month_button_4);
        monthButton5 = (Button)view.findViewById(R.id.month_button_5);
        monthButton6 = (Button)view.findViewById(R.id.month_button_6);

        if(mParam1 == 0){
            monthButton1.setText("JAN");
            monthButton2.setText("FEB");
            monthButton3.setText("MAR");
            monthButton4.setText("APR");
            monthButton5.setText("MEI");
            monthButton6.setText("JUN");

        }else if(mParam1 == 1){
            monthButton1.setText("JUL");
            monthButton2.setText("AUG");
            monthButton3.setText("SEP");
            monthButton4.setText("OKT");
            monthButton5.setText("NOV");
            monthButton6.setText("DES");
        }

        final MainActivity mainActivity = (MainActivity)getActivity();

        monthButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParam1 == 0){
                    mainActivity.updateDashboardData(0);
                }else if(mParam1 == 1){
                    mainActivity.updateDashboardData(6);
                }
            }
        });

        monthButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParam1 == 0){
                    mainActivity.updateDashboardData(1);
                }else if(mParam1 == 1){
                    mainActivity.updateDashboardData(7);
                }
            }
        });

        monthButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParam1 == 0){
                    mainActivity.updateDashboardData(2);
                }else if(mParam1 == 1){
                    mainActivity.updateDashboardData(8);
                }
            }
        });

        monthButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParam1 == 0){
                    mainActivity.updateDashboardData(3);
                }else if(mParam1 == 1){
                    mainActivity.updateDashboardData(9);
                }
            }
        });

        monthButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParam1 == 0){
                    mainActivity.updateDashboardData(4);
                }else if(mParam1 == 1){
                    mainActivity.updateDashboardData(10);
                }
            }
        });

        monthButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParam1 == 0){
                    mainActivity.updateDashboardData(5);
                }else if(mParam1 == 1){
                    mainActivity.updateDashboardData(11);
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMonthSelectFragmentInteraction(uri);
        }
    }

    public void clearButtonColor(){

        if(monthButton1 == null) return;

        monthButton1.setTextColor(Color.BLACK);
        monthButton2.setTextColor(Color.BLACK);
        monthButton3.setTextColor(Color.BLACK);
        monthButton4.setTextColor(Color.BLACK);
        monthButton5.setTextColor(Color.BLACK);
        monthButton6.setTextColor(Color.BLACK);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMonthSelectFragmentInteractionListener) {
            mListener = (OnMonthSelectFragmentInteractionListener) context;
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

    public void setActiveButtonColor(int buttonIndex) {

        if(monthButton1 == null) return;

        int selectedButtonColor = Color.RED;

        switch(buttonIndex){
            case 0:
                monthButton1.setTextColor(selectedButtonColor);
                break;
            case 1:
                monthButton2.setTextColor(selectedButtonColor);
                break;
            case 2:
                monthButton3.setTextColor(selectedButtonColor);
                break;
            case 3:
                monthButton4.setTextColor(selectedButtonColor);
                break;
            case 4:
                monthButton5.setTextColor(selectedButtonColor);
                break;
            case 5:
                monthButton6.setTextColor(selectedButtonColor);
                break;
        }

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
    public interface OnMonthSelectFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMonthSelectFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onMonthSelectFragmentInteraction(null);
    }
}
