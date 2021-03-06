package com.biketelemetry.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StreamHelper {
    public static String readString(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int length = stream.read();
        int ch;//stream.available()
        while ((ch = stream.read()) != -1 && ch != 0 && sb.length() < length) {
            System.out.println("Char: " + ch);
            sb.append((char) ch);
        }

        return sb.toString();
    }

    public static short readShort(InputStream stream) throws IOException {
        return createBuffer(stream, 2).getShort();
    }

    public static int readInt(InputStream stream) throws IOException {
        return createBuffer(stream, 4).getInt();
    }

    public static double readDouble(InputStream stream) throws IOException {
        return createBuffer(stream, 8).getDouble();
    }

    private static ByteBuffer createBuffer(InputStream stream, int length) throws IOException {
        byte[] dummy = new byte[length];
        stream.read(dummy);
        return ByteBuffer.wrap(dummy)
                //.order(ByteOrder.LITTLE_ENDIAN)
        ;
    }
}
