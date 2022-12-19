package com.hzhang.modularitychecker.model;

import java.util.Objects;

public class Pair {
    String file1;
    String file2;
    int moduleIndex1;
    int moduleIndex2;
    public Pair(String f1, String f2, int index1, int index2) {
        file1 = f1;
        file2 = f2;
        moduleIndex1 = index1;
        moduleIndex2 = index2;
    }

    public boolean sameModule() {
        return moduleIndex1 == moduleIndex2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(file1, pair.file1) && Objects.equals(file2, pair.file2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file1, file2);
    }
}
