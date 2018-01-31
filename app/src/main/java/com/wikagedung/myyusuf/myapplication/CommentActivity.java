package com.wikagedung.myyusuf.myapplication;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.cenkgun.chatbar.ChatBarView;
import com.wikagedung.myyusuf.myapplication.model.CommentModel;
import com.wikagedung.myyusuf.myapplication.model.Post;
import com.wikagedung.myyusuf.myapplication.remote.APIService;
import com.wikagedung.myyusuf.myapplication.remote.ApiUtils;
import com.wikagedung.myyusuf.myapplication.utils.LoadImageTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//post comment
import retrofit2.Call;
import retrofit2.Callback;


public class CommentActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private CommentAdapter adapter;

    private String username;
    private String password;
    private int monthSelected;
    private int yearSelected;

    String currentPage;
    String page;
    String commentTitle;
    String idProyek;
    String validation = "";

    private ArrayList<CommentModel> commentList = new ArrayList<>();

    private APIService mAPIService;

    CoordinatorLayout coordinatorLayout;

    String text;
    ChatBarView chatBarView;
    TextView statusComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //POST COMMENT
        mAPIService = ApiUtils.getAPIService();

        Intent intent = getIntent();
        commentTitle = intent.getStringExtra("commentTitle");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        monthSelected = intent.getIntExtra("selectedMonth", 1) + 1;
        yearSelected = intent.getIntExtra("selectedYear", 2016);

        idProyek = intent.getStringExtra("idProyek");
        currentPage = intent.getStringExtra("page");
        page = currentPage;
//        if (StringUtils.isNumeric(currentPage)){
//            page = currentPage;
//        } else {
//            page = intent.getStringExtra("pageString");
//        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        //chatbar
        chatBarView = (ChatBarView) findViewById(R.id.chatbar);
        chatBarView.setMessageBoxHint("Write Comment");
        chatBarView.setSendClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO what you want..
                text = chatBarView.getMessageText();
                if(!text.trim().isEmpty()) {
                    String mSelect, ySelect;
                    mSelect = Integer.toString(monthSelected);
                    ySelect = Integer.toString(yearSelected);
                    sendPost(username, text, page.replaceAll("\\s+",""), mSelect, ySelect);
                    android.os.SystemClock.sleep(1000);
                    Toast.makeText(CommentActivity.this, "Komentar anda Berhasil Terkirim !", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(CommentActivity.this, CommentActivity.class);
//                    intent.putExtra("username", username);
//                    intent.putExtra("password", password);
//                    finish();
//                    startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    getCommentData();
                    adapter = new CommentAdapter();
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.invalidate();
                } else {
                    coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coor_layout_comment);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Komentar Tidak Boleh kosong !", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_comment);
        setSupportActionBar(toolbar);
        TextView toolBarTitle = (TextView) findViewById(R.id.tool_bar_title1);
        toolBarTitle.setText(setCommentTitle(currentPage));

        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        final Drawable upArrow = getResources().getDrawable(R.drawable.android_back_button);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        statusComment = (TextView) findViewById(R.id.statusComment);

        if (idProyek!=null){
            getCommentStatus(); //jsonObjext
        } else{
            getCommentData(); //jsonArray
        }
//        getCommentData();

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        adapter = new CommentAdapter();
        mRecyclerView.setAdapter(adapter);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        mRecyclerView.setLayoutManager(layoutManager);


    }

    private String setComment;
    public String setCommentTitle(String title){
        switch (title) {
            case "0":
                setComment = "Kontrak Dihadapi";
                break;
            case "1":
                setComment = "Penjualan";
                break;
            case "2":
                setComment = "Laba Kotor";
                break;
            case "3":
                setComment = "PPH Final";
                break;
            case "4":
                setComment = "Laba Kotor stlh PPH Final";
                break;
            case "5":
                setComment = "Biaya Usaha";
                break;
            case "6":
                setComment = "Laba Usaha & LSP";
                break;
            case "8":
                setComment = "Produk & Proses";
                break;
            case "9":
                setComment = "Pelanggan";
                break;
            case "10":
                setComment = "Keuangan & Pasar";
                break;
            case "11":
                setComment = "Tenaga Kerja";
                break;
            case "12":
                setComment = "Kepemimpinan & Tata Kelola";
                break;
            default:
                setComment = currentPage;
        }
        return setComment;
    }

    public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public CommentAdapter(){

        }

        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.updateUI(CommentActivity.this.commentList.get(position));

        }

        @Override
        public int getItemCount() { return CommentActivity.this.commentList.size();}

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

        public ItemViewHolder(View itemView) {
            super(itemView);

            employeeImage = (ImageView)itemView.findViewById(R.id.comment_image);
            employeeName = (TextView)itemView.findViewById(R.id.coment_employee_name);
            commentText = (TextView)itemView.findViewById(R.id.comment_text);
            commentHour = (TextView)itemView.findViewById(R.id.hour_comment);
            commentDate = (TextView) itemView.findViewById(R.id.date_comment);

        }

        public void updateUI(CommentModel comment){

            String imgURL = comment.getCommentImage();
            new LoadImageTask(employeeImage).execute(imgURL);
//            employeeImage.setImageURI(Uri.parse(comment.getCommentImage()));
            employeeName.setText(WordUtils.capitalizeFully(comment.getName()));
            commentText.setText(comment.getComment());
            commentHour.setText(comment.getCommentHour());
            commentDate.setText(comment.getCommentDate());
        }
    }

    private void getCommentStatus() {
        String baseURL = DashboardConstant.BASE_URL + "comment/" + monthSelected + "/" + yearSelected + "/" + page.replaceAll("\\s+","") + "/" + username + "/" + idProyek;

        Response.Listener<JSONObject> listener1 = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status == "false") {
                        statusComment.setText("ANDA TIDAK MEMILIKI AKSES\nPADA KOLOM INI");
                        chatBarView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener1 = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //statusComment.setText("No Internet Connection");
                getCommentData();
            }
        };
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, baseURL, listener1, errorListener1) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                String key = "Authorization";
                String encodedString = Base64.encodeToString(String.format("%s:%s", CommentActivity.this.username, CommentActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void getCommentData(){
//        if (idProyek != null){
//            validation = "/" + username + "/" + idProyek;
//        }
        String baseURL = DashboardConstant.BASE_URL + "comment/" + monthSelected + "/" + yearSelected + "/" + page.replaceAll("\\s+","");

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                CommentActivity.this.commentList.clear();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        CommentModel comment = new CommentModel();

                        comment.setName(obj.getString("name"));
                        comment.setComment(obj.getString("comment"));
                        comment.setCommentHour(obj.getString("hour"));
                        comment.setCommentDate(obj.getString("date"));
                        comment.setCommentImage(obj.getString("photo_link"));


                        CommentActivity.this.commentList.add(comment);
                        statusComment.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(commentList.size());
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
                String encodedString = Base64.encodeToString(String.format("%s:%s", CommentActivity.this.username, CommentActivity.this.password).getBytes(), Base64.NO_WRAP);
                String value = String.format("Basic %s", encodedString);
                map.put(key, value);

                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);

    }

    public void sendPost(String username, String commentContain, String location, String month, String year) {
        mAPIService.savePost(username, commentContain, location, month, year).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, retrofit2.Response<Post> response) {
//                if(response.isSuccessful()) {
//                    Toast.makeText(CommentActivity.this, "Posting Komentar anda Berhasil " + response.body().toString(), Toast.LENGTH_LONG).show();
//                    Log.i(TAG, "post submitted to API." + response.body().toString());
//                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
//                Toast.makeText(CommentActivity.this, "Posting Komentar anda Gagal, Silahkan Cek Internet Anda!", Toast.LENGTH_LONG).show();
            }
        });
    }


}
