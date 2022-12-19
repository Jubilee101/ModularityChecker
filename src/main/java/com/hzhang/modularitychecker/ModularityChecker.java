package com.hzhang.modularitychecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzhang.modularitychecker.model.Info;
import com.hzhang.modularitychecker.model.MetaData;
import com.hzhang.modularitychecker.tool.DirectoryWalker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ModularityChecker {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("wrong number of inputs");
            return;
        }
        File file = new File(Paths.get(args[0], "meta_data.json").toString());
        if (file.exists() && !file.isDirectory()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                MetaData metaData = mapper.readValue(file, MetaData.class);
                DirectoryWalker walker = new DirectoryWalker(metaData);
                Info info = walker.walk();
                mapper.writeValue(Paths.get(args[0], "info.json").toFile(), info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("meta data does not exist");
            return;
        }
    }
}
