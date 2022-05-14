package me.ikevoodoo.jmage;

public class JUtils {

    public static void toBytes(byte[] bytes, int offset, int value) {
        bytes[offset] = (byte) (value >> 24);
        bytes[offset + 1] = (byte) (value >> 16);
        bytes[offset + 2] = (byte) (value >> 8);
        bytes[offset + 3] = (byte) value;
    }

    public static byte[] toBytes(int value) {
        byte[] bytes = new byte[4];
        toBytes(bytes, 0, value);
        return bytes;
    }

    public static int fromBytes(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) | ((bytes[offset + 1] & 0xFF) << 16) | ((bytes[offset + 2] & 0xFF) << 8) | (bytes[offset + 3] & 0xFF);
    }

}
