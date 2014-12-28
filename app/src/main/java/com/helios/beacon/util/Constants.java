package com.helios.beacon.util;

/**
 * Created by nhantran on 10/26/14.
 */
public class Constants {

    // ( 1244F4CC-8C7D-4D13-92F4-03DEA365EE65 )
    public static final String UUID = "01122334-4556-6778-899a-abbccddeeff0";

    public static final String RESTAURANT_MENU_URL = "http://54.68.242.152:8080/lbp/mobile/restaurant?major=majorValue&minor=minorValue";
    public static final String RESTAURANT_ORDER_URL = "http://54.68.242.152:8080/lbp/mobile/order?major=majorValue&minor=minorValue&data=dataValue";
    public static final String RESTAURANT_ORDER_CONFIRM = "http://54.68.242.152:8080/lbp/mobile/order/payment?orderId=orderIdValue";
    public static final String CONFIRM_ORDER_URL = "http://54.68.242.152:8080/lbp/mobile/order/payment?orderId=orderIdValue";

    public static final int REQUEST_CODE_PAYMENT = 0;

    public static final String CURRENCY_USD = "USD";
    public static final String CURRENCY_VND = "VND";
    public static final String DEFAULT_SKU = "NGU";

    public static final double USD_ABOVE_VND = 20000;

}

