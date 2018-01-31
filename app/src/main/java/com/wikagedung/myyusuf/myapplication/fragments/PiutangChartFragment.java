package com.wikagedung.myyusuf.myapplication.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

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
 * {@link PiutangChartFragment.OnPiutangChartFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PiutangChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PiutangChartFragment extends Fragment {
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

    private OnPiutangChartFragmentInteractionListener mListener;

    private String[] MONTHS;
    List<BarEntry> planDataEntries = new ArrayList<BarEntry>();
    List<BarEntry> actualDataEntries = new ArrayList<BarEntry>();
    private BarChart mChart;
    private TextView piutangChartCaption;
    private Button detailButton;
    DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");

    public PiutangChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PiutangChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PiutangChartFragment newInstance(String param1, String param2, Integer param3, Integer param4) {
        PiutangChartFragment fragment = new PiutangChartFragment();
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
        final View view = inflater.inflate(R.layout.fragment_piutang_chart, container, false);

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


        mChart = (BarChart)view.findViewById(R.id.piutang_chart);
        piutangChartCaption = (TextView) view.findViewById(R.id.piutang_chart_caption);
        detailButton = (Button) view.findViewById(R.id.piutang_chart_button);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PiutangChartFragment.this.mListener.onPiutangChartDetailButtonPressed();
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
            mListener.onPiutangChartFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPiutangChartFragmentInteractionListener) {
            mListener = (OnPiutangChartFragmentInteractionListener) context;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPiutangChartFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPiutangChartFragmentInteraction(Uri uri);

        void onPiutangChartDetailButtonPressed();
    }

    //-------------------

    private void getData(int selectedYear) throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "summarydata/financial/" + selectedYear;

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {
                // TODO Auto-generated method stub

                List<BarEntry> planDataEntries = new ArrayList<BarEntry>();
                List<BarEntry> actualDataEntries = new ArrayList<BarEntry>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i);
                        if(!obj.get("piutangUsaha").toString().equals("null")){
                            planDataEntries.add(new BarEntry(i + 1, new Float(obj.getDouble("piutangUsaha"))));
                        }else{
                            planDataEntries.add(new BarEntry(i + 1, 0.0f));
                        }

                        if(!obj.get("tagihanBruto").toString().equals("null")){
                            actualDataEntries.add(new BarEntry(i + 1, new Float(obj.getDouble("tagihanBruto"))));
                        }else{
                            planDataEntries.add(new BarEntry(i + 1, 0.0f));
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
                String encodedString = Base64.encodeToString(String.format("%s:%s", PiutangChartFragment.this.mParam1, PiutangChartFragment.this.mParam2).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(jsonRequest);
    }

    private void drawChart(List<BarEntry> planDataEntries, List<BarEntry> actualDataEntries){

        if(planDataEntries.size() > 11) {
            int lastIndex = planDataEntries.size() - 1;
            float lastValue = planDataEntries.get(lastIndex).getY();
            planDataEntries.add(new BarEntry(13, lastValue));

        }
        planDataEntries.add(0, new BarEntry(0f, 0f));

        if(actualDataEntries.size() > 11) {
            int lastIndex = actualDataEntries.size() - 1;
            float lastValue = actualDataEntries.get(lastIndex).getY();
            actualDataEntries.add(new BarEntry(13, lastValue));
        }
        actualDataEntries.add(0, new BarEntry(0f, 0f));
//
        this.planDataEntries = planDataEntries;
        this.actualDataEntries = actualDataEntries;
//
//        mChart.setMinimumHeight(450);
        mChart.setDescription("");
//        mChart.setNoDataTextDescription("No data.");
//
//
//        BarDataSet dataSet = new BarDataSet(planDataEntries, "Plan");
//        dataSet.setDrawValues(false);
//        dataSet.setColor(Color.parseColor("#7ED321"));
//
//        BarDataSet dataSet2 = new BarDataSet(actualDataEntries, "Actual");
//
//        dataSet2.setDrawValues(false);
//        dataSet2.setColor(Color.parseColor("#92E9FF"));
//
//        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//        dataSets.add(dataSet);
//        dataSets.add(dataSet2);
//
//        BarData barData = new BarData(dataSets);
//
//
//        AxisValueFormatter formatter = new AxisValueFormatter() {
//
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return PiutangChartFragment.this.MONTHS[(int) value];
//            }
//
//            @Override
//            public int getDecimalDigits() {  return 0; }
//        };
//
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.setGranularity(1f);
//        xAxis.setValueFormatter(formatter);
//
//        mChart.getAxisLeft().setSpaceBottom(0);
//        mChart.setDrawBorders(false);
//        mChart.getAxisRight().setEnabled(false);
//        mChart.getXAxis().setGridColor(Color.GRAY);
//        mChart.getLegend().setEnabled(false);
//
//        mChart.getXAxis().setTextColor(Color.WHITE);
//        mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);
//        mChart.getXAxis().setDrawAxisLine(false);
//
//        mChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
//        mChart.getAxisLeft().setEnabled(false);
//        mChart.setViewPortOffsets(0, 15, 0, 0);
//
//        mChart.setPinchZoom(true);
//
//        mChart.setData(barData);
//
//        mChart.setDrawBarShadow(false);
//
//        mChart.getBarData().setBarWidth(0.3f);
//        mChart.getXAxis().setAxisMinValue(0.0f);
//        mChart.getXAxis().setAxisMaxValue(13.0f);
//        mChart.groupBars(0.0f, 0.04f, 0.02f);
//
//        mChart.invalidate();

        BarChart barChart = mChart;

        mChart.setMinimumHeight(450);
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

        barChart.setDrawBarShadow(false);
//        barChart.setDrawValueAboveBar(true);
        barChart.setDescription("");
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(true);
        xl.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                int valueInInt = (int) value;
                if( valueInInt >= 0 && valueInInt < PiutangChartFragment.this.MONTHS.length){
                    return PiutangChartFragment.this.MONTHS[valueInInt];
                }else{
                    return "";
                }
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";//String.valueOf((int) value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true
        barChart.getAxisRight().setEnabled(false);

        //IMPORTANT!!!!
        //data
        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.46f; // x2 dataset
        // (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"

        int startYear = 1980;
        int endYear = 1985;


//        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
//        List<BarEntry> yVals2 = new ArrayList<BarEntry>();

        List<BarEntry> yVals1 = planDataEntries;
        List<BarEntry> yVals2 = actualDataEntries;


//        for (int i = startYear; i < endYear; i++) {
//            yVals1.add(new BarEntry(i, 0.4f));
//        }
//
//        for (int i = startYear; i < endYear; i++) {
//            yVals2.add(new BarEntry(i, 0.7f));
//        }


        BarDataSet set1, set2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet)barChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            // create 2 datasets with different types
            set1 = new BarDataSet(yVals1, "Company A");
            set1.setColor(Color.parseColor("#7ED321"));
            set2 = new BarDataSet(yVals2, "Company B");
            set2.setColor(Color.parseColor("#92E9FF"));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set2);
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }

        set1.setDrawValues(false);
        set2.setDrawValues(false);

        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(0);
        mChart.getXAxis().setAxisMaxValue(14);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate();

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
            piutangChartCaption.setText(this.decimalFormat.format(this.actualDataEntries.get(value + 1).getY()));
        }else{
            piutangChartCaption.setText("0.00");
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
