package com.wikagedung.myyusuf.myapplication.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wikagedung.myyusuf.myapplication.DashboardConstant;
import com.wikagedung.myyusuf.myapplication.R;
import com.wikagedung.myyusuf.myapplication.components.CustomMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OmzetChartFragment.OnOmzetChartFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OmzetChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OmzetChartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Integer selectedMonth;
    private Integer selectedYear;

    private OnOmzetChartFragmentInteractionListener mListener;

    private String[] MONTHS;
    List<Entry> planDataEntries = new ArrayList<Entry>();
    List<Entry> actualDataEntries = new ArrayList<Entry>();
    private LineChart mChart;
    private TextView omzetChartCaption;
    private Button detailButton;
    DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");

    public OmzetChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OmzetChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OmzetChartFragment newInstance(String param1, String param2, Integer param3, Integer param4) {
        OmzetChartFragment fragment = new OmzetChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            selectedMonth = getArguments().getInt(ARG_PARAM3);
            selectedYear = getArguments().getInt(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_omzet_chart, container, false);

        this.MONTHS = new String[14];
        this.MONTHS[0] = "";
        this.MONTHS[1] = DashboardConstant.MONTHS[0];
        this.MONTHS[2] = DashboardConstant.MONTHS[1];
        this.MONTHS[3] = DashboardConstant.MONTHS[2];
        this.MONTHS[4] = DashboardConstant.MONTHS[3];
        this.MONTHS[5] = DashboardConstant.MONTHS[4];
        this.MONTHS[6] = DashboardConstant.MONTHS[5];
        this.MONTHS[7] = DashboardConstant.MONTHS[6];
        this.MONTHS[8] = DashboardConstant.MONTHS[7];
        this.MONTHS[9] = DashboardConstant.MONTHS[8];
        this.MONTHS[10] = DashboardConstant.MONTHS[9];
        this.MONTHS[11] = DashboardConstant.MONTHS[10];
        this.MONTHS[12] = DashboardConstant.MONTHS[11];
        this.MONTHS[13] = "";


        mChart = (LineChart)view.findViewById(R.id.omzet_chart);
        omzetChartCaption = (TextView) view.findViewById(R.id.omzet_chart_caption);
        detailButton = (Button) view.findViewById(R.id.omzet_chart_button);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OmzetChartFragment.this.mListener.onOmzetChartDetailButtonPressed();
            }
        });

        try {
            this.getData(selectedYear);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onOmzetChartFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOmzetChartFragmentInteractionListener) {
            mListener = (OnOmzetChartFragmentInteractionListener) context;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onOmzetChartFragmentInteraction(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnOmzetChartFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOmzetChartFragmentInteraction(Uri uri);
        void onOmzetChartDetailButtonPressed();
    }

    private void getData(int selectedYear) throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "summarydata/sales/" + selectedYear;

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {
                // TODO Auto-generated method stub

                List<Entry> planDataEntries = new ArrayList<Entry>();
                List<Entry> actualDataEntries = new ArrayList<Entry>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i);
                        if(!obj.get("plan").toString().equals("null")){
                            planDataEntries.add(new Entry(i + 1, new Float(obj.getDouble("plan"))));
                        }

                        if(!obj.get("actual").toString().equals("null")){
                            actualDataEntries.add(new Entry(i + 1, new Float(obj.getDouble("actual"))));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                drawChart(planDataEntries, actualDataEntries);

            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", OmzetChartFragment.this.mParam1, OmzetChartFragment.this.mParam2).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(jsonRequest);
    }

    private void drawChart(List<Entry> planDataEntries, List<Entry> actualDataEntries){

        if(planDataEntries.size() > 11) {
            int lastIndex = planDataEntries.size() - 1;
            float lastValue = planDataEntries.get(lastIndex).getY();
            planDataEntries.add(new Entry(13, lastValue));

        }
        planDataEntries.add(0, new Entry(0, 0));

        if(actualDataEntries.size() > 11) {
            int lastIndex = actualDataEntries.size() - 1;
            float lastValue = actualDataEntries.get(lastIndex).getY();
            actualDataEntries.add(new Entry(13, lastValue));
        }
        actualDataEntries.add(0, new Entry(0, 0));

        this.planDataEntries = planDataEntries;
        this.actualDataEntries = actualDataEntries;

        mChart.setMinimumHeight(450);
        mChart.setDescription("");
        mChart.setNoDataTextDescription("No data.");


        LineDataSet dataSet = new LineDataSet(planDataEntries, "Plan");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.parseColor("#8CEAFF"));
        dataSet.setFillColor(Color.parseColor("#8CEAFF"));
        dataSet.setFillAlpha(70);

        LineDataSet dataSet2 = new LineDataSet(actualDataEntries, "Actual");
        dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet2.setDrawCircles(false);
        dataSet2.setDrawFilled(true);
        dataSet2.setDrawValues(false);
        dataSet2.setColor(Color.parseColor("#F7E81C"));
        dataSet2.setFillColor(Color.parseColor("#F7E81C"));
        dataSet2.setFillAlpha(200);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet2);
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);


        AxisValueFormatter formatter = new AxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int valueInInt = (int) value;
                if(valueInInt >= 0 && valueInInt < OmzetChartFragment.this.MONTHS.length){
                    return OmzetChartFragment.this.MONTHS[(int) value];
                }else{
                    return "";
                }

            }

            @Override
            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        mChart.getAxisLeft().setSpaceBottom(0);
        mChart.setDrawBorders(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setGridColor(Color.GRAY);
        mChart.getLegend().setEnabled(false);

        mChart.getXAxis().setTextColor(Color.WHITE);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        mChart.getXAxis().setDrawAxisLine(false);

        mChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        mChart.getAxisLeft().setEnabled(false);
        mChart.setViewPortOffsets(0, 15, 0, 0);

        mChart.setPinchZoom(true);

        mChart.setData(lineData);
        mChart.invalidate();

        mChart.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mChart.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        CustomMarkerView mv = new CustomMarkerView(getActivity().getBaseContext(), R.layout.custom_marker_view);
        mChart.setMarkerView(mv);

        updateSelectedMonth(selectedMonth);

    }

    private void zoomToValue(int value){

        if((value+1) < this.actualDataEntries.size()){
            float yValue = this.actualDataEntries.get(value + 1).getY();
            float xValue = this.actualDataEntries.get(value + 1).getX();

            mChart.fitScreen();
            mChart.zoomAndCenter(2.0f, 3.0f, xValue, yValue, YAxis.AxisDependency.LEFT);
//            mChart.zoomAndCenterAnimated(2.0f, 3.0f, xValue, yValue, YAxis.AxisDependency.LEFT, 1l);
//            mChart.centerViewToAnimated(xValue, yValue, YAxis.AxisDependency.LEFT, 1);
        }

    }

    private void updateSelectedMonth(int value){
        if(value + 1 < this.actualDataEntries.size()){
            omzetChartCaption.setText(this.decimalFormat.format(this.actualDataEntries.get(value + 1).getY()));
        }else{
            omzetChartCaption.setText("0.00");
        }

        zoomToValue(value);
    }

    public void update(int month, int year){
        try {
            selectedMonth = month;
            getData(year);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
