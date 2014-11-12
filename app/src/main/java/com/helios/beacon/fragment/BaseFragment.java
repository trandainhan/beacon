package com.helios.beacon.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nhantran.beaconexample.R;
import com.helios.beacon.adapter.BaseFragmentListAdapter;
import com.helios.beacon.util.ConnectionDetector;


public abstract class BaseFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    private static final String TAG = BaseFragment.class.getSimpleName();


    protected ProgressDialog dialog;
    protected ListView listView;
    protected BaseFragmentListAdapter adapter;
    protected Button btnNetworkRetry;
    protected View view;

    public BaseFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    protected void showDialog() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.show();
    }

    protected void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_base, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        btnNetworkRetry = (Button) view.findViewById(R.id.btnNetworkRetry);
        btnNetworkRetry.setOnClickListener(this);
        showTextHint(view);
        setUpData(view);
        return view;
    }

    protected abstract void setUpData(View view);

    protected void hideTextHint(View view){
        TextView noItemsInfo = (TextView) view.findViewById(R.id.noItemsInfo);
        RelativeLayout noNetWorkInfo = (RelativeLayout) view.findViewById(R.id.rlNetworkInfo);
        noItemsInfo.setVisibility(View.GONE);
        noNetWorkInfo.setVisibility(View.GONE);
    }

    protected void showTextHint(View view){
        TextView noItemsInfo = (TextView) view.findViewById(R.id.noItemsInfo);
        RelativeLayout networkInfo = (RelativeLayout) view.findViewById(R.id.rlNetworkInfo);
        if (!ConnectionDetector.isConnectingToInternet(getActivity())){
            noItemsInfo.setVisibility(View.GONE);
            networkInfo.setVisibility(View.VISIBLE);
        } else {
            noItemsInfo.setVisibility(View.VISIBLE);
            networkInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object obj = adapterView.getItemAtPosition(i);
        // TODO: with object
    }

    @Override
    public void onClick(View v) {}

}
