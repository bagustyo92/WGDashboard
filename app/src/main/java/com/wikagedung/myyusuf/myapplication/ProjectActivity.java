package com.wikagedung.myyusuf.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wikagedung.myyusuf.myapplication.model.Bast;
import com.wikagedung.myyusuf.myapplication.model.Project;
import com.wikagedung.myyusuf.myapplication.utils.LoadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ProjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "ProjectActivity";

    RecyclerView mRecyclerView;
    private ProjectAdapter adapter;

    TextView monthSelectLabel;
    private String username;
    private String password;

    private int selectedMonth = 1;
    private int selectedYear = 2016;
    private int selectedFilter = 0;
    private int selectedSort = 0;

    private ArrayList<Project> originalProjectList = new ArrayList<>();
    private ArrayList<Project> projectList = new ArrayList<>();

    DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
    private static final String RED_COLOR = "#FF2B2B";
    private static final String GREEN_COLOR = "#70AA2D";
    private static final String BLUE_COLOR = "#3232FF";

    private String[] filterWords = {"All", "Divisi Operasi 1", "Divisi Operasi 2", "Divisi Operasi 3", "Divisi Property"};
    private boolean isSortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        TextView toolbarTitle1 = (TextView) findViewById(R.id.tool_bar_title1);
        toolbarTitle1.setText("Project");

        //IMPORTANT!!!
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        selectedMonth = intent.getIntExtra("selectedMonth", 1);
        selectedYear = intent.getIntExtra("selectedYear", 2016);
        selectedFilter = intent.getIntExtra("selectedFilter", 0);
        selectedSort = intent.getIntExtra("selectedSort", 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.project_recycler_view);

        adapter = new ProjectAdapter();
        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.android_back_button);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        updateSelectedMonthYearLabel();
        makeSpinner();
        makeDatePickerDialog();

        getProjectData();


    }

    private void makeSpinner() {
        Spinner spinner1 = (Spinner) findViewById(R.id.project_filter_spinner);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.project_filter_array,
                R.layout.project_spinner_item
//                android.R.layout.simple_spinner_item
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter1);

        Spinner spinner2 = (Spinner) findViewById(R.id.project_sort_spinner);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.project_sort_array,
                R.layout.project_spinner_item
//                android.R.layout.simple_spinner_item
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(adapter2);

        spinner1.setSelection(selectedFilter);
        spinner2.setSelection(selectedSort);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);

        final Button sortButton = (Button) findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectActivity.this.isSortAscending = !ProjectActivity.this.isSortAscending;
                if(ProjectActivity.this.isSortAscending){
                    sortButton.setBackground(getResources().getDrawable(R.drawable.sort_down));
                }else{
                    sortButton.setBackground(getResources().getDrawable(R.drawable.sort_up));
                }

                ProjectActivity.this.sortProjectList();
            }
        });

    }

    private void filterProjectList(){
        ArrayList<Project> filteredList = new ArrayList<>();

        if(this.selectedFilter != 0){
            for(Project project: this.originalProjectList){
                if(filterWords[this.selectedFilter].equalsIgnoreCase(project.getDivision())){
                    filteredList.add(project);
                }
            }
            this.projectList = filteredList;
        }else{
            this.projectList = this.originalProjectList;
        }

        ProjectActivity.this.sortProjectList();
    }

    private void sortProjectList(){
        Collections.sort(ProjectActivity.this.projectList, new Comparator<Project>() {
            @Override
            public int compare(Project p1, Project p2) {

                Double val1 = 0.0;
                Double val2 = 0.0;
                try {
                    if(ProjectActivity.this.selectedSort == 0){
                        val1 = decimalFormat.parse(p1.getPersenRiThdRaProgress()).doubleValue();
                        val2 = decimalFormat.parse(p2.getPersenRiThdRaProgress()).doubleValue();
                    }else if(ProjectActivity.this.selectedSort == 1){
                        val1 = decimalFormat.parse(p1.getNetProfitRi()).doubleValue();
                        val2 = decimalFormat.parse(p2.getNetProfitRi()).doubleValue();
                    }else if(ProjectActivity.this.selectedSort == 2){
                        val1 = decimalFormat.parse(p1.getPiutangUsaha()).doubleValue();
                        val2 = decimalFormat.parse(p2.getPiutangUsaha()).doubleValue();
                    }else if(ProjectActivity.this.selectedSort == 3){
                        val1 = decimalFormat.parse(p1.getPiutangRetensi()).doubleValue();
                        val2 = decimalFormat.parse(p2.getPiutangRetensi()).doubleValue();
                    }else if(ProjectActivity.this.selectedSort == 4){
                        val1 = decimalFormat.parse(p1.getTagihanBruto()).doubleValue();
                        val2 = decimalFormat.parse(p2.getTagihanBruto()).doubleValue();
                    }else if(ProjectActivity.this.selectedSort == 5){
                        val1 = decimalFormat.parse(p1.getPersediaan()).doubleValue();
                        val2 = decimalFormat.parse(p2.getPersediaan()).doubleValue();
                    }else if(ProjectActivity.this.selectedSort == 6){
                        val1 = decimalFormat.parse(p1.getCashFlow()).doubleValue();
                        val2 = decimalFormat.parse(p2.getCashFlow()).doubleValue();
                    }else if(ProjectActivity.this.selectedSort == 7){
                        val1 = decimalFormat.parse(p1.getPdp()).doubleValue();
                        val2 = decimalFormat.parse(p2.getPdp()).doubleValue();
                    }else{
                        return val1.compareTo(val2);
                    }

                    if(ProjectActivity.this.isSortAscending){
                        return val2.compareTo(val1);
                    }else{
                        return val1.compareTo(val2);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        ProjectActivity.this.adapter.notifyDataSetChanged();
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        Spinner spinner = (Spinner) parent;

        if(spinner.getId() == R.id.project_filter_spinner){
            ProjectActivity.this.selectedFilter = pos;
            ProjectActivity.this.filterProjectList();
        }else if(spinner.getId() == R.id.project_sort_spinner){
            Log.v("spinner", "sort: " + pos);

            ProjectActivity.this.selectedSort = pos;
            ProjectActivity.this.sortProjectList();
        }

//        Collections.sort(ProjectActivity.this.projectList, new Comparator<Project>() {
//            @Override
//            public int compare(Project p1, Project p22) {
//                return p1.get .compareToIgnoreCase(s2);
//            }
//        });

//        let filterWords = ["All", "Divisi 1", "Divisi 2", "Divisi 3", "Divisi Property"]
//        let filterWords2 = ["All", "Divisi Operasi 1", "Divisi Operasi 2", "Divisi Operasi 3", "Divisi Property"]

//        if(selectedFilter != 0){
//
//            filteredArray = self.projectList.filter() {$0.division!.containsString(self.filterWords2[selectedFilter])}
//        }

//        if self.selectedSortingType == 0{
//            if(selectedSort == 0){
//                sortedArray = filteredArray.sort({$0.persenRiThdRaProgress > $1.persenRiThdRaProgress})
//            }else if(selectedSort == 1){
//                sortedArray = filteredArray.sort({$0.netProfitRi > $1.netProfitRi})
//            }else if(selectedSort == 2){
//                sortedArray = filteredArray.sort({$0.piutangUsaha > $1.piutangUsaha})
//            }else if(selectedSort == 3){
//                sortedArray = filteredArray.sort({$0.piutangRetensi > $1.piutangRetensi})
//            }else if(selectedSort == 4){
//                sortedArray = filteredArray.sort({$0.tagihanBruto > $1.tagihanBruto})
//            }else if(selectedSort == 5){
//                sortedArray = filteredArray.sort({$0.persediaan > $1.persediaan})
//            }else if(selectedSort == 6){
//                sortedArray = filteredArray.sort({$0.cashFlow > $1.cashFlow})
//            }else if(selectedSort == 7){
//                sortedArray = filteredArray.sort({$0.pdp > $1.pdp})
//            }
//        }else{
//            if(selectedSort == 0){
//                sortedArray = filteredArray.sort({$0.persenRiThdRaProgress < $1.persenRiThdRaProgress})
//            }else if(selectedSort == 1){
//                sortedArray = filteredArray.sort({$0.netProfitRi < $1.netProfitRi})
//            }else if(selectedSort == 2){
//                sortedArray = filteredArray.sort({$0.piutangUsaha < $1.piutangUsaha})
//            }else if(selectedSort == 3){
//                sortedArray = filteredArray.sort({$0.piutangRetensi < $1.piutangRetensi})
//            }else if(selectedSort == 4){
//                sortedArray = filteredArray.sort({$0.tagihanBruto < $1.tagihanBruto})
//            }else if(selectedSort == 5){
//                sortedArray = filteredArray.sort({$0.persediaan < $1.persediaan})
//            }else if(selectedSort == 6){
//                sortedArray = filteredArray.sort({$0.cashFlow < $1.cashFlow})
//            }else if(selectedSort == 7){
//                sortedArray = filteredArray.sort({$0.pdp < $1.pdp})
//            }
//        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void updateSelectedMonthYearLabel(){
        monthSelectLabel.setText(DashboardConstant.MONTHS[this.selectedMonth] + ", " + this.selectedYear);
    }

    public class ProjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ProjectAdapter() {
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.updateUI(ProjectActivity.this.projectList.get(position));
        }

        @Override
        public int getItemCount() {
            return ProjectActivity.this.projectList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
            card.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    int itemPosition = mRecyclerView.getChildAdapterPosition(view);

                    Project selectedProject = projectList.get(itemPosition);

                    Intent intent = new Intent(ProjectActivity.this, ProjectDetailActivity.class);

                    intent.putExtra("username", ProjectActivity.this.username);
                    intent.putExtra("password", ProjectActivity.this.password);
                    intent.putExtra("selectedMonth", ProjectActivity.this.selectedMonth);
                    intent.putExtra("selectedYear", ProjectActivity.this.selectedYear);
                    intent.putExtra("selectedProject", selectedProject);
                    intent.putExtra("page", 7);

                    startActivity(intent);

                }
            });

            return new ItemViewHolder(card);

        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView projectImage;
        private TextView label1;
        private TextView label2;
        private TextView label3;
        private TextView label4;
        private TextView label5;
        private TextView label6;
        private TextView label7;
        private TextView label8;
        private TextView label9;
        private TextView label10;
        private TextView label11;
        private TextView label12;
        private TextView label13;
        private TextView label14;
        private TextView label15;
        private TextView label16;
        private TextView label17;
        private TextView label18;
        private TextView managerProyek;

        public ItemViewHolder(View itemView) {
            super(itemView);

            projectImage = (ImageView)itemView.findViewById(R.id.project_circle_image);
            label1 = (TextView)itemView.findViewById(R.id.project_label_1);
            label2 = (TextView)itemView.findViewById(R.id.project_label_2);
            label3 = (TextView)itemView.findViewById(R.id.project_label_3);
            label4 = (TextView)itemView.findViewById(R.id.project_label_4);
            label5 = (TextView)itemView.findViewById(R.id.project_label_5);
            label6 = (TextView)itemView.findViewById(R.id.project_label_6);
            label7 = (TextView)itemView.findViewById(R.id.project_label_7);
            label8 = (TextView)itemView.findViewById(R.id.project_label_8);
            label9 = (TextView)itemView.findViewById(R.id.project_label_9);
            label10 = (TextView)itemView.findViewById(R.id.project_label_10);
            label11 = (TextView)itemView.findViewById(R.id.project_label_11);
            label12 = (TextView)itemView.findViewById(R.id.project_label_12);
            label13 = (TextView)itemView.findViewById(R.id.project_label_13);

            label14 = (TextView)itemView.findViewById(R.id.project_label_14);
            label15 = (TextView)itemView.findViewById(R.id.project_label_15);

            label16 = (TextView)itemView.findViewById(R.id.project_label_16);
            label17 = (TextView)itemView.findViewById(R.id.project_label_17);
            label18 = (TextView)itemView.findViewById(R.id.project_label_18);

            managerProyek = (TextView)itemView.findViewById(R.id.manager_proyek_preview);

        }

        public void updateUI(Project project) {

            String imgURL = DashboardConstant.BASE_URL + "project-image/PROJECT01/icon_oval";
            new LoadImageTask(projectImage).execute(imgURL);
            label1.setText(project.getName());
            label2.setText(project.getDivision());
            label3.setText(project.getNetProfitRa());
            label4.setText(project.getNetProfitRi());
            label5.setText(project.getDeviasi());
            label6.setText(project.getPdp());
            label7.setText(project.getBad());
            label8.setText(project.getOk());
            label9.setText(project.getRaProgressPercentage() + "%");
            label10.setText(project.getRiProgressPercentage() + "%");
            label11.setText("(" + project.getPersenRiThdRaProgress() + "%)");
            label11.setTextColor(Color.parseColor(project.getRiProgressPercentageTextColor()));
            label12.setText(project.getRaProgress());
            label13.setText(project.getRiProgress());

            label14.setText(project.getPiutangUsaha());
            label15.setText(project.getPiutangRetensi());

            label16.setText(project.getTagihanBruto());
            label17.setText(project.getPersediaan());
            label18.setText(project.getCashFlow());
            managerProyek.setText(project.getManajerProyek());
        }
    }

    private void getProjectData(){

        String baseURL = DashboardConstant.BASE_URL + "drilldowndata/project-info-dd/" + selectedYear + "/" + (selectedMonth + 1);

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray response) {

                ProjectActivity.this.originalProjectList.clear();

//                let projectName = projectData["namaProyek"] as? String ?? ""
//                let idProyek = projectData["idProyek"] as? String ?? ""
//                let division = projectData["divisi"] as? String ?? ""
//                let projectAddress = projectData["alamatProyek"] as? String ?? ""
//                let tglMulaiProyek = projectData["tglMulaiProyek"] as? String ?? ""
//                let tglSelesaiProyek = projectData["tglSelesaiProyek"] as? String ?? ""
//                let photoList = projectData["lokasiFotoProyek"] as? NSArray ?? []
//                let bastList = projectData["bast"] as? NSArray ?? []
//
//                let wgProject = WgProject(name: projectName)
//                wgProject.idProyek = idProyek
//                wgProject.division = division
//                wgProject.address = projectAddress
//                wgProject.photoList = photoList
//                wgProject.bastList = bastList
//                wgProject.netProfitRa = projectData["rpLabaBersih"]??["ra"] as? Double
//                wgProject.netProfitRi = projectData["rpLabaBersih"]??["ri"] as? Double
//                wgProject.deviasi = projectData["rpDeviasi"] as? Double
//                wgProject.pdp = projectData["pdp"] as? Double
//                wgProject.bad = projectData["bad"] as? Double
//                wgProject.ok = projectData["rpOk"] as? Double
//                wgProject.raProgress = projectData["rpRaProgress"] as? Double
//                wgProject.raProgressPercentage = projectData["persenRaProgress"] as? Double
//                wgProject.riProgress = projectData["rpRiProgress"] as? Double
//                wgProject.riProgressPercentage = projectData["persenRiProgress"] as? Double
//                wgProject.persenRiThdRaProgress = projectData["persenRiThdRaProgress"] as? Double
//
//                wgProject.piutangUsaha = projectData["piutangUsaha"]??["rpPiutangUsaha"] as? Double
//                wgProject.piutangRetensi = projectData["piutangRetensi"] as? Double
//                wgProject.tagihanBruto = projectData["tagihanBrutto"] as? Double
//                wgProject.persediaan = projectData["persediaan"] as? Double
//                wgProject.cashFlow = projectData["cashFlow"] as? Double
//
//                wgProject.tglMulaiProyek = ProjectInfoDDService.dateFormatter.dateFromString(tglMulaiProyek)
//                wgProject.tglSelesaiProyek = ProjectInfoDDService.dateFormatter.dateFromString(tglSelesaiProyek)
//
//                wgProject.manajerProyek = projectData["timProyek"]??["manajerProyek"] as? String
//                wgProject.kasieKeuangan = projectData["timProyek"]??["kasieKeuangan"] as? String
//                wgProject.kasieKomersial = projectData["timProyek"]??["kasieKomersial"] as? String
//                wgProject.pelut = projectData["timProyek"]??["pelut"] as? String
//                wgProject.kasieEnjinering = projectData["timProyek"]??["kasieEnjinering"] as? String
//
//                wgProject.rpKurangDari30 = projectData["piutangUsaha"]??["rpKurangDari30"] as? Double
//
//                wgProject.rp31Sd90 = projectData["piutangUsaha"]??["rp31Sd90"] as? Double
//                wgProject.rpLebihDari90 = projectData["piutangUsaha"]??["rpLebihDari90"] as? Double
//                wgProject.salahBuku = projectData["piutangUsaha"]??["salahBuku"] as? Double
//
//                wgProject.labaKotorRa = projectData["labaKotor"]??["ra"] as? Double
//                wgProject.labaKotorRi = projectData["labaKotor"]??["ri"] as? Double
//                wgProject.labaKotorSt = projectData["labaKotor"]??["st"] as? Double
//
//                wgProject.nilaiRisikoEkstrim = projectData["nilaiRisikoEkstrim"] as? Double
//                wgProject.qmsl = projectData["qmsl"] as? Double
//                wgProject.sheLevel = projectData["sheLevel"] as? Double
//                wgProject.limaR = projectData["limaR"] as? Double

//                cell.raProgress.text = decimalFormatter.stringFromNumber(project.raProgress!)
//                cell.raProgressPercentage.text = decimalFormatter.stringFromNumber(project.raProgressPercentage!)! + "%"
//                cell.riProgress.text = decimalFormatter.stringFromNumber(project.riProgress!)
//                cell.riProgressPercentage.text = decimalFormatter.stringFromNumber(project.riProgressPercentage!)! + "%"
//                cell.riProgressPercentageInner.text = "(" + decimalFormatter.stringFromNumber(project.persenRiThdRaProgress!)! + "%)"
//
//                if(project.persenRiThdRaProgress > 0){
//                    cell.riProgressPercentageInner.textColor = UIColor(red:0.36, green:0.62, blue:0.06, alpha:1.0)
//                }else if(project.persenRiThdRaProgress < 0){
//                    cell.riProgressPercentageInner.textColor = UIColor.redColor()
//                }else{
//                    cell.riProgressPercentageInner.textColor = UIColor.blueColor()
//                }

                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
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

                        if(obj.getDouble("persenRiThdRaProgress") > 0){
                            project.setRiProgressPercentageTextColor(GREEN_COLOR);
                        }else if(obj.getDouble("persenRiThdRaProgress") < 0){
                            project.setRiProgressPercentageTextColor(RED_COLOR);
                        }else{
                            project.setRiProgressPercentageTextColor(BLUE_COLOR);
                        }

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

                        ProjectActivity.this.originalProjectList.add(project);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ProjectActivity.this.projectList = ProjectActivity.this.originalProjectList;

                ProjectActivity.this.sortProjectList();
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
                String encodedString = Base64.encodeToString(String.format("%s:%s", ProjectActivity.this.username, ProjectActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);

    }

    private void makeDatePickerDialog() {
        monthSelectLabel = (TextView) findViewById(R.id.month_select_label);

        DatePickerDialog.OnDateSetListener dateOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
//                datePick.setText(new StringBuilder().append(selectedDay).append("-").append(selectedMonth).append("-").append(selectedYear));

                // Prevent called twice
                if (view.isShown()) {
                    ProjectActivity.this.selectedMonth = selectedMonth;
                    ProjectActivity.this.selectedYear = selectedYear;
                    ProjectActivity.this.updateSelectedMonthYearLabel();
                    ProjectActivity.this.getProjectData();
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
