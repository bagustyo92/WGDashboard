package com.wikagedung.myyusuf.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cenkgun.chatbar.ChatBarView;
import com.wikagedung.myyusuf.myapplication.fragments.NetProfitList1Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.NetProfitList2Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.NetProfitList3Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.NetProfitList4Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.NetProfitList5Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.NetProfitList6Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.NetProfitList7Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetProfitActivity extends AppCompatActivity
        implements NetProfitList1Fragment.OnNetProfitList1FragmentInteractionListener,
        NetProfitList2Fragment.OnNetProfitList2FragmentInteractionListener,
        NetProfitList3Fragment.OnNetProfitList3FragmentInteractionListener,
        NetProfitList4Fragment.OnNetProfitList4FragmentInteractionListener,
        NetProfitList5Fragment.OnNetProfitList5FragmentInteractionListener,
        NetProfitList6Fragment.OnNetProfitList6FragmentInteractionListener,
        NetProfitList7Fragment.OnNetProfitList7FragmentInteractionListener

{

    TextView monthSelectLabel;
    FragmentPagerAdapter netProfitAdapterViewPager;
    ViewPager netProfitViewPager;

    private NetProfitList1Fragment netProfitList1Fragment;
    private NetProfitList2Fragment netProfitList2Fragment;
    private NetProfitList3Fragment netProfitList3Fragment;
    private NetProfitList4Fragment netProfitList4Fragment;
    private NetProfitList5Fragment netProfitList5Fragment;
    private NetProfitList6Fragment netProfitList6Fragment;
    private NetProfitList7Fragment netProfitList7Fragment;

    private String username;
    private String password;

    private static final int NUM_PAGES = 7;
    private int selectedMonth = 1;
    private int selectedYear = 2016;

    private int currentPage;

    ArrayList<ImageView> dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_profit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        TextView toolbarTitle1 = (TextView) findViewById(R.id.tool_bar_title1);
        toolbarTitle1.setText("Hasil Usaha");

        //IMPORTANT!!!
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        selectedMonth = intent.getIntExtra("selectedMonth", 1);
        selectedYear = intent.getIntExtra("selectedYear", 2016);

        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.android_back_button);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        updateSelectedMonthYearLabel();
        makeDatePickerDialog();
        createNetProfitPager();
        addDots();
        selectDot(0);

        getNetProfitData();

        final FloatingActionButton buttonComment = (FloatingActionButton) findViewById(R.id.float_comment_button);

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(NetProfitActivity.this, CommentActivity.class);
                intent.putExtra("username", NetProfitActivity.this.username);
                intent.putExtra("password", NetProfitActivity.this.password);
                intent.putExtra("selectedMonth", selectedMonth);
                intent.putExtra("selectedYear", selectedYear);
                intent.putExtra("commentTitle", Integer.toString(currentPage));
                intent.putExtra("page", Integer.toString(currentPage));
                startActivity(intent);
//                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coor_layout),
//                        "COMING SOON !", Snackbar.LENGTH_LONG);
//                mySnackbar.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFragmentComment(JSONObject jsonData){
//        comment1Fragment.loadData(jsonData);
//        comment2Fragment.loadData(jsonData);
//        comment3Fragment.loadData(jsonData);
//        comment4Fragment.loadData(jsonData);
//        comment5Fragment.loadData(jsonData);
//        comment6Fragment.loadData(jsonData);
//        comment7Fragment.loadData(jsonData);
    }

    private void updateFragment(JSONObject jsonData) {
        updateSelectedMonthYearLabel();
        netProfitList1Fragment.loadData(jsonData);
        netProfitList2Fragment.loadData(jsonData);
        netProfitList3Fragment.loadData(jsonData);
        netProfitList4Fragment.loadData(jsonData);
        netProfitList5Fragment.loadData(jsonData);
        netProfitList6Fragment.loadData(jsonData);
        netProfitList7Fragment.loadData(jsonData);
    }

    private void updateSelectedMonthYearLabel() {
        monthSelectLabel.setText(DashboardConstant.MONTHS[this.selectedMonth] + ", " + this.selectedYear);
    }

    private void createNetProfitPager() {
        netProfitViewPager = (ViewPager) findViewById(R.id.net_profit_pager);
        netProfitAdapterViewPager = new NetProfitPagerAdapter(getSupportFragmentManager());
        netProfitViewPager.setAdapter(netProfitAdapterViewPager);

        netProfitViewPager.setOffscreenPageLimit(7);

        netProfitViewPager.setCurrentItem(0);
    }

    public class NetProfitPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 7;


        public NetProfitPagerAdapter(FragmentManager fragmentManager) {
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
                    return NetProfitList1Fragment.newInstance("", "");
                case 1:
                    return NetProfitList2Fragment.newInstance("", "");
                case 2:
                    return NetProfitList3Fragment.newInstance("", "");
                case 3:
                    return NetProfitList4Fragment.newInstance("", "");
                case 4:
                    return NetProfitList5Fragment.newInstance("", "");
                case 5:
                    return NetProfitList6Fragment.newInstance("", "");
                case 6:
                    return NetProfitList7Fragment.newInstance("", "");
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
                    netProfitList1Fragment = (NetProfitList1Fragment) createdFragment;
                    break;
                case 1:
                    netProfitList2Fragment = (NetProfitList2Fragment) createdFragment;
                    break;
                case 2:
                    netProfitList3Fragment = (NetProfitList3Fragment) createdFragment;
                    break;
                case 3:
                    netProfitList4Fragment = (NetProfitList4Fragment) createdFragment;
                    break;
                case 4:
                    netProfitList5Fragment = (NetProfitList5Fragment) createdFragment;
                    break;
                case 5:
                    netProfitList6Fragment = (NetProfitList6Fragment) createdFragment;
                    break;
                case 6:
                    netProfitList7Fragment = (NetProfitList7Fragment) createdFragment;
                    break;

            }
            return createdFragment;
        }


    }

    private void makeDatePickerDialog() {
        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        DatePickerDialog.OnDateSetListener dateOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                datePick.setText(new StringBuilder().append(selectedDay).append("-").append(selectedMonth).append("-").append(selectedYear));

                // Prevent called twice
                if (view.isShown()) {
                    NetProfitActivity.this.selectedMonth = selectedMonth;
                    NetProfitActivity.this.selectedYear = selectedYear;
                    NetProfitActivity.this.updateSelectedMonthYearLabel();
                    NetProfitActivity.this.getNetProfitData();
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
    }

//    private void setInitialMonth(){
//        Calendar calendar = Calendar.getInstance();
//        this.selectedYear = calendar.get(Calendar.YEAR);
//        this.selectedMonth = calendar.get(Calendar.MONTH);
//        if(this.selectedMonth == 0){
//            this.selectedYear = this.selectedYear - 1;
//            this.selectedMonth = 11;
//        }else{
//            this.selectedMonth = this.selectedMonth - 1;
//        }
//    }

    private DatePickerDialog createDialogWithoutDateField(DatePickerDialog.OnDateSetListener dateOnDateSetListener) {
        DatePickerDialog dpd = new DatePickerDialog(this, android.R.style.Theme_Holo_Dialog, dateOnDateSetListener, selectedYear, selectedMonth, 24);
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
                        if (daySpinnerId != 0) {
                            View daySpinner = datePicker.findViewById(daySpinnerId);
                            if (daySpinner != null) {
                                daySpinner.setVisibility(View.GONE);
                            }
                        }
//                        if ("mDaySpinner".equals(datePickerField.getName())) {
//
//                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return dpd;
    }

    public void addDots() {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.dots);

        for (int i = 0; i < NUM_PAGES; i++) {
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

        netProfitViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position);
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void selectDot(int idx) {
        Resources res = getResources();
        for (int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i == idx) ? (R.drawable.pager_dot_selected) : (R.drawable.pager_dot_not_selected);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    @Override
    public void onNetProfitList1FragmentInteraction(Uri uri) {

    }

    @Override
    public void onNetProfitList2FragmentInteraction(Uri uri) {

    }

    @Override
    public void onNetProfitList3FragmentInteraction(Uri uri) {

    }

    @Override
    public void onNetProfitList4FragmentInteraction(Uri uri) {

    }

    @Override
    public void onNetProfitList5FragmentInteraction(Uri uri) {

    }

    @Override
    public void onNetProfitList6FragmentInteraction(Uri uri) {

    }

    @Override
    public void onNetProfitList7FragmentInteraction(Uri uri) {

    }
    //////////////////////////////////////

    private void getNetProfitData() {

        String baseURL = DashboardConstant.BASE_URL + "drilldowndata/total_kontrak_dihadapi/" + selectedYear + "/" + (selectedMonth + 1);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject jsonData = response.getJSONObject("jsonData");

                    NetProfitActivity.this.updateFragment(jsonData);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        };

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", NetProfitActivity.this.username, NetProfitActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);

    }
}
