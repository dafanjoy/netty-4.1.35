package io.netty.example.echo.test;

import io.netty.buffer.ByteBuf;

public class App {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.err.println(i + "---" + releas0(i));
        }
//        for (int i = 0; i < 10; i++) {
//            System.err.println(i + "---" + retain0(i));
//        }
    }

    private static int realRefCnt(int rawCnt) {
        return (rawCnt & 1) != 0 ? 0 : rawCnt >>> 1;
    }

    private static int retain0(int increment) {
        // all changes to the raw count are 2x the "real" change
        return increment << 1;
    }

    private static int releas0(int increment) {
        // all changes to the raw count are 2x the "real" change
        return increment >>> 1;
    }
}
