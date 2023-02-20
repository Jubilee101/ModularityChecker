package com.hzhang.modularitychecker.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LineCounter {
    private static int count(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner sc = new Scanner(file);
        int count = 0;
        while (sc.hasNextLine()) {
            sc.nextLine();
            count++;
        }
        sc.close();
        return count;
    }
    public static int countLines(String path) {
        try {
            return count(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
