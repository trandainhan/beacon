package com.helios.beacon.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nhantran.beaconexample.R;
import com.helios.beacon.Dialog.QuantityPickerDialog;
import com.helios.beacon.activity.MainActivity;
import com.helios.beacon.activity.OrderActivity;
import com.helios.beacon.adapter.BaseFragmentListAdapter;
import com.helios.beacon.application.BeaconApplication;
import com.helios.beacon.model.Item;
import com.helios.beacon.model.OrderedItem;
import com.helios.beacon.util.Constants;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends BaseFragment implements QuantityPickerDialog.NoticeDialogListener {

    private static final String TAG = OrderFragment.class.getSimpleName();

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Can not make network request!", Toast.LENGTH_SHORT).show();
                    }
                });
                hideProgressDialog();
            }
        });

        BeaconApplication.getInstance().addToRequestQueue(menuReq);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Item item = menuList.get(i);
        OrderedItem orderedItem = new OrderedItem();
        orderedItem.setItem(item);
        Intent intent = new Intent(getActivity(), OrderActivity.class);
        intent.putExtra("orderItem", orderedItem);
        intent.putExtra("major", ((MainActivity)getActivity()).getMajor());
        intent.putExtra("minor", ((MainActivity)getActivity()).getMinor());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        showDialog();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        QuantityPickerDialog quantityPickerDialog = (QuantityPickerDialog) dialog;
    }

    private PayPalPayment getStuffToBuy(OrderedItem orderedItem) {
        PayPalItem[] payPalItems = {new PayPalItem(orderedItem.getItem().getName(), orderedItem.getQuantity(), orderedItem.getTotalPrice(), Constants.CURRENCY_USD, Constants.DEFAULT_SKU)};
        BigDecimal subtotal = PayPalItem.getItemTotal(payPalItems);
        BigDecimal shipping = new BigDecimal("0");
        BigDecimal tax = new BigDecimal("0");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, Constants.CURRENCY_USD, orderedItem.getItem().getName(),
                PayPalPayment.PAYMENT_INTENT_SALE);
        payment.items(payPalItems).paymentDetails(paymentDetails);
        payment.custom("This is text that will be associated with the payment that the app can use.");
        return payment;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }
}
