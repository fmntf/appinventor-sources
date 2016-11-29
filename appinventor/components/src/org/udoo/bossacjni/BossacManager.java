package org.udoo.bossacjni;

public class BossacManager {

    public native int BossacWriteImage(String sam3xImage, boolean verify);
    public native int BossacReadImage(String sam3xImage, long sizeToRead);

    static {
        System.loadLibrary("bossacjni");
    }

}
