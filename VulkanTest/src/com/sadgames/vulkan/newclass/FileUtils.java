package com.sadgames.vulkan.newclass;

import org.lwjgl.BufferUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class FileUtils {

    public static InputStream readFile(File file) {
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (Exception var2) {
                if (file.isDirectory()) {
                    throw new RuntimeException("Cannot open a stream to a directory: " + file, var2);
                } else {
                    throw new RuntimeException("Error reading file: " + file, var2);
                }
            }
        }
        else
            throw new RuntimeException("Error reading file: " + file);
    }

    public static BufferedInputStream readFile(File file, int bufferSize) {
        return new BufferedInputStream(readFile(file), bufferSize);
    }

    public static Reader reader(File file) {
        return new InputStreamReader(readFile(file));
    }

    public static Reader reader(File file, String charset) {
        InputStream stream = readFile(file);

        try {
            return new InputStreamReader(stream, charset);
        } catch (UnsupportedEncodingException var4) {
            StreamUtils.closeQuietly(stream);
            throw new RuntimeException("Error reading file: " + file, var4);
        }
    }

    public static BufferedReader reader(File file, int bufferSize) {
        return new BufferedReader(new InputStreamReader(readFile(file)), bufferSize);
    }

    public static BufferedReader reader(File file, int bufferSize, String charset) {
        try {
            return new BufferedReader(new InputStreamReader(readFile(file), charset), bufferSize);
        } catch (UnsupportedEncodingException var4) {
            throw new RuntimeException("Error reading file: " + file, var4);
        }
    }

    public static String readString(File file) {
        return readString(file, (String)null);
    }

    public static String readString(File file, String charset) {
        StringBuilder output = new StringBuilder(estimateLength(file));
        InputStreamReader reader = null;

        try {
            if (charset == null) {
                reader = new InputStreamReader(readFile(file));
            } else {
                reader = new InputStreamReader(readFile(file), charset);
            }

            char[] buffer = new char[256];

            while(true) {
                int length = reader.read(buffer);
                if (length == -1) {
                    return output.toString();
                }

                output.append(buffer, 0, length);
            }
        } catch (IOException var9) {
            throw new RuntimeException("Error reading layout file: " + file, var9);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
    }

    public static byte[] readBytes(File file) {
        InputStream input = readFile(file);

        byte[] var2;
        try {
            var2 = StreamUtils.copyStreamToByteArray(input, estimateLength(file));
        } catch (IOException var6) {
            throw new RuntimeException("Error reading file: " + file, var6);
        } finally {
            StreamUtils.closeQuietly(input);
        }

        return var2;
    }

    public static ByteBuffer readBuffer(File file) {
        InputStream input = readFile(file);

        ByteBuffer output = null;
        try {
            output = BufferUtils.createByteBuffer(estimateLength(file));
            StreamUtils.copyStream(input, output);
        } catch (IOException var6) {
            if (output != null) {
                output.limit(0);
                output = null;
            }
            throw new RuntimeException("Error reading file: " + file, var6);
        } finally {
            StreamUtils.closeQuietly(input);
        }

        return output;
    }

    private static int estimateLength(File file) {
        int length = (int)file.length();
        return length != 0 ? length : 512;
    }

    public static int readBytes(File file, byte[] bytes, int offset, int size) {
        InputStream input = readFile(file);
        int position = 0;

        try {
            while(true) {
                int count = input.read(bytes, offset + position, size - position);
                if (count <= 0) {
                    return position - offset;
                }

                position += count;
            }
        } catch (IOException var10) {
            throw new RuntimeException("Error reading file: " + file, var10);
        } finally {
            StreamUtils.closeQuietly(input);
        }
    }
}