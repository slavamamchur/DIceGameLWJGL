//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sadgames.vulkan.newclass.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;

public final class StreamUtils {
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final byte[] EMPTY_BYTES = new byte[0];

    public StreamUtils() {
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        copyStream(input, output, new byte[4096]);
    }

    public static void copyStream(InputStream input, OutputStream output, int bufferSize) throws IOException {
        copyStream(input, output, new byte[bufferSize]);
    }

    public static void copyStream(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        int bytesRead;
        while((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

    }

    public static void copyStream(InputStream input, ByteBuffer output) throws IOException {
        copyStream(input, output, new byte[4096]);
    }

    public static void copyStream(InputStream input, ByteBuffer output, int bufferSize) throws IOException {
        copyStream(input, output, new byte[bufferSize]);
    }

    public static int copyStream(InputStream input, ByteBuffer output, byte[] buffer) throws IOException {
        int startPosition = output.position();
        int total = 0;

        int bytesRead;
        while((bytesRead = input.read(buffer)) != -1) {
            output.put(buffer, 0, bytesRead);
            total += bytesRead;
            output.position(startPosition + total);
        }

        output.position(startPosition);
        return total;
    }

    public static byte[] copyStreamToByteArray(InputStream input) throws IOException {
        return copyStreamToByteArray(input, input.available());
    }

    public static byte[] copyStreamToByteArray(InputStream input, int estimatedSize) throws IOException {
        ByteArrayOutputStream baos = new OptimizedByteArrayOutputStream(Math.max(0, estimatedSize));
        copyStream(input, (OutputStream)baos);
        return baos.toByteArray();
    }

    public static String copyStreamToString(InputStream input) throws IOException {
        return copyStreamToString(input, input.available(), (String)null);
    }

    public static String copyStreamToString(InputStream input, int estimatedSize) throws IOException {
        return copyStreamToString(input, estimatedSize, (String)null);
    }

    public static String copyStreamToString(InputStream input, int estimatedSize, String charset) throws IOException {
        InputStreamReader reader = charset == null ? new InputStreamReader(input) : new InputStreamReader(input, charset);
        StringWriter writer = new StringWriter(Math.max(0, estimatedSize));
        char[] buffer = new char[4096];

        int charsRead;
        while((charsRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, charsRead);
        }

        return writer.toString();
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable var2) {
            }
        }

    }

    public static class OptimizedByteArrayOutputStream extends ByteArrayOutputStream {
        public OptimizedByteArrayOutputStream(int initialSize) {
            super(initialSize);
        }

        public synchronized byte[] toByteArray() {
            return this.count == this.buf.length ? this.buf : super.toByteArray();
        }

        public byte[] getBuffer() {
            return this.buf;
        }
    }
}
