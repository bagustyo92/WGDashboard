package com.wikagedung.myyusuf.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wikagedung.myyusuf.myapplication.model.EmployeeBirthday;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BirthdayActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private EmployeeAdapter adapter;

    TextView monthSelectLabel;
    private String username;
    private String password;

    private int selectedMonth = 1;
    private int selectedYear = 2016;

    private ArrayList<EmployeeBirthday> birthdayList = new ArrayList<>();

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_birthday);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        TextView toolbarTitle1 = (TextView) findViewById(R.id.tool_bar_title1);
        toolbarTitle1.setText("Birthday");

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

        mRecyclerView = (RecyclerView) findViewById(R.id.birthday_recycler_view);
        adapter = new EmployeeAdapter();
        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        updateSelectedMonthYearLabel();
        makeDatePickerDialog();

        getEmployeeBirthdayData();
    }

    public class EmployeeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public EmployeeAdapter() {
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.updateUI(BirthdayActivity.this.birthdayList.get(position));
        }

        @Override
        public int getItemCount() {
            return BirthdayActivity.this.birthdayList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.birthday_item, parent, false);

            return new ItemViewHolder(card);

        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView employeeImage;
        private TextView employeeNameLabel;
        private TextView employeeBirthdateLabel;
        private TextView employeeWorkunitLabel;

        public ItemViewHolder(View itemView) {
            super(itemView);

            employeeImage = (ImageView)itemView.findViewById(R.id.employee_circle_image);
            employeeNameLabel = (TextView)itemView.findViewById(R.id.employee_name_label);
            employeeBirthdateLabel = (TextView)itemView.findViewById(R.id.employee_birthdate_label);
            employeeWorkunitLabel = (TextView)itemView.findViewById(R.id.employee_workunit_label);

        }

        public void updateUI(EmployeeBirthday birthday) {

//            String imgURL = DashboardConstant.BASE_URL + "project-image/PROJECT01/icon_oval";
//            new LoadImageTask(employeeImage).execute(imgURL);
            employeeNameLabel.setText(birthday.getName());
            employeeBirthdateLabel.setText(birthday.getBirthDate());
            employeeWorkunitLabel.setText(birthday.getWorkUnit());
        }
    }

    private void getEmployeeBirthdayData(){

        String baseURL = DashboardConstant.BASE_URL + "employeedata/birthday/" + (selectedMonth + 1);

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {

                BirthdayActivity.this.birthdayList.clear();

                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        EmployeeBirthday birthday = new EmployeeBirthday();

                        String dateString = obj.getString("tanggal") + "/" + obj.getString("bulan") +
                                "/" + obj.getString("tahun");

                        String resultDateString = "";

                        try {
                            Date birthDate = sdf.parse(dateString);
                            resultDateString = sdf2.format(birthDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        birthday.setName(obj.getString("nama"));
                        birthday.setBirthDate(resultDateString);
                        birthday.setWorkUnit(obj.getString("unit_kerja"));

                        BirthdayActivity.this.birthdayList.add(birthday);
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
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, baseURL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", BirthdayActivity.this.username, BirthdayActivity.this.password).getBytes(), Base64.NO_WRAP);
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

    private void makeDatePickerDialog() {
        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        DatePickerDialog.OnDateSetListener dateOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                datePick.setText(new StringBuilder().append(selectedDay).append("-").append(selectedMonth).append("-").append(selectedYear));

                // Prevent called twice
                if (view.isShown()) {
                    BirthdayActivity.this.selectedMonth = selectedMonth;
                    BirthdayActivity.this.selectedYear = selectedYear;
                    BirthdayActivity.this.updateSelectedMonthYearLabel();
                    BirthdayActivity.this.getEmployeeBirthdayData();
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
