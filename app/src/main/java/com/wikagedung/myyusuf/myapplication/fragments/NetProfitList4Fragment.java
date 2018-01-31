package com.wikagedung.myyusuf.myapplication.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wikagedung.myyusuf.myapplication.NetProfitActivity;
import com.wikagedung.myyusuf.myapplication.R;
import com.wikagedung.myyusuf.myapplication.model.NetProfit;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NetProfitList4Fragment.OnNetProfitList4FragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NetProfitList4Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetProfitList4Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "NetProfitList3Fragment";
    private static final String RED_COLOR = "#FF3B3B";
    private static final String GREEN_COLOR = "#70AA3D";
    private static final String BLUE_COLOR = "#3333FF";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView mRecyclerView;
    private NetProfitAdapter adapter;

    private OnNetProfitList4FragmentInteractionListener mListener;

    DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");

    private ArrayList<NetProfit> netProfitList = new ArrayList<>();

    public NetProfitList4Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NetProfitList3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NetProfitList4Fragment newInstance(String param1, String param2) {
        NetProfitList4Fragment fragment = new NetProfitList4Fragment();
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

        this.initNetProfitList();

        final View view = inflater.inflate(R.layout.fragment_net_profit_list4, container, false);

        final NetProfitActivity netProfitActivity = (NetProfitActivity)getActivity();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.net_profit_recycler_view1);

        adapter = new NetProfitAdapter(netProfitList);
        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(netProfitActivity.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onNetProfitList4FragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNetProfitList4FragmentInteractionListener) {
            mListener = (OnNetProfitList4FragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNetProfitList4FragmentInteractionListener {
        // TODO: Update argument type and name
        void onNetProfitList4FragmentInteraction(Uri uri);
    }

    public void loadData(JSONObject jsonData) {

        this.resetNetProfitListData();

        try {
            setNetProfitValue(1, "totalPphFinal", "total", jsonData);
            setNetProfitValue(6, "totalPphFinal", "ekstern", jsonData);
            setNetProfitValue(11, "totalPphFinal", "joKso", jsonData);

            setNetProfitValue(17, "pphFinalLama", "lama", jsonData);
            setNetProfitValue(22, "pphFinalLama", "eksternIntern", jsonData);
            setNetProfitValue(27, "pphFinalLama", "joKso", jsonData);

            setNetProfitValue(33, "pphFinalBaru", "baru", jsonData);
            setNetProfitValue(38, "pphFinalBaru", "eksternIntern", jsonData);
            setNetProfitValue(43, "pphFinalBaru", "joKso", jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

    }


    public class NetProfitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<NetProfit> list;

        public NetProfitAdapter(ArrayList<NetProfit> list) {
            this.list = list;
        }

        @Override
        public int getItemViewType(int position) {

            if(NetProfitList4Fragment.this.netProfitList.size() > 0){
                return NetProfitList4Fragment.this.netProfitList.get(position).getRowType();
            }else{
                return 0;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(NetProfitList4Fragment.this.netProfitList.size() > 0){
                int rowType = NetProfitList4Fragment.this.netProfitList.get(position).getRowType();
                if(rowType == 0){
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    headerViewHolder.updateUI(list.get(position));
                }else if(rowType == 1){
                    ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                    itemViewHolder.updateUI(list.get(position));
                }else{
                    SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) holder;
                    separatorViewHolder.updateUI(list.get(position));
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
                View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.net_profit_list_header, parent, false);
                return new HeaderViewHolder(card);
            }else if(viewType == 1){
                View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.net_profit_list_item, parent, false);
                return new ItemViewHolder(card);
            }else{
                View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.net_profit_list_separator, parent, false);
                return new SeparatorViewHolder(card);
            }

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView label1;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            label1 = (TextView)itemView.findViewById(R.id.net_profit_list_header_label_1);

        }

        public void updateUI(NetProfit item) {


            label1.setText(item.getLabel1());
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView label1;
        private TextView label2;
        private TextView label3;

        public ItemViewHolder(View itemView) {
            super(itemView);

            label1 = (TextView)itemView.findViewById(R.id.net_profit_list_item_label_1);
            label2 = (TextView)itemView.findViewById(R.id.net_profit_list_item_label_2);
            label3 = (TextView)itemView.findViewById(R.id.net_profit_list_item_label_3);

        }

        public void updateUI(NetProfit item) {

            label1.setText(item.getLabel1());
            label2.setText(item.getLabel2());
            label3.setText(item.getLabel3());

            label2.setTextColor(Color.parseColor(item.getLabel2TextColor()));
        }
    }

    public class SeparatorViewHolder extends RecyclerView.ViewHolder {

        public SeparatorViewHolder(View itemView) {
            super(itemView);
        }

        public void updateUI(NetProfit item) {

        }
    }

    private void initNetProfitList(){

        addToNetProfitList("Total");
        addToNetProfitList("Ekstern");
        addToNetProfitList("JO/KSO");
        addSeparator();
        addToNetProfitList("Lama");
        addToNetProfitList("Ekstern + Intern");
        addToNetProfitList("JO/KSO");
        addSeparator();
        addToNetProfitList("Baru");
        addToNetProfitList("Ekstern + Intern");
        addToNetProfitList("JO/KSO");

    }

    private void addSeparator(){
        NetProfit netProfit = new NetProfit();
        netProfit.setRowType(3);
        netProfitList.add(netProfit);
    }

    private void addToNetProfitList(String groupName){
        NetProfit netProfit = new NetProfit();
        netProfit.setRowType(0);
        netProfit.setLabel1(groupName);
        netProfitList.add(netProfit);

        netProfit = new NetProfit();
        netProfit.setRowType(1);
        netProfit.setLabel1("RKAP");
        netProfitList.add(netProfit);

        netProfit = new NetProfit();
        netProfit.setRowType(1);
        netProfit.setLabel1("Ra s/d saat ini");
        netProfitList.add(netProfit);

        netProfit = new NetProfit();
        netProfit.setRowType(1);
        netProfit.setLabel1("Ri saat ini");
        netProfitList.add(netProfit);

        netProfit = new NetProfit();
        netProfit.setRowType(1);
        netProfit.setLabel1("Prognosa");
        netProfitList.add(netProfit);
    }

    private void setNetProfitValue(int startPosition, String groupName, String subGroupName, JSONObject jsonData) throws JSONException {
        JSONObject group = jsonData.getJSONObject(groupName);
        JSONObject subGroup = group.getJSONObject(subGroupName);
        double rkap = subGroup.getDouble("rkap") / 1000000;
        double raSdSaatIni = subGroup.getDouble("raSdSaatIni") / 1000000;
        double riSaatIni = subGroup.getDouble("riSaatIni") / 1000000;
        double persenRiThdRa = subGroup.getDouble("persenRiThdRa");
        double prognosa = subGroup.getDouble("prognosa") / 1000000;
        double persenPrognosa = subGroup.getDouble("persenPrognosa");

        NetProfit netProfit = netProfitList.get(startPosition);
        netProfit.setLabel2(decimalFormat.format(rkap));

        netProfit = netProfitList.get(startPosition+1);
        netProfit.setLabel2(decimalFormat.format(raSdSaatIni));

        netProfit = netProfitList.get(startPosition+2);
        netProfit.setLabel2(decimalFormat.format(riSaatIni));
        netProfit.setLabel3(decimalFormat.format(persenRiThdRa) + "%");
        if(riSaatIni < raSdSaatIni){
            netProfit.setLabel2TextColor(RED_COLOR);
        }else if(riSaatIni > raSdSaatIni){
            netProfit.setLabel2TextColor(GREEN_COLOR);
        }else{
            netProfit.setLabel2TextColor(BLUE_COLOR);
        }

        netProfit = netProfitList.get(startPosition+3);
        netProfit.setLabel2(decimalFormat.format(prognosa));
        netProfit.setLabel3(decimalFormat.format(persenPrognosa) + "%");
    }

    private void resetNetProfitListData(){
        for(int i=0; i<netProfitList.size(); i++){
            netProfitList.get(i).setLabel2("");
            netProfitList.get(i).setLabel3("");
            netProfitList.get(i).setLabel2TextColor("#000000");
        }
    }
}
