package com.wikagedung.myyusuf.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wikagedung.myyusuf.myapplication.model.Bast;
import com.wikagedung.myyusuf.myapplication.model.Project;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDetailActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private BastAdapter bastAdapter;
    private List<Bast> bastList = new ArrayList<>();

    TextView monthSelectLabel;
    private String username;
    private String password;

    private int selectedMonth = 1;
    private int selectedYear = 2016;
    Project project;

    TextView projectNameTextView;
    TextView projectAddressTextView;
    TextView okTextView;
    TextView projectStartDateTextView;
    TextView projectEndDateTextView;
    TextView projectManagerTextView;
    TextView kasieKeuTextView;
    TextView kasieKomTextView;
    TextView pelutTextView;
    TextView kasieEnjineringTextView;

    TextView raProgressPercentageTextView;
    TextView raProgressTextView;
    TextView riProgressPercentageTextView;
    TextView riProgressTextView;

    TextView piutangRetensiTextView;
    TextView tagihanBrutoTextView;

    TextView piutangUsahaTextView;
    TextView piutangUsahaTextView2;
    TextView piutangUsahaTextView3;
    TextView piutangUsahaTextView4;
    TextView piutangUsahaTextView5;

    TextView labaKotorRaTextView;
    TextView labaKotorRiTextView;
    TextView labaKotorStTextView;

    TextView persediaanTextView;
    TextView cashFlowTextView;

    WebView scoreCardWebView;

    TextView nilaiRisikoEkstrimTextView;

    TextView qmslTextView;
    TextView sheLevelTextView;
    TextView limaRTextView;

    DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        TextView toolbarTitle1 = (TextView) findViewById(R.id.tool_bar_title1);
        toolbarTitle1.setText("Detail");

        //IMPORTANT!!!
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NestedScrollView scrollView = (NestedScrollView)findViewById(R.id.project_detail_scroll_view);
        scrollView.setVerticalScrollBarEnabled(false);

//        OverScrollDecoratorHelper.setUpOverScroll(scrollView);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        selectedMonth = intent.getIntExtra("selectedMonth", 1);
        selectedYear = intent.getIntExtra("selectedYear", 2016);
        project = (Project) intent.getSerializableExtra("selectedProject");

        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.android_back_button);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        updateSelectedMonthYearLabel();
        makeDatePickerDialog();

        makeBastRecyclerView();

        initProjectDetailView();

        fillProjectData(project);

        final FloatingActionButton buttonComment = (FloatingActionButton) findViewById(R.id.pd_float_comment_button);

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(ProjectDetailActivity.this, CommentActivity.class);
                intent.putExtra("username", ProjectDetailActivity.this.username);
                intent.putExtra("password", ProjectDetailActivity.this.password);
                intent.putExtra("page", project.getName());
                intent.putExtra("selectedMonth", selectedMonth);
                intent.putExtra("selectedYear", selectedYear);
                intent.putExtra("idProyek", project.getIdProyek());
                startActivity(intent);
//                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coor_layout),
//                        "COMING SOON !", Snackbar.LENGTH_LONG);
//                mySnackbar.show();
            }
        });
    }

    private void initProjectDetailView() {
        projectNameTextView = (TextView) findViewById(R.id.project_name_text_view);
        projectAddressTextView = (TextView) findViewById(R.id.project_address_text_view);
        okTextView = (TextView) findViewById(R.id.ok_text_view);
        projectStartDateTextView = (TextView) findViewById(R.id.project_start_date_text_view);
        projectEndDateTextView = (TextView) findViewById(R.id.project_end_date_text_view);
        projectManagerTextView = (TextView) findViewById(R.id.project_manager_text_view);
        kasieKeuTextView = (TextView) findViewById(R.id.kasie_keu_text_view);
        kasieKomTextView = (TextView) findViewById(R.id.kasie_kom_text_view);
        pelutTextView = (TextView) findViewById(R.id.pelut_text_view);
        kasieEnjineringTextView = (TextView) findViewById(R.id.kasie_enjinering_text_view);

        raProgressPercentageTextView = (TextView) findViewById(R.id.ra_progress_percentage_text_view);
        raProgressTextView = (TextView) findViewById(R.id.ra_progress_text_view);
        riProgressPercentageTextView = (TextView) findViewById(R.id.ri_progress_percentage_text_view);
        riProgressTextView = (TextView) findViewById(R.id.ri_progress_text_view);

        piutangRetensiTextView = (TextView) findViewById(R.id.piutang_retensi_text_view);
        tagihanBrutoTextView = (TextView) findViewById(R.id.tagihan_bruto_text_view);

        piutangUsahaTextView = (TextView) findViewById(R.id.piutang_usaha_text_view);
        piutangUsahaTextView2 = (TextView) findViewById(R.id.piutang_usaha_text_view_2);
        piutangUsahaTextView3 = (TextView) findViewById(R.id.piutang_usaha_text_view_3);
        piutangUsahaTextView4 = (TextView) findViewById(R.id.piutang_usaha_text_view_4);
        piutangUsahaTextView5 = (TextView) findViewById(R.id.piutang_usaha_text_view_5);

        labaKotorRaTextView = (TextView) findViewById(R.id.laba_kotor_ra_text_view);
        labaKotorRiTextView = (TextView) findViewById(R.id.laba_kotor_ri_text_view);
        labaKotorStTextView = (TextView) findViewById(R.id.laba_kotor_st_text_view);

        persediaanTextView = (TextView) findViewById(R.id.persediaan_text_view);
        cashFlowTextView = (TextView) findViewById(R.id.cash_flow_text_view);

        scoreCardWebView = (WebView) findViewById(R.id.score_card_web_view);

        nilaiRisikoEkstrimTextView = (TextView) findViewById(R.id.nilai_risiko_ekstrim_text_view);

        qmslTextView = (TextView) findViewById(R.id.qmsl_text_view);
        sheLevelTextView = (TextView) findViewById(R.id.sil_text_view);
        limaRTextView = (TextView) findViewById(R.id.lima_r_text_view);

        String tag =
                "<style>" +
//                        "td:first-child {\n" +
//                        "    position: fixed;\n" +
//                        "    left: 0px;\n" +
//                        "}" +
                        "td{" +
                        "text-align: center;" +
                        "}" +
                        "</style>" +

                        "<table border=1 style='width: 650px; margin-left: 0px; border-collapse: collapse'>" +
                        "<tr style='background-color: #2996CC; color: white; height: 20px;'>" +
                        "<td style='width: 120px; padding-top: 10px;'>KPI</td>" +
                        "<td style='width: 120px;'>Mutu (%)</td>" +
                        "<td style='width: 120px;'>Cust Engangement</td>" +
                        "<td style='width: 120px;'>Response Rate (%)</td>" +
                        "<td style='width: 120px;'>Laba Kotor (Juta Rp.)</td>" +
                        "<td style='width: 120px;'>Net Cash Flow (%)</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td style='width: 120px; background-color: white;'>Target KKP</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td style='width: 120px; background-color: white;'>Ra</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "</tr>" +

                        "<tr>" +
                        "<td style='width: 120px; background-color: white;'>Ri</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "</tr>" +

                        "<tr>" +
                        "<td style='width: 120px; background-color: white;'>Nilai</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "</tr>" +

                        "<tr>" +
                        "<td style='width: 120px; background-color: white;'>Bobot KPI</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "</tr>" +

                        "<tr>" +
                        "<td style='width: 120px; background-color: white;'>Score Ra</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "</tr>" +

                        "<tr>" +
                        "<td style='width: 120px; background-color: white;'>Score Ri</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "<td>-</td>" +
                        "</tr>" +
                        "</table>";

        scoreCardWebView.setHorizontalScrollBarEnabled(true);
        scoreCardWebView.loadData(tag, "text/html", "utf-8");
    }

    private void makeBastRecyclerView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.bast_recycler_view);

        bastAdapter = new BastAdapter();
        mRecyclerView.setAdapter(bastAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void fillProjectData(Project project){

        projectNameTextView.setText(project.getName());
        projectAddressTextView.setText(project.getAddress());
        okTextView.setText(project.getOk());
        projectStartDateTextView.setText(project.getTglMulaiProyek());
        projectEndDateTextView.setText(project.getTglSelesaiProyek());

        projectManagerTextView.setText(project.getManajerProyek());
        kasieKeuTextView.setText(project.getKasieKeuangan());
        kasieKomTextView.setText(project.getKasieKomersial());
        pelutTextView.setText(project.getPelut());
        kasieEnjineringTextView.setText(project.getKasieEnjinering());

        raProgressPercentageTextView.setText("Ra = " + project.getRaProgressPercentage() + "%");
        raProgressTextView.setText(project.getRaProgress());
        riProgressPercentageTextView.setText("Ri = " + project.getRiProgressPercentage() + "%");
        riProgressTextView.setText(project.getRiProgress());

        piutangRetensiTextView.setText(project.getPiutangRetensi());
        tagihanBrutoTextView.setText(project.getTagihanBruto());

        piutangUsahaTextView.setText(project.getPiutangUsaha());

        piutangUsahaTextView2.setText(project.getRpKurangDari30());
        piutangUsahaTextView3.setText(project.getRp31Sd90());
        piutangUsahaTextView4.setText(project.getRpLebihDari90());
        piutangUsahaTextView5.setText(project.getSalahBuku());

        labaKotorRaTextView.setText(project.getLabaKotorRa());
        labaKotorRiTextView.setText(project.getLabaKotorRi());
        labaKotorStTextView.setText(project.getLabaKotorSt());

        persediaanTextView.setText(project.getPersediaan());
        cashFlowTextView.setText(project.getCashFlow());

        nilaiRisikoEkstrimTextView.setText(project.getNilaiRisikoEkstrim());

        qmslTextView.setText(project.getQmsl());
        sheLevelTextView.setText(project.getSheLevel());
        limaRTextView.setText(project.getLimaR());


        ProjectDetailActivity.this.bastList = project.getBastList();

        ProjectDetailActivity.this.bastAdapter.notifyDataSetChanged();

    }

    public class BastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public BastAdapter() {
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.updateUI(ProjectDetailActivity.this.bastList.get(position));
        }

        @Override
        public int getItemCount() {
            return ProjectDetailActivity.this.bastList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.bast_item, parent, false);
            return new ItemViewHolder(card);

        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView label1;
        private TextView label2;

        public ItemViewHolder(View itemView) {
            super(itemView);

            label1 = (TextView)itemView.findViewById(R.id.bast_item_label_1);
            label2 = (TextView)itemView.findViewById(R.id.bast_item_label_2);

        }

        public void updateUI(Bast bast) {

            label1.setText(bast.getName());
            label2.setText(bast.getBastDate());
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
//                datePick.setText(new StringBuilder().append(selectedDay).append("-").append(selectedMonth).append("-").append(selectedYear));

                // Prevent called twice
                if (view.isShown()) {
                    ProjectDetailActivity.this.selectedMonth = selectedMonth;
                    ProjectDetailActivity.this.selectedYear = selectedYear;
                    ProjectDetailActivity.this.updateSelectedMonthYearLabel();
                    ProjectDetailActivity.this.getProjectDetailData();
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

    private void getProjectDetailData() {

        String baseURL = DashboardConstant.BASE_URL + "drilldowndata/project-info-dd-details/" + project.getIdProyek() + "/" + selectedYear + "/" + (selectedMonth + 1);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject obj = response;
                    Project project = new Project();

                    project.setIdProyek(obj.getString("idProyek"));
                    project.setName(obj.getString("namaProyek"));
                    project.setDivision(obj.getString("divisi"));
                    project.setAddress(obj.getString("alamatProyek"));
                    project.setTglMulaiProyek(obj.getString("tglMulaiProyek"));
                    project.setTglSelesaiProyek(obj.getString("tglSelesaiProyek"));
                    project.setNetProfitRa(decimalFormat.format(obj.getJSONObject("rpLabaBersih").getDouble("ra")));
                    project.setNetProfitRi(decimalFormat.format(obj.getJSONObject("rpLabaBersih").getDouble("ri")));

                    project.setDeviasi(decimalFormat.format(obj.getDouble("rpDeviasi")));
                    project.setPdp(decimalFormat.format(obj.getDouble("pdp")));

                    project.setBad(decimalFormat.format(obj.getDouble("bad")));
                    project.setOk(decimalFormat.format(obj.getDouble("rpOk")));

                    project.setRaProgress(decimalFormat.format(obj.getDouble("rpRaProgress")));
                    project.setRaProgressPercentage(decimalFormat.format(obj.getDouble("persenRaProgress")));
                    project.setRiProgress(decimalFormat.format(obj.getDouble("rpRiProgress")));
                    project.setRiProgressPercentage(decimalFormat.format(obj.getDouble("persenRiProgress")));
                    project.setPersenRiThdRaProgress(decimalFormat.format(obj.getDouble("persenRiThdRaProgress")));

                    project.setPiutangUsaha(decimalFormat.format(obj.getJSONObject("piutangUsaha").getDouble("rpPiutangUsaha")));
                    project.setPiutangRetensi(decimalFormat.format(obj.getDouble("piutangRetensi")));
                    project.setTagihanBruto(decimalFormat.format(obj.getDouble("tagihanBrutto")));
                    project.setPersediaan(decimalFormat.format(obj.getDouble("persediaan")));
                    project.setCashFlow(decimalFormat.format(obj.getDouble("cashFlow")));

                    project.setBastList(new ArrayList<Bast>());

                    JSONArray bastArray = obj.getJSONArray("bast");
                    for(int x=0; x<bastArray.length(); x++){
                        try {
                            JSONObject bastJSON = bastArray.getJSONObject(x);
                            Bast bast = new Bast();
                            bast.setName(bastJSON.getString("nama"));
                            bast.setBastDate(bastJSON.getString("tgl"));
                            project.getBastList().add(bast);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    JSONObject teamProyek = obj.getJSONObject("timProyek");

                    project.setManajerProyek(teamProyek.getString("manajerProyek"));
                    project.setKasieKeuangan(teamProyek.getString("kasieKeuangan"));
                    project.setKasieKomersial(teamProyek.getString("kasieKomersial"));
                    project.setPelut(teamProyek.getString("pelut"));
                    project.setKasieEnjinering(teamProyek.getString("kasieEnjinering"));

                    JSONObject piutangUsaha = obj.getJSONObject("piutangUsaha");
                    project.setRpKurangDari30(decimalFormat.format(piutangUsaha.getDouble("rpKurangDari30")));
                    project.setRp31Sd90(decimalFormat.format(piutangUsaha.getDouble("rp31Sd90")));
                    project.setRpLebihDari90(decimalFormat.format(piutangUsaha.getDouble("rpLebihDari90")));
                    project.setSalahBuku(decimalFormat.format(piutangUsaha.getDouble("salahBuku")));

                    JSONObject labaKotor = obj.getJSONObject("labaKotor");
                    project.setLabaKotorRa(decimalFormat.format(labaKotor.getDouble("ra")));
                    project.setLabaKotorRi(decimalFormat.format(labaKotor.getDouble("ri")));
                    project.setLabaKotorSt(decimalFormat.format(labaKotor.getDouble("st")));

                    project.setNilaiRisikoEkstrim(decimalFormat.format(obj.getDouble("nilaiRisikoEkstrim")));

                    project.setQmsl(decimalFormat.format(obj.getDouble("qmsl")));
                    project.setSheLevel(decimalFormat.format(obj.getDouble("sheLevel")));
                    project.setLimaR(decimalFormat.format(obj.getDouble("limaR")));

                    ProjectDetailActivity.this.fillProjectData(project);

                } catch (JSONException e) {
                    Project project = new Project();
                    project.setIdProyek(ProjectDetailActivity.this.project.getIdProyek());
                    project.setName(ProjectDetailActivity.this.project.getName());
                    project.setDivision(ProjectDetailActivity.this.project.getDivision());
                    project.setAddress(ProjectDetailActivity.this.project.getAddress());
                    project.setTglMulaiProyek(ProjectDetailActivity.this.project.getTglMulaiProyek());
                    project.setTglSelesaiProyek(ProjectDetailActivity.this.project.getTglSelesaiProyek());
                    project.setNetProfitRa("-");
                    project.setNetProfitRi("-");

                    project.setDeviasi("-");
                    project.setPdp("-");

                    project.setBad("-");
                    project.setOk("-");

                    project.setRaProgress("-");
                    project.setRaProgressPercentage("-");
                    project.setRiProgress("-");
                    project.setRiProgressPercentage("-");
                    project.setPersenRiThdRaProgress("-");

                    project.setPiutangUsaha("-");
                    project.setPiutangRetensi("-");
                    project.setTagihanBruto("-");
                    project.setPersediaan("-");
                    project.setCashFlow("-");

                    project.setBastList(new ArrayList<Bast>());

                    project.setManajerProyek(ProjectDetailActivity.this.project.getManajerProyek());
                    project.setKasieKeuangan(ProjectDetailActivity.this.project.getKasieKeuangan());
                    project.setKasieKomersial(ProjectDetailActivity.this.project.getKasieKomersial());
                    project.setPelut(ProjectDetailActivity.this.project.getPelut());
                    project.setKasieEnjinering(ProjectDetailActivity.this.project.getKasieEnjinering());

                    project.setRpKurangDari30("-");
                    project.setRp31Sd90("-");
                    project.setRpLebihDari90("-");
                    project.setSalahBuku("-");

                    project.setLabaKotorRa("-");
                    project.setLabaKotorRi("-");
                    project.setLabaKotorSt("-");

                    project.setNilaiRisikoEkstrim("-");

                    project.setQmsl("-");
                    project.setSheLevel("-");
                    project.setLimaR("-");

                    ProjectDetailActivity.this.fillProjectData(project);
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
                Log.v("KAT", "MyAuth: authenticating");
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", ProjectDetailActivity.this.username, ProjectDetailActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
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
