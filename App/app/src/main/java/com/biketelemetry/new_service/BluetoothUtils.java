package com.biketelemetry.new_service;

public class BluetoothUtils {
    public static final String ACTION_DISCOVERY_STARTED = "ACTION_DISCOVERY_STARTED";
    public static final String ACTION_DISCOVERY_STOPPED = "ACTION_DISCOVERY_STOPPED";
    public static final String ACTION_DEVICE_FOUND = "ACTION_DEVICE_FOUND";
    public static final String ACTION_DEVICE_CONNECTED = "ACTION_DEVICE_CONNECTED";
    public static final String ACTION_DEVICE_DISCONNECTED = "ACTION_DEVICE_DISCONNECTED";
    public static final String ACTION_MESSAGE_SENT = "ACTION_MESSAGE_SENT";
    public static final String ACTION_CONNECTION_ERROR = "ACTION_CONNECTION_ERROR";
    public static final String ACTION_DEVICE_INFO_RECEIVED = "ACTION_DEVICE_INFO_RECEIVED";
    public static final String ACTION_FILE_LIST_ENTRY_RECEIVED = "ACTION_FILE_LIST_ENTRY_RECEIVED";
    public static final String ACTION_FILE_RECEIVED = "ACTION_FILE_RECEIVED";
    public static final String ACTION_TELEMETRY_RECEIVED = "ACTION_TELEMETRY_RECEIVED";

    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    public static final String EXTRA_PARCEBLE = "EXTRA_PARCABLE";
    public static final String EXTRA_STRING = "EXTRA_STRING";

    public static final byte REQUEST_TAG_DEVICE_INFO = 2;
    public static final byte REQUEST_TAG_GET_FILE_LIST = 3;
    public static final byte REQUEST_TAG_GET_FILE = 4;
    public static final byte REQUEST_TAG_DELETE_FILE = 5;
    public static final byte REQUEST_TAG_ENABLE_TELEMETRY = 6;

    public static final byte RESPONSE_TAG_DEVICE_CONNECTED = 1;
    public static final byte RESPONSE_TAG_DEVICE_INFO = 2;
    public static final byte RESPONSE_TAG_GET_FILE_LIST_ENTRY = 3;
    public static final byte RESPONSE_TAG_GET_FILE_LIST_ENTRY_END = 4;
    public static final byte RESPONSE_TAG_GET_FILE = 5;
    public static final byte RESPONSE_TAG_GET_FILE_END = 6;
    public static final byte RESPONSE_TAG_TELEMETRY = 7;
    public static final byte RESPONSE_TAG_ERROR = (byte) 255;
}
