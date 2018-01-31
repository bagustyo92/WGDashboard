package com.wikagedung.myyusuf.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wikagedung.myyusuf.myapplication.model.CommentModel;
import com.wikagedung.myyusuf.myapplication.utils.LoadImageTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllCommentAvtivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private AllCommentAdapter adapter;

    TextView monthSelectLabel;

    private String username;
    private String password;
    private int selectedMonth;
    private int selectedYear;
    private String locationTitle;
    private String currentPage;

    private String selectedLoc = "";

    private ArrayList<CommentModel> commentList = new ArrayList<>();

    TextView statusComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 2. Made filter by location integrated with backend
        // TODO 3. Made spinner for filtering
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comment);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_all_comment);
        setSupportActionBar(toolbar);
        TextView toolBarTitle2 = (TextView) findViewById(R.id.tool_bar_title1);
        toolBarTitle2.setText("Komentar");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.android_back_button);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        //Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        selectedMonth = intent.getIntExtra("selectedMonth", 1);
        selectedYear = intent.getIntExtra("selectedYear", 2016);


        //GetData
        getAllCommentData();

        //Update Date
        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);
        updateSelectedMonthYearLabel();

        //Get Date Selector
        makeDatePickerDialog();

        //Spinner
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner_location);
        spinner.setItems("Semua Komentar", "Kontrak Dihadapi", "Penjualan", "Laba Kotor", "PPH Final", "Laba Kotor stlh PPH Final", "Biaya Usaha", "Laba Usaha & LSP",
                    "Proyek", "Produk & Proses", "Pelanggan", "Keuangan & Pasar", "Tenaga Kerja", "Kepemimpinan & Tata Kelola", "QMSL", "SHE LEVEL", "5R");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                Snackbar.make(view, item + " " + " | pos : " + position + " " + (selectedMonth+1) + " " + selectedYear, Snackbar.LENGTH_LONG).show();
//                Intent intent = new Intent(AllCommentAvtivity.this, AllCommentAvtivity.class);
                selectedLoc = setCommentFilter(position);
                getAllCommentData();
                adapter = new AllCommentAdapter();
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.invalidate();
            }
        });

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.all_comment_recycler_view);
        adapter = new AllCommentAdapter();
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(false);
        mRecyclerView.setLayoutManager(layoutManager);

        //test
        //No Comment
        statusComment = (TextView) findViewById(R.id.statusAllComment);

    }

    public String setCommentFilter(int pos){
        //Rename The Location
        String location = "";
        switch (pos) {
            case 0 :
                location = "";
                break;
            case 1 :
                location = "/0";
                break;
            case 2 :
                location = "/1";
                break;
            case 3 :
                location = "/2";
                break;
            case 4 :
                location = "/3";
                break;
            case 5 :
                location = "/4";
                break;
            case 6 :
                location = "/5";
                break;
            case 7 :
                location = "/6";
                break;
            case 8 :
                location = "/projectDetail";
                statusComment.setText("ANDA TIDAK MEMILIKI AKSES");
                statusComment.setVisibility(View.VISIBLE);
                break;
            case 9 :
                location = "/8";
                break;
            case 10 :
                location = "/9";
                break;
            case 11 :
                location = "/10";
                break;
            case 12 :
                location = "/11";
                break;
            case 13 :
                location = "/12";
                break;
            case 14 :
                location = "/QMSL";
                break;
            case 15 :
                location = "/SHE_LEVEL";
                break;
            case 16 :
                location = "/5R";
                break;
        }
        return location;
    }


    public class AllCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public AllCommentAdapter(){

        }

        public int getItemViewType(int position){ return 0;}

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.updateUI(AllCommentAvtivity.this.commentList.get(position));

        }

        @Override
        public int getItemCount() {
            return AllCommentAvtivity.this.commentList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            return new ItemViewHolder(card);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView employeeImage;
        private TextView employeeName;
        private TextView commentText;
        private TextView commentHour;
        private TextView commentDate;
        private TextView commentLocationTitle;
        private ImageView ivCommentLocation;
        private ImageButton ibLinkComment;

//        Intent intent;

        public ItemViewHolder(View itemView) {
            super(itemView);

            employeeImage = (ImageView) itemView.findViewById(R.id.comment_image);
            employeeName = (TextView) itemView.findViewById(R.id.coment_employee_name);
            commentText = (TextView) itemView.findViewById(R.id.comment_text);
            commentHour = (TextView) itemView.findViewById(R.id.hour_comment);
            commentDate = (TextView) itemView.findViewById(R.id.date_comment);
            commentLocationTitle = (TextView) itemView.findViewById(R.id.location);
            ivCommentLocation = (ImageView) itemView.findViewById(R.id.comment_location);
        }

        public void updateUI(CommentModel comment) {
            String imgURL = comment.getCommentImage();
            new LoadImageTask(employeeImage).execute(imgURL);
//            employeeImage.setImageURI(Uri.parse(comment.getCommentImage()));
            employeeName.setText(WordUtils.capitalizeFully(comment.getName()));
            commentText.setText(comment.getComment());
            commentHour.setText(comment.getCommentHour());
            commentDate.setText(comment.getCommentDate());
            ivCommentLocation.setVisibility(View.VISIBLE);

            currentPage = (comment.getLocation());
//            commentLocationTitle.setText(currentPage);
            if (!StringUtils.isNumeric(currentPage)){
                if (currentPage.length()>9){
                    commentLocationTitle.setText("PROYEK");
                } else {
                    commentLocationTitle.setText(currentPage);
                }
            } else {
                locationTitle = renameLocation(currentPage);
                commentLocationTitle.setText(locationTitle);
            }

            //TODO 1. Benerin pindah activity dari allComment ke Comment Activity
//            ibLinkComment = (ImageButton) itemView.findViewById(R.id.link_comment_button);
//            ibLinkComment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(AllCommentAvtivity.this, CommentActivity.class);
//                    intent.putExtra("username", username);
//                    intent.putExtra("password", password);
//                    intent.putExtra("selectedMonth", selectedMonth);
//                    intent.putExtra("selectedYear", selectedYear);
//                    intent.putExtra("commentTitle", currentPage);
//                    intent.putExtra("page", currentPage);
//                    startActivity(intent);
////
////                    //TODO 1. Pindahin rename toolbar ke commentactivity
//                }
//            });
        }

        public String renameLocation(String page){
            //Rename The Location
            String location = "";
            switch (page) {
                case "0" :
                    location = "Kontrak Dihadapi";
                    break;
                case "1" :
                    location = "Penjualan";
                    break;
                case "2" :
                    location = "Laba Kotor";
                    break;
                case "3" :
                    location = "PPH Final";
                    break;
                case "4" :
                    location = "Laba Kotor stlh PPH Final";
                    break;
                case "5" :
                    location = "Biaya Usaha";
                    break;
                case "6" :
                    location = "Laba Usaha & LSP";
                    break;
                case "8" :
                    location = "Produk & Proses";
                    break;
                case "9" :
                    location = "Pelanggan";
                    break;
                case "10" :
                    location = "Keuangan & Pasar";
                    break;
                case "11" :
                    location = "Tenaga Kerja";
                    break;
                case "12" :
                    location = "Kepemimpinan & Tata Kelola";
                    break;
            }
            return location;
        }
    }

    private void getAllCommentData() {
        String baseURL = DashboardConstant.BASE_URL + "comment/" + (selectedMonth+1) + "/" + selectedYear + selectedLoc;

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                AllCommentAvtivity.this.commentList.clear();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);

                        CommentModel comment = new CommentModel();

                        comment.setName(obj.getString("name"));
                        comment.setComment(obj.getString("comment"));
                        comment.setCommentHour(obj.getString("hour"));
                        comment.setCommentDate(obj.getString("date"));
                        comment.setCommentImage(obj.getString("photo_link"));
                        comment.setLocation(obj.getString("location"));
                        comment.setMonth_loc(obj.getString("month_loc"));
                        comment.setYear_loc(obj.getString("year_loc"));

                        AllCommentAvtivity.this.commentList.add(comment);
                        statusComment.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, baseURL, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", AllCommentAvtivity.this.username, AllCommentAvtivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
    }

    //===============================Date Picker===================================
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
                    AllCommentAvtivity.this.selectedMonth = selectedMonth;
                    AllCommentAvtivity.this.selectedYear = selectedYear;
                    AllCommentAvtivity.this.updateSelectedMonthYearLabel();
                    AllCommentAvtivity.this.getAllCommentData();
                    adapter = new AllCommentAdapter();
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.invalidate();
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
