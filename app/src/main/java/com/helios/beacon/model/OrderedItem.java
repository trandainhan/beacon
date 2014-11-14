package com.helios.beacon.model;

/**
 * Created by nhantran on 11/13/14.
 */
public class OrderedItem {

    private int quantity;
    private Item item;

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

    public double getTotalPrice() {
        return quantity * item.getPrice();
    }

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
