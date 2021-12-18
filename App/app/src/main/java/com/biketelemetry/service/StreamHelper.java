package com.biketelemetry.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StreamHelper {
    public static String readString(InputStream stream, int length) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch = 0;
        while ((ch = stream.read()) != -1) {
            sb.append((char) ch);
        }

        return sb.toString();
    }

    public static int readInt(InputStream stream) throws IOException {
        byte[] dummy = new byte[4];
        stream.read(dummy);
        return ByteBuffer.wrap(dummy).getInt();
    }

    public static double readDouble(InputStream stream) throws IOException {
        byte[] dummy = new byte[8];
        stream.read(dummy);
        return ByteBuffer.wrap(dummy).getDouble();
    }
}
