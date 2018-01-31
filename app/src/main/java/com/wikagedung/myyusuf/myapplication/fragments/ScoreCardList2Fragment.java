package com.wikagedung.myyusuf.myapplication.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wikagedung.myyusuf.myapplication.R;
import com.wikagedung.myyusuf.myapplication.ScoreCardActivity;
import com.wikagedung.myyusuf.myapplication.model.ScoreCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScoreCardList2Fragment.OnScoreCardList2FragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScoreCardList2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreCardList2Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "ScoreCardList2Fragment";

    RecyclerView mRecyclerView;
    private ScoreCardAdapter adapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnScoreCardList2FragmentInteractionListener mListener;

    DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

    private ArrayList<ScoreCard> scoreCardList = new ArrayList<>();

    public ScoreCardList2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScoreCardList2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoreCardList2Fragment newInstance(String param1, String param2) {
        ScoreCardList2Fragment fragment = new ScoreCardList2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.initScoreCardList();

        final View view = inflater.inflate(R.layout.fragment_score_card_list2, container, false);

        final ScoreCardActivity scoreCardActivity = (ScoreCardActivity)getActivity();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.score_card_recycler_view);

        adapter = new ScoreCardAdapter(scoreCardList);
        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(scoreCardActivity.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onScoreCardList2FragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScoreCardList2FragmentInteractionListener) {
            mListener = (OnScoreCardList2FragmentInteractionListener) context;
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

    public void loadData(JSONObject jsonData) {

        this.resetScoreCardListData();

        try {
            setScoreCardValue(1, "kinerjaFokusPelanggan", "csi", jsonData);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnScoreCardList2FragmentInteractionListener {
        // TODO: Update argument type and name
        void onScoreCardList2FragmentInteraction(Uri uri);
    }


    public class ScoreCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<ScoreCard> list;

        public ScoreCardAdapter(ArrayList<ScoreCard> list) {
            this.list = list;
        }

        @Override
        public int getItemViewType(int position) {

            if(ScoreCardList2Fragment.this.scoreCardList.size() > 0){
                return ScoreCardList2Fragment.this.scoreCardList.get(position).getRowType();
            }else{
                return 0;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(ScoreCardList2Fragment.this.scoreCardList.size() > 0){
                int rowType = ScoreCardList2Fragment.this.scoreCardList.get(position).getRowType();
                if(rowType == 0){
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    headerViewHolder.updateUI(list.get(position));
                }else if(rowType == 1){
                    ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                    itemViewHolder.updateUI(list.get(position));
                }
            }else{
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.updateUI(list.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if(viewType == 0){
                View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_card_list_header, parent, false);
                return new HeaderViewHolder(card);
            }else{
                View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_card_list_item, parent, false);
                return new ItemViewHolder(card);
            }
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView label1;
        private ImageView statusImage;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            label1 = (TextView)itemView.findViewById(R.id.score_card_list_header_label_1);
            statusImage = (ImageView)itemView.findViewById(R.id.score_card_list_header_status_image);
        }

        public void updateUI(ScoreCard item) {

            label1.setText(item.getLabel1());

            if(item.getStatusImageName().equals("equal_trend")){
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.equal_trend));
            }else if(item.getStatusImageName().equals("up_trend")){
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.up_trend));
            }else if(item.getStatusImageName().equals("up_trend_red")){
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.up_trend_red));
            }else if(item.getStatusImageName().equals("down_trend")){
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.down_trend));
            }else if(item.getStatusImageName().equals("down_trend_red")){
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.down_trend_red));
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView label1;
        private TextView label2;
        private TextView label3;
        private TextView label4;
        private TextView label5;
        private TextView label6;
        private TextView label7;
        private TextView label8;

        public ItemViewHolder(View itemView) {
            super(itemView);

            label1 = (TextView)itemView.findViewById(R.id.score_card_list_item_label_1);
            label2 = (TextView)itemView.findViewById(R.id.score_card_list_item_label_2);
            label3 = (TextView)itemView.findViewById(R.id.score_card_list_item_label_3);
            label4 = (TextView)itemView.findViewById(R.id.score_card_list_item_label_4);
            label5 = (TextView)itemView.findViewById(R.id.score_card_list_item_label_5);
            label6 = (TextView)itemView.findViewById(R.id.score_card_list_item_label_6);
            label7 = (TextView)itemView.findViewById(R.id.score_card_list_item_label_7);
            label8 = (TextView)itemView.findViewById(R.id.score_card_list_item_label_8);

        }

        public void updateUI(ScoreCard item) {

            label1.setText(item.getLabel1());
            label2.setText(item.getLabel2());
            label3.setText(item.getLabel3());
            label4.setText(item.getLabel4());
            label5.setText(item.getLabel5());
            label6.setText(item.getLabel6());
            label7.setText(item.getLabel7());
            label8.setText(item.getLabel8());

        }
    }


    private void initScoreCardList(){

        addToScoreCardList("CSI");

    }

    private void addToScoreCardList(String groupName){
        ScoreCard scoreCard = new ScoreCard();
        scoreCard.setRowType(0);
        scoreCard.setLabel1(groupName);
        scoreCardList.add(scoreCard);

        scoreCard = new ScoreCard();
        scoreCard.setRowType(1);
        scoreCardList.add(scoreCard);

    }

    private void setScoreCardValue(int startPosition, String groupName, String subGroupName, JSONObject jsonData) throws JSONException {
        JSONObject group = jsonData.getJSONObject(groupName);
        JSONObject subGroup = group.getJSONObject(subGroupName);
        double target = subGroup.getDouble("target");
        double targetPeriodikRa = subGroup.getJSONObject("targetPeriodik").getDouble("ra");
        double targetPeriodikRi = subGroup.getJSONObject("targetPeriodik").getDouble("ri");
        double scoreRa = subGroup.getJSONObject("score").getDouble("ra");
        double scoreRi = subGroup.getJSONObject("score").getDouble("ri");
        double prevScoreRi = subGroup.getJSONObject("score").getDouble("prevRi");
        double nilai = subGroup.getDouble("nilai");
        double kpi = subGroup.getDouble("bobotKpi");
        String upj = subGroup.getString("upj");

        ScoreCard scoreCard = scoreCardList.get(startPosition);
        scoreCard.setLabel1(decimalFormat.format(target));
        scoreCard.setLabel2(decimalFormat.format(targetPeriodikRa));
        scoreCard.setLabel3(decimalFormat.format(targetPeriodikRi));
        scoreCard.setLabel4(decimalFormat.format(scoreRa));
        scoreCard.setLabel5(decimalFormat.format(scoreRi));
        scoreCard.setLabel6(decimalFormat.format(nilai));
        scoreCard.setLabel7(decimalFormat.format(kpi));
        scoreCard.setLabel8(upj);

        String statusImageName = "equal_trend";
        if(scoreRi > prevScoreRi){
            statusImageName = "up_trend";
        }else if(scoreRi < prevScoreRi){
            statusImageName = "down_trend_red";
        }

        ScoreCard headerScoreCard = scoreCardList.get(startPosition - 1);
        headerScoreCard.setStatusImageName(statusImageName);

    }

    private void resetScoreCardListData(){
        for(int i=0; i<scoreCardList.size(); i++){

            if(scoreCardList.get(i).getRowType() != 0){
                scoreCardList.get(i).setLabel1("");
            }
            scoreCardList.get(i).setLabel2("");
            scoreCardList.get(i).setLabel3("");
            scoreCardList.get(i).setLabel4("");
            scoreCardList.get(i).setLabel5("");
            scoreCardList.get(i).setLabel6("");
            scoreCardList.get(i).setLabel7("");
            scoreCardList.get(i).setLabel8("");
            scoreCardList.get(i).setStatusImageName("equal_trend");
        }
    }
}
