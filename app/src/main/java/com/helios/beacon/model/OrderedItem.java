package com.helios.beacon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.helios.beacon.util.Constants;

import java.math.BigDecimal;

/**
 * Created by nhantran on 11/13/14.
 */
public class OrderedItem implements Parcelable {

    private int quantity;
    private Item item;

    private OrderedItem(Parcel in){
        quantity = in.readInt();
        item = in.readParcelable(Item.class.getClassLoader());
    }

    public OrderedItem() {
        quantity = 1;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getTotalPrice() {
        return new BigDecimal(quantity * item.getPrice() / Constants.USD_ABOVE_VND).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(quantity);
        dest.writeParcelable(item, flags);
    }

    public static final Parcelable.Creator<OrderedItem> CREATOR
            = new Parcelable.Creator<OrderedItem>() {
        public OrderedItem createFromParcel(Parcel in) {
            return new OrderedItem(in);
        }

        public OrderedItem[] newArray(int size) {
            return new OrderedItem[size];
        }
    };

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"itemId\"");
        sb.append(":");
        sb.append(item.getId());
        sb.append(",");
        sb.append("\"quantity\"");
        sb.append(":");
        sb.append(quantity);
        sb.append("}");
        return sb.toString();
    }
}
