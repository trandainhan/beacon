package com.helios.beacon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nhantran.beaconexample.R;
import com.helios.beacon.application.BeaconApplication;
import com.helios.beacon.model.OrderedItem;
import com.helios.beacon.util.Constants;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class OrderActivity extends Activity implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    private static final String TAG = OrderActivity.class.getSimpleName();

    ImageLoader imageLoader = BeaconApplication.getInstance().getImageLoader();

    private OrderedItem orderedItem;
    String major;
    String minor;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        orderedItem = getIntent().getParcelableExtra("orderItem");
        major = getIntent().getStringExtra("major");
        minor = getIntent().getStringExtra("minor");

        TextView name = (TextView) findViewById(R.id.name);
        TextView price = (TextView ) findViewById(R.id.price);
        TextView description = (TextView) findViewById(R.id.description);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.thumbnail);

        name.setText(orderedItem.getItem().getName());
        price.setText(orderedItem.getItem().getPrice().toString());
        description.setText(orderedItem.getItem().getDescription());
        thumbNail.setImageUrl(orderedItem.getItem().getLogoUrl(), imageLoader);
        numberPicker.setMaxValue(30);
        numberPicker.setMinValue(1);
        numberPicker.setValue(1);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        Button btnOrder = (Button) findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "On Activity Result");
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.d(TAG, confirm.toJSONObject().toString(4));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
                Toast.makeText(
                        getApplicationContext(),
                        "PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
                        .show();
                sendOrderConfirmReq();
                onBackPressed();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i( TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    private void sendOrderConfirmReq(){
        String url = Constants.CONFIRM_ORDER_URL;
        url.replace("orderIdValue", orderId);
        JsonObjectRequest orderReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        BeaconApplication.getInstance().addToRequestQueue(orderReq);
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.push_right_in, R.anim.fade_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        makeRequestOrder();
    }

    private void makeRequestOrder() {
        String url = Constants.RESTAURANT_ORDER_URL;
        url = url.replace("majorValue", major);
        url = url.replace("minorValue", minor);
        String data = "[" + orderedItem.toString() + "]";
        url = url.replace("dataValue", data);
        JsonObjectRequest orderReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject data = response.getJSONObject("data");
                    orderId = data.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PayPalPayment payment = getStuffToBuy(orderedItem);

                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Your order are not available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        BeaconApplication.getInstance().addToRequestQueue(orderReq);
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
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    }
}
