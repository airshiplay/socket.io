package com.airlenet;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileTest {

    @Test
    public void test() throws IOException {
        FileInputStream inputStream = new FileInputStream(new File("/Users/lig/Downloads/a.txt"));
        byte[] b = new byte[1024];
        int read = inputStream.read(b);
        System.out.println(read);
    }
}
