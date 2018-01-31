package com.wikagedung.myyusuf.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wikagedung.myyusuf.myapplication.fragments.MonthSelectFragment;
import com.wikagedung.myyusuf.myapplication.fragments.OmzetChartFragment;
import com.wikagedung.myyusuf.myapplication.fragments.SalesChartFragment;
import com.wikagedung.myyusuf.myapplication.fragments.PiutangChartFragment;
import com.wikagedung.myyusuf.myapplication.model.DashboardItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class MainActivity extends AppCompatActivity
        implements MonthSelectFragment.OnMonthSelectFragmentInteractionListener,
        OmzetChartFragment.OnOmzetChartFragmentInteractionListener,
        SalesChartFragment.OnSalesChartFragmentInteractionListener,
        PiutangChartFragment.OnPiutangChartFragmentInteractionListener{

    private static final int NUM_PAGES = 3;
    private int selectedMonth = 1;
    private int selectedYear = 2016;

    private DashboardAdapter adapter;
    ArrayList<DashboardItem> dashboardItems = new ArrayList<DashboardItem>();
    DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");

    private RecyclerView mRecyclerView;

    FragmentPagerAdapter monthSelectAdapterViewPager;
    FragmentPagerAdapter chartAdapterViewPager;

    ViewPager monthSelectPager;
    ViewPager chartPager;

    TextView monthSelectLabel;

    ArrayList<ImageView> dots;

    private String username;
    private String password;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        //IMPORTANT!!!
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        ScrollView scrollView = (ScrollView)findViewById(R.id.dashboard_scroll_view);
        scrollView.setVerticalScrollBarEnabled(false);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);

        Calendar calendar = Calendar.getInstance();
        this.selectedYear = calendar.get(Calendar.YEAR);
        this.selectedMonth = calendar.get(Calendar.MONTH);
        if(this.selectedMonth == 0){
            this.selectedYear = this.selectedYear - 1;
            this.selectedMonth = 11;
        }else{
            this.selectedMonth = this.selectedMonth - 1;
        }

        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        DatePickerDialog.OnDateSetListener dateOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                datePick.setText(new StringBuilder().append(selectedDay).append("-").append(selectedMonth).append("-").append(selectedYear));

                // Prevent called twice
                if (view.isShown()) {
                    MainActivity.this.selectedMonth = selectedMonth;
                    MainActivity.this.selectedYear = selectedYear;
                    MainActivity.this.updateDashboardData();
                }

            }
        };

        final DatePickerDialog datePickerDialog = (DatePickerDialog) createDialogWithoutDateField(dateOnDateSetListener);


        monthSelectLabel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        drawDashboardItems();

        monthSelectPager = (ViewPager) findViewById(R.id.month_select_pager);
        monthSelectAdapterViewPager = new MonthSelectPagerAdapter(getSupportFragmentManager());
        monthSelectPager.setAdapter(monthSelectAdapterViewPager);
        monthSelectPager.setCurrentItem(0);

        chartPager = (ViewPager) findViewById(R.id.chart_pager);
        chartAdapterViewPager = new ChartPagerAdapter(getSupportFragmentManager());
        chartPager.setAdapter(chartAdapterViewPager);
        chartPager.setOffscreenPageLimit(3);
        chartPager.setCurrentItem(0);

        this.updateDashboardData();

        scrollView.fullScroll(View.FOCUS_UP);//if you move at the end of the scroll
        scrollView.pageScroll(View.FOCUS_UP);//if you move at the middle of the scroll
        scrollView.smoothScrollTo(0,0);

        addDots();
        selectDot(0);

    }

//    public boolean isOnline() {
//
//        return true;
//    }

    @Override
    protected void onStart() {
        super.onStart();
        ScrollView scrollView = (ScrollView)findViewById(R.id.dashboard_scroll_view);
        scrollView.fullScroll(View.FOCUS_UP);//if you move at the end of the scroll
        scrollView.pageScroll(View.FOCUS_UP);//if you move at the middle of the scroll
        scrollView.smoothScrollTo(0,0);
    }

    private DatePickerDialog createDialogWithoutDateField(DatePickerDialog.OnDateSetListener dateOnDateSetListener) {
        DatePickerDialog dpd = new DatePickerDialog(this,android.R.style.Theme_Holo_Dialog, dateOnDateSetListener, selectedYear, selectedMonth, 24);
        dpd.setTitle("");
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);

                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                        if (daySpinnerId != 0)
                        {
                            View daySpinner = datePicker.findViewById(daySpinnerId);
                            if (daySpinner != null)
                            {
                                daySpinner.setVisibility(View.GONE);
                            }
                        }
//                        if ("mDaySpinner".equals(datePickerField.getName())) {
//
//                        }
                    }
                }
            }
        }
        catch (Exception ex) {
        }
        return dpd;
    }

    private void drawDashboardItems(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_list);

//        ArrayList<DashboardItem> dashboardItems = new ArrayList<DashboardItem>();

        DashboardItem item = new DashboardItem();
        item.setImage1("equal_trend");
        item.setLabel1("Laba Bersih");
        item.setLabel2("%Terhadap RKAP");
        item.setLabel3("-");
        item.setLabel4("-");
        dashboardItems.add(item);

        item = new DashboardItem();
        item.setImage1("equal_trend");
        item.setLabel1("Proyek Terlambat");
        item.setLabel2("%Terhadap Total Proyek");
        item.setLabel3("-");
        item.setLabel4("-");
        dashboardItems.add(item);

        item = new DashboardItem();
        item.setImage1("equal_trend");
        item.setLabel1("Score Card");
        item.setLabel2("%Terhadap Target");
        item.setLabel3("-");
        item.setLabel4("-");
        dashboardItems.add(item);

        item = new DashboardItem();
        item.setImage1("equal_trend");
        item.setLabel1("Nilai Risiko Ekstrim");
        item.setLabel2("%Terhadap Total");
        item.setLabel3("-");
        item.setLabel4("-");
        dashboardItems.add(item);

        item = new DashboardItem();
        item.setImage1("equal_trend");
        item.setLabel1("OK Property");
        item.setLabel2("%Terhadap RKAP");
        item.setLabel3("-");
        item.setLabel4("-");
        dashboardItems.add(item);

        item = new DashboardItem();
//        item.setImage1(R.drawable.ic_cake_black_24dp);
        item.setStatusImageName("ulangTahun");
        item.setLabel1("Ulang Tahun");
        item.setLabel2("Dalam bulan ini");
        item.setLabel3("");
        item.setLabel4("");
        dashboardItems.add(item);

        item = new DashboardItem();
        item.setStatusImageName("komentarGambar");
        item.setLabel1("Komentar");
        item.setLabel2("Dalam bulan ini");
        item.setLabel3("");
        item.setLabel4("");
        dashboardItems.add(item);

        item = new DashboardItem();
        item.setImage1("equal_trend");
        item.setLabel1("QMSL");
        item.setLabel2("");
        item.setLabel3("");
        item.setLabel4("");
        dashboardItems.add(item);

        item = new DashboardItem();
        item.setImage1("equal_trend");
        item.setLabel1("SHE Level");
        item.setLabel2("");
        item.setLabel3("");
        item.setLabel4("");
        dashboardItems.add(item);

        item = new DashboardItem();
        item.setImage1("equal_trend");
        item.setLabel1("5R");
        item.setLabel2("");
        item.setLabel3("");
        item.setLabel4("");
        dashboardItems.add(item);

        adapter = new DashboardAdapter(dashboardItems);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        mRecyclerView = recyclerView;
    }

    private void getNetProfitData() throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "summarydata/net-profit/" + selectedYear + "/" + (selectedMonth + 1);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                int itemPosition = 0;

                try {

                    BigDecimal netProfit = new BigDecimal(response.getString("netProfit"));
                    BigDecimal prevNetProfit = new BigDecimal(response.getString("prevNetProfit"));
                    BigDecimal rkap = new BigDecimal(response.getString("rkap"));
                    BigDecimal prevRkap = new BigDecimal(response.getString("prevRkap"));
                    BigDecimal netProfitInMillion = netProfit.divide(new BigDecimal("1000000"),2, BigDecimal.ROUND_HALF_UP);
                    dashboardItems.get(itemPosition).setLabel3(decimalFormat.format(netProfitInMillion));

                    BigDecimal progressInPercentage = new BigDecimal("0.0");
                    if (rkap.signum() == 1){
                        progressInPercentage = netProfit.divide(rkap, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                        dashboardItems.get(itemPosition).setLabel4(decimalFormat.format(progressInPercentage) + "%");
                    }else{
                        dashboardItems.get(itemPosition).setLabel4("-");
                    }

                    BigDecimal prevProgressInPercentage = new BigDecimal("0.0");
                    if (prevRkap.signum() == 1){
                        prevProgressInPercentage = prevNetProfit.divide(prevRkap, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                    }

                    String statusImageName = "equal_trend";
                    String percentageLabelColor = "blue";
                    if (progressInPercentage.compareTo(prevProgressInPercentage) == 1){
                        statusImageName = "up_trend";
                        percentageLabelColor = "#7ED321";
                    }else if (progressInPercentage.compareTo(prevProgressInPercentage) == -1){
                        statusImageName = "down_trend_red";
                        percentageLabelColor = "red";
                    }

                    dashboardItems.get(itemPosition).setStatusImageName(statusImageName);
                    dashboardItems.get(itemPosition).setPercentageLabelColor(percentageLabelColor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", MainActivity.this.username, MainActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void getProjectInfoData() throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "summarydata/project-info/" + selectedYear + "/" + (selectedMonth + 1);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                int itemPosition = 1;

                try {

                    BigDecimal lateProjectCount = new BigDecimal(response.getString("lateProjectCount"));
                    BigDecimal projectCount = new BigDecimal(response.getString("projectCount"));
                    BigDecimal prevLateProjectCount = new BigDecimal(response.getString("prevLateProjectCount"));

                    dashboardItems.get(itemPosition).setLabel3(lateProjectCount.toPlainString());

                    if (projectCount.signum() == 1){
                        BigDecimal progressInPercentage = lateProjectCount.divide(projectCount, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                        dashboardItems.get(itemPosition).setLabel4(decimalFormat.format(progressInPercentage) + "%");
                    }else{
                        dashboardItems.get(itemPosition).setLabel4("-");
                    }

//                    BigDecimal prevProgressInPercentage = new BigDecimal("0.0");
//                    if (prevRkap.signum() == 1){
//                        prevProgressInPercentage = prevNetProfit.divide(prevRkap, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
//                    }

                    String statusImageName = "equal_trend";
                    String percentageLabelColor = "blue";
                    if (lateProjectCount.compareTo(prevLateProjectCount) == -1){
                        statusImageName = "down_trend";
                        percentageLabelColor = "#7ED321";
                    }else if (lateProjectCount.compareTo(prevLateProjectCount) == 1){
                        statusImageName = "up_trend_red";
                        percentageLabelColor = "red";
                    }

                    dashboardItems.get(itemPosition).setStatusImageName(statusImageName);
                    dashboardItems.get(itemPosition).setPercentageLabelColor(percentageLabelColor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", MainActivity.this.username, MainActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void getScoreCardData() throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "summarydata/score-card/" + selectedYear + "/" + (selectedMonth + 1);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                int itemPosition = 2;

                try {

                    BigDecimal total = new BigDecimal(response.getString("total"));
                    BigDecimal target = new BigDecimal(response.getString("target"));
                    BigDecimal prevTotal = new BigDecimal(response.getString("prevTotal"));

                    dashboardItems.get(itemPosition).setLabel3(total.toPlainString());

                    if (target.signum() == 1){
                        BigDecimal progressInPercentage = total.divide(target, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                        dashboardItems.get(itemPosition).setLabel4(decimalFormat.format(progressInPercentage) + "%");
                    }else{
                        dashboardItems.get(itemPosition).setLabel4("-");
                    }

                    String statusImageName = "equal_trend";
                    String percentageLabelColor = "blue";
                    if (total.compareTo(prevTotal) == 1){
                        statusImageName = "up_trend";
                        percentageLabelColor = "#7ED321";
                    }else if (total.compareTo(prevTotal) == -1){
                        statusImageName = "down_trend_red";
                        percentageLabelColor = "red";
                    }

                    dashboardItems.get(itemPosition).setStatusImageName(statusImageName);
                    dashboardItems.get(itemPosition).setPercentageLabelColor(percentageLabelColor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", MainActivity.this.username, MainActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void getRiskInfoData() throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "summarydata/risk-info/" + selectedYear + "/" + (selectedMonth + 1);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                int itemPosition = 3;

                try {

                    BigDecimal extremeRiskCount = new BigDecimal(response.getString("extremeRiskCount"));
                    BigDecimal riskCount = new BigDecimal(response.getString("riskCount"));
                    BigDecimal prevExtremeRiskCount = new BigDecimal(response.getString("prevExtremeRiskCount"));
                    BigDecimal extremeRiskCountInMillion = extremeRiskCount.divide(new BigDecimal("1000000"), 4, BigDecimal.ROUND_HALF_UP);

                    dashboardItems.get(itemPosition).setLabel3(extremeRiskCountInMillion.toPlainString());

                    if (riskCount.signum() == 1){
                        BigDecimal progressInPercentage = extremeRiskCount.divide(riskCount, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                        dashboardItems.get(itemPosition).setLabel4(decimalFormat.format(progressInPercentage) + "%");
                    }else{
                        dashboardItems.get(itemPosition).setLabel4("-");
                    }

                    String statusImageName = "equal_trend";
                    String percentageLabelColor = "blue";
                    if (extremeRiskCount.compareTo(prevExtremeRiskCount) == 1){
                        statusImageName = "up_trend_red";
                        percentageLabelColor = "red";
                    }else if (extremeRiskCount.compareTo(prevExtremeRiskCount) == -1){
                        statusImageName = "down_trend";
                        percentageLabelColor = "#7ED321";
                    }

                    dashboardItems.get(itemPosition).setStatusImageName(statusImageName);
                    dashboardItems.get(itemPosition).setPercentageLabelColor(percentageLabelColor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", MainActivity.this.username, MainActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void getSmwgData() throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "summarydata/smwg/" + selectedYear + "/" + (selectedMonth + 1);
        Log.v("test", "selectedMonth : " + selectedMonth + ", selectedYear : " + selectedYear);
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                try {

                    BigDecimal qmsl = new BigDecimal(response.getString("qmsl"));
                    BigDecimal qmslTotalScore = new BigDecimal(response.getString("qmslTotalScore"));
                    BigDecimal projectCountQmsl = new BigDecimal(response.getJSONObject("projectCount").getString("qmsl"));


                    if(projectCountQmsl.signum() == 1){
                        BigDecimal progressInPercentage = qmsl.divide(projectCountQmsl, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")); //(Double(tmpQmsl!) / Double(tmpProjectCountQmsl!)) * 100;
                        BigDecimal avarageInPercentage = qmslTotalScore.divide(projectCountQmsl, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")); //(Double(tmpQmsl!) / Double(tmpProjectCountQmsl!)) * 100;

                        dashboardItems.get(7).setValue1(progressInPercentage);
                        dashboardItems.get(7).setLabel2(decimalFormat.format(avarageInPercentage.floatValue() / 100));
                    }

                    //----

                    BigDecimal sheLevel = new BigDecimal(response.getString("sheLevel"));
                    BigDecimal sheLevelTotalScore = new BigDecimal(response.getString("sheLevelTotalScore"));
                    BigDecimal projectCountSheLevel = new BigDecimal(response.getJSONObject("projectCount").getString("sheLevel"));


                    if(projectCountSheLevel.signum() == 1){
                        BigDecimal progressInPercentage = sheLevel.divide(projectCountSheLevel, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")); //(Double(tmpQmsl!) / Double(tmpProjectCountQmsl!)) * 100;
                        BigDecimal avarageInPercentage = sheLevelTotalScore.divide(projectCountSheLevel, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")); //(Double(tmpQmsl!) / Double(tmpProjectCountQmsl!)) * 100;

                        dashboardItems.get(8).setValue1(progressInPercentage);
                        dashboardItems.get(8).setLabel2(decimalFormat.format(avarageInPercentage.floatValue() / 100));
                    }

                    //----

                    BigDecimal limaR = new BigDecimal(response.getString("limaR"));
                    BigDecimal limaRTotalScore = new BigDecimal(response.getString("limaRTotalScore"));
                    BigDecimal projectCountLimaR = new BigDecimal(response.getJSONObject("projectCount").getString("limaR"));


                    if(projectCountLimaR.signum() == 1){
                        BigDecimal progressInPercentage = limaR.divide(projectCountLimaR, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")); //(Double(tmpQmsl!) / Double(tmpProjectCountQmsl!)) * 100;
                        BigDecimal avarageInPercentage = limaRTotalScore.divide(projectCountLimaR, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")); //(Double(tmpQmsl!) / Double(tmpProjectCountQmsl!)) * 100;

                        dashboardItems.get(9).setValue1(progressInPercentage);
                        dashboardItems.get(9).setLabel2(decimalFormat.format(avarageInPercentage.floatValue() / 100));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", MainActivity.this.username, MainActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void getEmployeeBirthdayDataCount() throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "employeedata/birthdaycount/" + (selectedMonth + 1);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                int itemPosition = 5;

                try {

                    Integer birthdayCount = response.getInt("totalRecords");
                    dashboardItems.get(itemPosition).setLabel3(birthdayCount.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", MainActivity.this.username, MainActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void getCommentDataCount() throws Exception{

        String baseURL = DashboardConstant.BASE_URL + "comment/commentcount/" + (selectedMonth + 1) + "/" + selectedYear;

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                int itemPosition = 6;

                try {

                    Integer commentCount = response.getInt("totalComment");
                    dashboardItems.get(itemPosition).setLabel3(commentCount.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", MainActivity.this.username, MainActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    public void updateDashboardData(int month) {

        this.selectedMonth = month;
        this.updateDashboardData();
    }

    private void updateSelectedMonthYearLabel(){
        monthSelectLabel.setText(DashboardConstant.MONTHS[this.selectedMonth] + ", " + this.selectedYear);
    }

    private void selectPageOnMonthSelectPager(){

        if(this.monthSelectFragment1 != null && this.monthSelectFragment2 != null){
            this.monthSelectFragment1.clearButtonColor();
            this.monthSelectFragment2.clearButtonColor();

            if(this.selectedMonth < 7){
                this.monthSelectFragment1.setActiveButtonColor(this.selectedMonth);
                monthSelectPager.setCurrentItem(0, true);
            }else{
                this.monthSelectFragment2.setActiveButtonColor(this.selectedMonth-6);
                monthSelectPager.setCurrentItem(1, true);
            }
        }

    }

    public void updateDashboardData() {

        try {
            getSmwgData();
            getNetProfitData();
            getProjectInfoData();
            getScoreCardData();
            getRiskInfoData();
            updateSelectedMonthYearLabel();
            zoomChart();
            zoomSalesChart();
            zoomPiutangChart();
            selectPageOnMonthSelectPager();
            getEmployeeBirthdayDataCount();
            getCommentDataCount();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DashboardViewHolder extends RecyclerView.ViewHolder {

        private ImageView cardDashboardImage1;
        private TextView cardDashboardLabel1;
        private TextView cardDashboardLabel2;
        private TextView cardDashboardLabel3;
        private TextView cardDashboardLabel4;

        public DashboardViewHolder(View itemView) {
            super(itemView);


            cardDashboardImage1 = (ImageView)itemView.findViewById(R.id.card_dashboard_image_1);
            cardDashboardLabel1 = (TextView)itemView.findViewById(R.id.card_dashboard_label_1);
            cardDashboardLabel2 = (TextView)itemView.findViewById(R.id.card_dashboard_label_2);
            cardDashboardLabel3 = (TextView)itemView.findViewById(R.id.card_dashboard_label_3);
            cardDashboardLabel4 = (TextView)itemView.findViewById(R.id.card_dashboard_label_4);
        }

        public void updateUI(DashboardItem item) {

            if(item.getStatusImageName().equals("equal_trend")){
                cardDashboardImage1.setImageDrawable(getResources().getDrawable(R.drawable.equal_trend));
            }else if(item.getStatusImageName().equals("up_trend")){
                cardDashboardImage1.setImageDrawable(getResources().getDrawable(R.drawable.up_trend));
            }else if(item.getStatusImageName().equals("up_trend_red")){
                cardDashboardImage1.setImageDrawable(getResources().getDrawable(R.drawable.up_trend_red));
            }else if(item.getStatusImageName().equals("down_trend")){
                cardDashboardImage1.setImageDrawable(getResources().getDrawable(R.drawable.down_trend));
            }else if(item.getStatusImageName().equals("down_trend_red")){
                cardDashboardImage1.setImageDrawable(getResources().getDrawable(R.drawable.down_trend_red));
            }else if(item.getStatusImageName().equals("ulangTahun")){
                cardDashboardImage1.setImageDrawable(getResources().getDrawable(R.drawable.ic_cake_black_24dp));
            }else if(item.getStatusImageName().equals("komentarGambar")){
                cardDashboardImage1.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_black_24dp));
            }
            cardDashboardLabel1.setText(item.getLabel1());
            cardDashboardLabel2.setText(item.getLabel2());
            cardDashboardLabel3.setText(item.getLabel3());
            cardDashboardLabel4.setText(item.getLabel4());

            cardDashboardLabel4.setTextColor(Color.parseColor(item.getPercentageLabelColor()));
        }
    }

    public class RingProgressBarHolder extends RecyclerView.ViewHolder {

        private RingProgressBar ringProgressBar1;
        private TextView ringProgressBarLabel1;
        private TextView ringProgressBarLabel2;


        public RingProgressBarHolder(View itemView) {
            super(itemView);


            ringProgressBar1 = (RingProgressBar)itemView.findViewById(R.id.ring_progress_bar_1);
            ringProgressBarLabel1 = (TextView) itemView.findViewById(R.id.ring_progress_bar_label_1);
            ringProgressBarLabel2 = (TextView) itemView.findViewById(R.id.ring_progress_bar_label_2);

        }

        public void updateUI(DashboardItem item) {
            ringProgressBar1.setProgress(item.getValue1().toBigInteger().intValue());
            ringProgressBarLabel1.setText(item.getLabel1());
            ringProgressBarLabel2.setText(item.getLabel2());
        }
    }

    public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<DashboardItem> list;

        public DashboardAdapter(ArrayList<DashboardItem> list) {
            this.list = list;
        }

        @Override
        public int getItemViewType(int position) {
            if(position < 7){
                return 1;
            }else{
                return 2;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            DailyWeatherReport report = list.get(position);

            if(position < 7){
                DashboardViewHolder dashboardViewHolder = (DashboardViewHolder) holder;
                dashboardViewHolder.updateUI(list.get(position));
            }else{
                RingProgressBarHolder ringProgressBarHolder = (RingProgressBarHolder) holder;
                ringProgressBarHolder.updateUI(list.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if(viewType == 1){
                View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dashboard, parent, false);
                card.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        int itemPosition = mRecyclerView.getChildAdapterPosition(view);
//                        Log.v("Test", "Test recycler listener itemPosition : " + itemPosition);

                        if(itemPosition == 0){
                            Intent intent = new Intent(MainActivity.this, NetProfitActivity.class);

                            intent.putExtra("username", MainActivity.this.username);
                            intent.putExtra("password", MainActivity.this.password);
                            intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
                            intent.putExtra("selectedYear", MainActivity.this.selectedYear);
                            startActivity(intent);
                        }else if(itemPosition == 1){
                            Intent intent = new Intent(MainActivity.this, ProjectActivity.class);

                            intent.putExtra("username", MainActivity.this.username);
                            intent.putExtra("password", MainActivity.this.password);
                            intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
                            intent.putExtra("selectedYear", MainActivity.this.selectedYear);
                            intent.putExtra("selectedFilter", 0);
                            intent.putExtra("selectedSort", 0);
                            startActivity(intent);
                        }else if(itemPosition == 2){
                            Intent intent = new Intent(MainActivity.this, ScoreCardActivity.class);

                            intent.putExtra("username", MainActivity.this.username);
                            intent.putExtra("password", MainActivity.this.password);
                            intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
                            intent.putExtra("selectedYear", MainActivity.this.selectedYear);
                            startActivity(intent);
                        }else if(itemPosition == 5){
                            Intent intent = new Intent(MainActivity.this, BirthdayActivity.class);

                            intent.putExtra("username", MainActivity.this.username);
                            intent.putExtra("password", MainActivity.this.password);
                            intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
                            intent.putExtra("selectedYear", MainActivity.this.selectedYear);
                            startActivity(intent);
                        }else if(itemPosition == 6){
                            Intent intent = new Intent(MainActivity.this, AllCommentAvtivity.class);

                            intent.putExtra("username", MainActivity.this.username);
                            intent.putExtra("password", MainActivity.this.password);
                            intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
                            intent.putExtra("selectedYear", MainActivity.this.selectedYear);
                            startActivity(intent);
                        }

                    }
                });

                return new DashboardViewHolder(card);
            }else{
                View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ring_progress_bar, parent, false);
                card.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        int itemPosition = mRecyclerView.getChildAdapterPosition(view);

                        if(itemPosition == 7){
                            Intent intent = new Intent(MainActivity.this, SmwgActivity.class);

                            intent.putExtra("username", MainActivity.this.username);
                            intent.putExtra("password", MainActivity.this.password);
                            intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
                            intent.putExtra("selectedYear", MainActivity.this.selectedYear);
                            intent.putExtra("smwgType", "QMSL");
                            startActivity(intent);
                        }else if(itemPosition == 8){
                            Intent intent = new Intent(MainActivity.this, SmwgActivity.class);

                            intent.putExtra("username", MainActivity.this.username);
                            intent.putExtra("password", MainActivity.this.password);
                            intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
                            intent.putExtra("selectedYear", MainActivity.this.selectedYear);
                            intent.putExtra("smwgType", "SHE_LEVEL");
                            startActivity(intent);
                        }else if(itemPosition == 9){
                            Intent intent = new Intent(MainActivity.this, SmwgActivity.class);

                            intent.putExtra("username", MainActivity.this.username);
                            intent.putExtra("password", MainActivity.this.password);
                            intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
                            intent.putExtra("selectedYear", MainActivity.this.selectedYear);
                            intent.putExtra("smwgType", "5R");
                            startActivity(intent);
                        }

                    }
                });
                return new RingProgressBarHolder(card);
            }

        }
    }

    private MonthSelectFragment monthSelectFragment1;
    private MonthSelectFragment monthSelectFragment2;
    public class MonthSelectPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;

        public MonthSelectPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return MonthSelectFragment.newInstance(0, "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return MonthSelectFragment.newInstance(1, "Page # 2");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    monthSelectFragment1 = (MonthSelectFragment) createdFragment;
                    break;
                case 1:
                    monthSelectFragment2 = (MonthSelectFragment) createdFragment;
                    break;
            }

            return createdFragment;
        }

    }

    @Override
    public void onMonthSelectFragmentInteraction(Uri uri) {
        selectPageOnMonthSelectPager();
    }

    private OmzetChartFragment omzetChartFragment;
    private SalesChartFragment salesChartFragment;
    private PiutangChartFragment piutangChartFragment;

    public  class ChartPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 3;


        public ChartPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OmzetChartFragment.newInstance(MainActivity.this.username, MainActivity.this.password, MainActivity.this.selectedMonth, MainActivity.this.selectedYear);
                case 1:
                    return SalesChartFragment.newInstance(MainActivity.this.username, MainActivity.this.password, MainActivity.this.selectedMonth, MainActivity.this.selectedYear);
                case 2:
                    return PiutangChartFragment.newInstance(MainActivity.this.username, MainActivity.this.password, MainActivity.this.selectedMonth, MainActivity.this.selectedYear);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    omzetChartFragment = (OmzetChartFragment) createdFragment;
                    break;
                case 1:
                    salesChartFragment = (SalesChartFragment) createdFragment;
                    break;
                case 2:
                    piutangChartFragment = (PiutangChartFragment) createdFragment;
                    break;

            }
            return createdFragment;
        }


    }

    @Override
    public void onOmzetChartFragmentInteraction(Uri uri) {
        zoomChart();
    }

    @Override
    public void onOmzetChartDetailButtonPressed() {
        showOmzetChartDetail();
    }

    @Override
    public void onSalesChartDetailButtonPressed() {
        showSalesChartDetail();
    }

    @Override
    public void onPiutangChartDetailButtonPressed() {
        showPiutangChartDetail();
    }

    @Override
    public void onSalesChartFragmentInteraction(Uri uri) {
        zoomSalesChart();
    }

    @Override
    public void onPiutangChartFragmentInteraction(Uri uri) {
        zoomPiutangChart();
    }

    private void zoomChart(){
//        OmzetChartFragment omzetChartFragment = (OmzetChartFragment) chartAdapterViewPager.getItem(0);
        if(this.omzetChartFragment != null){
//            this.omzetChartFragment.zoomToValue(this.selectedMonth);
            this.omzetChartFragment.update(this.selectedMonth, this.selectedYear);
        }
    }

    private void zoomSalesChart(){
        if(this.salesChartFragment != null){
            this.salesChartFragment.update(this.selectedMonth, this.selectedYear);
        }
    }

    private void zoomPiutangChart() {
        if (this.piutangChartFragment != null) {
            this.piutangChartFragment.update(this.selectedMonth, this.selectedYear);
        }
    }

    public void addDots() {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout)findViewById(R.id.dots);

        for(int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(this);

            dot.setImageDrawable(getResources().getDrawable(R.drawable.pager_dot_not_selected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(10, 0, 10, 0);

            LinearLayout layout = new LinearLayout(this);
            layout.addView(dot);

            dotsLayout.addView(layout, params);

            dots.add(dot);
        }

        chartPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.v("Page select", "position : " + position);
                selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void selectDot(int idx) {
        Resources res = getResources();
        for(int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i==idx)?(R.drawable.pager_dot_selected):(R.drawable.pager_dot_not_selected);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            this.username = "";
            this.password = "";
            SharedPreferences preferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan tombol BACK sekali lagi untuk KELUAR", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    private void showOmzetChartDetail(){
        Intent intent = new Intent(MainActivity.this, NetProfitActivity.class);

        intent.putExtra("username", MainActivity.this.username);
        intent.putExtra("password", MainActivity.this.password);
        intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
        intent.putExtra("selectedYear", MainActivity.this.selectedYear);
        startActivity(intent);
    }

    private void showSalesChartDetail(){
        Intent intent = new Intent(MainActivity.this, NetProfitActivity.class);

        intent.putExtra("username", MainActivity.this.username);
        intent.putExtra("password", MainActivity.this.password);
        intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
        intent.putExtra("selectedYear", MainActivity.this.selectedYear);
        startActivity(intent);
    }

    private void showPiutangChartDetail(){
        Intent intent = new Intent(MainActivity.this, ProjectActivity.class);

        intent.putExtra("username", MainActivity.this.username);
        intent.putExtra("password", MainActivity.this.password);
        intent.putExtra("selectedMonth", MainActivity.this.selectedMonth);
        intent.putExtra("selectedYear", MainActivity.this.selectedYear);
        intent.putExtra("selectedFilter", 0);
        intent.putExtra("selectedSort", 2);
        startActivity(intent);
    }

}
