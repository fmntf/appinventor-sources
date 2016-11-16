package org.udoo.minicom;


import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class SerialPort {

    private FileDescriptor mFd;
    private InputStream mFileInputStream;
    private OutputStream mFileOutputStream;

    public SerialPort(String device, int baudrate, int flags) throws SecurityException, IOException {

        mFd = open(device, baudrate, flags);
        if (mFd == null) {
            Log.e("SerialPort", "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    // JNI
    private native static FileDescriptor open(String path, int baudrate, int flags);
    public native void close();
    static {
        System.loadLibrary("serial_port");
    }
}