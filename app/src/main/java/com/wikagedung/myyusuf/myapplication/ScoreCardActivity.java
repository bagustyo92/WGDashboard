package com.wikagedung.myyusuf.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wikagedung.myyusuf.myapplication.fragments.ScoreCardList1Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.ScoreCardList2Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.ScoreCardList3Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.ScoreCardList4Fragment;
import com.wikagedung.myyusuf.myapplication.fragments.ScoreCardList5Fragment;
import com.wikagedung.myyusuf.myapplication.model.ScoreCard;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreCardActivity extends AppCompatActivity implements ScoreCardList1Fragment.OnScoreCardList1FragmentInteractionListener,
        ScoreCardList2Fragment.OnScoreCardList2FragmentInteractionListener,
        ScoreCardList3Fragment.OnScoreCardList3FragmentInteractionListener,
        ScoreCardList4Fragment.OnScoreCardList4FragmentInteractionListener,
        ScoreCardList5Fragment.OnScoreCardList5FragmentInteractionListener{

    private static final String TAG = "ScoreCardActivity";

    TextView monthSelectLabel;
    FragmentPagerAdapter scoreCardAdapterViewPager;
    ViewPager scoreCardViewPager;
    private ScoreCardList1Fragment scoreCardList1Fragment;
    private ScoreCardList2Fragment scoreCardList2Fragment;
    private ScoreCardList3Fragment scoreCardList3Fragment;
    private ScoreCardList4Fragment scoreCardList4Fragment;
    private ScoreCardList5Fragment scoreCardList5Fragment;

    private String username;
    private String password;

    private static final int NUM_PAGES = 5;
    private int selectedMonth = 1;
    private int selectedYear = 2016;

    private int currentPage;
    private int mCurrentPage;

    ArrayList<ImageView> dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        TextView toolbarTitle1 = (TextView) findViewById(R.id.tool_bar_title1);
        toolbarTitle1.setText("Score Card");

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

        createScoreCardPager();
        addDots();
        selectDot(0);

        getScoreCardData();

        final FloatingActionButton buttonComment = (FloatingActionButton) findViewById(R.id.score_card_float_comment_button);

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(ScoreCardActivity.this, CommentActivity.class);
                intent.putExtra("username", ScoreCardActivity.this.username);
                intent.putExtra("password", ScoreCardActivity.this.password);
                intent.putExtra("selectedMonth", selectedMonth);
                intent.putExtra("selectedYear", selectedYear);
                mCurrentPage = currentPage + 8;
                intent.putExtra("page", Integer.toString(mCurrentPage));
                intent.putExtra("commentTitle", Integer.toString(mCurrentPage));
                startActivity(intent);
//                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coor_layout),
//                        "COMING SOON !", Snackbar.LENGTH_LONG);
//                mySnackbar.show();
            }
        });
    }

    private void updateFragment(JSONObject jsonData){
        updateSelectedMonthYearLabel();
        scoreCardList1Fragment.loadData(jsonData);
        scoreCardList2Fragment.loadData(jsonData);
        scoreCardList3Fragment.loadData(jsonData);
        scoreCardList4Fragment.loadData(jsonData);
        scoreCardList5Fragment.loadData(jsonData);

    }

    private void getScoreCardData(){

        String baseURL = DashboardConstant.BASE_URL + "drilldowndata/score-card-dd/WGPUS001/" + selectedYear + "/" + (selectedMonth + 1);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                try {

//                    JSONObject jsonData = response.getJSONObject("jsonData");

                    ScoreCardActivity.this.updateFragment(response);


                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", ScoreCardActivity.this.username, ScoreCardActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);

    }

    private void createScoreCardPager() {
        scoreCardViewPager = (ViewPager) findViewById(R.id.score_card_pager);
        scoreCardAdapterViewPager = new ScoreCardPagerAdapter(getSupportFragmentManager());
        scoreCardViewPager.setAdapter(scoreCardAdapterViewPager);

        scoreCardViewPager.setOffscreenPageLimit(7);

        scoreCardViewPager.setCurrentItem(0);
    }

    @Override
    public void onScoreCardList1FragmentInteraction(Uri uri) {

    }

    @Override
    public void onScoreCardList2FragmentInteraction(Uri uri) {

    }

    @Override
    public void onScoreCardList3FragmentInteraction(Uri uri) {

    }

    @Override
    public void onScoreCardList4FragmentInteraction(Uri uri) {

    }

    @Override
    public void onScoreCardList5FragmentInteraction(Uri uri) {

    }

    public  class ScoreCardPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 5;


        public ScoreCardPagerAdapter(FragmentManager fragmentManager) {
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
                    return ScoreCardList1Fragment.newInstance("", "");
                case 1:
                    return ScoreCardList2Fragment.newInstance("", "");
                case 2:
                    return ScoreCardList3Fragment.newInstance("", "");
                case 3:
                    return ScoreCardList4Fragment.newInstance("", "");
                case 4:
                    return ScoreCardList5Fragment.newInstance("", "");
//                case 5:
//                    return ScoreCardList6Fragment.newInstance("", "");
//                case 6:
//                    return ScoreCardList7Fragment.newInstance("", "");
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
                    scoreCardList1Fragment = (ScoreCardList1Fragment) createdFragment;
                    break;
                case 1:
                    scoreCardList2Fragment = (ScoreCardList2Fragment) createdFragment;
                    break;
                case 2:
                    scoreCardList3Fragment = (ScoreCardList3Fragment) createdFragment;
                    break;
                case 3:
                    scoreCardList4Fragment = (ScoreCardList4Fragment) createdFragment;
                    break;
                case 4:
                    scoreCardList5Fragment = (ScoreCardList5Fragment) createdFragment;
                    break;
//                case 5:
//                    scoreCardList6Fragment = (ScoreCardList6Fragment) createdFragment;
//                    break;
//                case 6:
//                    scoreCardList7Fragment = (ScoreCardList7Fragment) createdFragment;
//                    break;

            }
            return createdFragment;
        }


    }

    private void updateSelectedMonthYearLabel(){
        monthSelectLabel.setText(DashboardConstant.MONTHS[this.selectedMonth] + ", " + this.selectedYear);
    }

    private void makeDatePickerDialog() {
        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        DatePickerDialog.OnDateSetListener dateOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                // Prevent called twice
                if (view.isShown()) {
                    ScoreCardActivity.this.selectedMonth = selectedMonth;
                    ScoreCardActivity.this.selectedYear = selectedYear;
                    ScoreCardActivity.this.updateSelectedMonthYearLabel();
                    ScoreCardActivity.this.getScoreCardData();
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

        scoreCardViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        for(int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i==idx)?(R.drawable.pager_dot_selected):(R.drawable.pager_dot_not_selected);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }
}
