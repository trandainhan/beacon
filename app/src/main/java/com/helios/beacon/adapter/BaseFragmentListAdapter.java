package com.helios.beacon.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.nhantran.beaconexample.R;
import com.helios.beacon.Dialog.QuantityPickerDialog;
import com.helios.beacon.application.BeaconApplication;
import com.helios.beacon.fragment.OrderFragment;
import com.helios.beacon.model.Item;
import com.helios.beacon.model.OrderedItem;
import com.helios.beacon.util.Constants;

import java.util.List;

/**
 * Created by nhantran on 10/23/14.
 */


public class BaseFragmentListAdapter extends BaseAdapter {

    private static final String TAG = "BaseFragmentListAdapter";

    private Activity activity;
    private OrderFragment fragment;
    private LayoutInflater inflater;
    private List<Item> menuItems;
    ImageLoader imageLoader = BeaconApplication.getInstance().getImageLoader();

    private int isFirst = 0;

    public BaseFragmentListAdapter(Activity activity, OrderFragment fragment, List<Item> menuItems) {
        this.activity = activity;
        this.fragment = fragment;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int location) {
        return menuItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.fragment_base_list_row, null);

        if (imageLoader == null)
            imageLoader = BeaconApplication.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.name);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        final Item item = menuItems.get(position);
        thumbNail.setImageUrl(item.getLogoUrl(), imageLoader);
        title.setText(item.getName());
        description.setText(item.getDescription());
        price.setText(item.getPrice().toString() + " " + Constants.CURRENCY_VND);

        return convertView;
    }

    private void showPreOrderDialog(Item item){
        QuantityPickerDialog quantityPickerDialog = new QuantityPickerDialog();
        OrderedItem orderedItem = new OrderedItem();
        orderedItem.setItem(item);
        quantityPickerDialog.setOrderedItem(orderedItem);
        quantityPickerDialog.setListener(fragment);
        quantityPickerDialog.show(activity.getFragmentManager(), "quantityDialog");
    };
}