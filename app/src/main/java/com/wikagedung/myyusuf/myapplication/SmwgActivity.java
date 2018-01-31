package com.wikagedung.myyusuf.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wikagedung.myyusuf.myapplication.model.Smwg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SmwgActivity extends AppCompatActivity {

    private static final String TAG = "ProjectActivity";

    RecyclerView mRecyclerView;
    private SmwgAdapter adapter;


    private String smwgType = "";

    TextView monthSelectLabel;
    private String username;
    private String password;

    private int selectedMonth = 1;
    private int selectedYear = 2016;

    private String commentTitle;
    private int currentPage;


    private ArrayList<Smwg> smwgList = new ArrayList<>();
    DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
    private static final String RED_COLOR = "#FF2B2B";
    private static final String GREEN_COLOR = "#70AA2D";
    private static final String BLUE_COLOR = "#3232FF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smwg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        //IMPORTANT!!!
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        selectedMonth = intent.getIntExtra("selectedMonth", 1);
        selectedYear = intent.getIntExtra("selectedYear", 2016);
        smwgType = intent.getStringExtra("smwgType");

        TextView toolbarTitle1 = (TextView) findViewById(R.id.tool_bar_title1);
        toolbarTitle1.setText(smwgType);

        mRecyclerView = (RecyclerView) findViewById(R.id.smwg_recycler_view);

        adapter = new SmwgAdapter();
        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.android_back_button);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        updateSelectedMonthYearLabel();
        makeDatePickerDialog();

        getSmwgData();

        final FloatingActionButton buttonComment = (FloatingActionButton) findViewById(R.id.smwg_float_comment_button);

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(SmwgActivity.this, CommentActivity.class);
                intent.putExtra("username", SmwgActivity.this.username);
                intent.putExtra("password", SmwgActivity.this.password);
                intent.putExtra("selectedMonth", selectedMonth);
                intent.putExtra("selectedYear", selectedYear);
                intent.putExtra("page", commentTitle);
//                intent.putExtra("pageString", commentTitle);
                intent.putExtra("commentTitle", commentTitle);
                startActivity(intent);
//                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coor_layout),
//                        "COMING SOON !", Snackbar.LENGTH_LONG);
//                mySnackbar.show();
            }
        });

    }

    private void getSmwgData(){

        String baseURL = "";

        if("QMSL".equals(smwgType)){
            baseURL = DashboardConstant.BASE_URL + "drilldowndata/qmsl-dd/" + selectedYear + "/" + (selectedMonth + 1);
//            currentPage = 13;
            commentTitle = "QMSL";
        }else if("SHE_LEVEL".equals(smwgType)){
            baseURL = DashboardConstant.BASE_URL + "drilldowndata/she-level-dd/" + selectedYear + "/" + (selectedMonth + 1);
//            currentPage = 14;
            commentTitle = "SHE_Level";
        }else{
            baseURL = DashboardConstant.BASE_URL + "drilldowndata/lima-r-dd/" + selectedYear + "/" + (selectedMonth + 1);
//            currentPage = 15;
            commentTitle = "5R";
        }

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {

                SmwgActivity.this.smwgList.clear();

                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);

                        Smwg smwg = new Smwg();

                        String tmpTrend = obj.getString("trend");
                        String trend = "0";
                        if(!"=".equals(tmpTrend)){
                            trend = tmpTrend;
                            if(Integer.parseInt(tmpTrend) > 0){
                                smwg.setTrendTextColor(SmwgActivity.RED_COLOR);
                            }else{
                                smwg.setTrendTextColor(SmwgActivity.GREEN_COLOR);
                            }
                        }else{
                            smwg.setTrendTextColor(SmwgActivity.BLUE_COLOR);
                        }

                        smwg.setKriteria(obj.getString("kriteria"));
                        smwg.setAvgVal(decimalFormat.format(obj.getDouble("avgVal")));
                        smwg.setTrend(trend);
                        smwg.setAvgRank(decimalFormat.format(obj.getDouble("avgRank")));

                        SmwgActivity.this.smwgList.add(smwg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
                updateSelectedMonthYearLabel();

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
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s",
                        SmwgActivity.this.username, SmwgActivity.this.password).getBytes(),
                        Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);

    }

    private void updateSelectedMonthYearLabel(){
        monthSelectLabel.setText(DashboardConstant.MONTHS[this.selectedMonth] + ", " + this.selectedYear);
    }

    public class SmwgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public SmwgAdapter() {
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.updateUI(SmwgActivity.this.smwgList.get(position));
        }

        @Override
        public int getItemCount() {
            return SmwgActivity.this.smwgList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.smwg_item, parent, false);
            return new ItemViewHolder(card);

        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView label1;
        private TextView label2;
        private TextView label3;
        private TextView label4;

        public ItemViewHolder(View itemView) {
            super(itemView);
            label1 = (TextView)itemView.findViewById(R.id.smwg_label_1);
            label2 = (TextView)itemView.findViewById(R.id.smwg_label_2);
            label3 = (TextView)itemView.findViewById(R.id.smwg_label_3);
            label4 = (TextView)itemView.findViewById(R.id.smwg_label_4);
        }

        public void updateUI(Smwg smwg) {
            label1.setText(smwg.getKriteria());
            label2.setText(smwg.getAvgVal());
            label3.setText(smwg.getTrend());
            label4.setText(smwg.getAvgRank());

            label3.setTextColor(Color.parseColor(smwg.getTrendTextColor()));
        }
    }


    private void makeDatePickerDialog() {
        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        DatePickerDialog.OnDateSetListener dateOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            // Prevent called twice
            if (view.isShown()) {
                SmwgActivity.this.selectedMonth = selectedMonth;
                SmwgActivity.this.selectedYear = selectedYear;
                SmwgActivity.this.updateSelectedMonthYearLabel();
                SmwgActivity.this.getSmwgData();
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
}
