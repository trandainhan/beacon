package com.helios.beacon.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.helios.beacon.Dialog.QuantityPickerDialog;
import com.helios.beacon.activity.MainActivity;
import com.helios.beacon.adapter.BaseFragmentListAdapter;
import com.helios.beacon.application.BeaconApplication;
import com.helios.beacon.model.Item;
import com.helios.beacon.model.OrderedItem;
import com.helios.beacon.util.Constants;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends BaseFragment implements QuantityPickerDialog.NoticeDialogListener {

    private static final String TAG = "OrderFragment";

    private List<Item> menuList = new ArrayList<Item>();

    public OrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setUpData(View view) {
        adapter = new BaseFragmentListAdapter(getActivity(), this, menuList);
        listView.setAdapter(adapter);
        showTextHint(view);
    }

    @Override
    public void makeRequestData(final View view, String major, String minor) {
        String url = Constants.RESTAURANT_MENU_URL;
        url = url.replace("majorValue", major);
        url = url.replace("minorValue", minor);
        menuList.clear();
        showDialog();
        JsonObjectRequest menuReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideTextHint(view);
                hideProgressDialog();
                try {
                    String code = response.getString("code");
                    if (!code.equalsIgnoreCase("SUCCESS")) return;

                    JSONObject data = response.getJSONObject("data");
                    JSONArray items = data.getJSONArray("menu");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = items.getJSONObject(i);
                        int id = obj.getInt("id");
                        String name = obj.getString("name");
                        double price = obj.getDouble("price");
                        String logoUrl = obj.getString("logoUrl");
                        String description = obj.getString("description");
                        String status = obj.getString("status");
                        Item item = new Item(id, name, price, logoUrl, description, status);
                        menuList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                } finally {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        BeaconApplication.getInstance().addToRequestQueue(menuReq);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object obj = adapterView.getItemAtPosition(i);
        // TODO: with object
    }

    @Override
    public void onClick(View v) {
        showDialog();
//        makeRequestData(view, );s
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        QuantityPickerDialog quantityPickerDialog = (QuantityPickerDialog) dialog;
        makeRequestOrder(quantityPickerDialog.getOrderedItem());
    }

    private void makeRequestOrder(final OrderedItem orderedItem) {
        MainActivity activity = (MainActivity) getActivity();
        String major = activity.getMajor();
        String minor = activity.getMinor();
        String url = Constants.RESTAURANT_ORDER_URL;
        url = url.replace("majorValue", major);
        url = url.replace("minorValue", minor);
        String data = "[" + orderedItem.toString() + "]";
        url = url.replace("dataValue", data);
        JsonObjectRequest orderReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                PayPalPayment payment = new PayPalPayment(new BigDecimal(5), Constants.CURRENCY, orderedItem.getItem().getName(),
                        PayPalPayment.PAYMENT_INTENT_SALE);
                PayPalItem[] payPalItems = {new PayPalItem(orderedItem.getItem().getName(), orderedItem.getQuantity(), new BigDecimal(orderedItem.getItem().getPrice()), Constants.CURRENCY, Constants.DEFAUT_SKU)};
                payment.items(payPalItems);

                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        BeaconApplication.getInstance().addToRequestQueue(orderReq);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }
}
