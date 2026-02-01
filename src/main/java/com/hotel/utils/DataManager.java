package com.hotel.utils;

import java.io.File;

public class DataManager {
    private static final String DATA_DIR = "src/main/resources/data";
    private String FILE_PATH;

    public DataManager(String fileName) {
        FILE_PATH = DATA_DIR + File.separator + fileName;;
    }

    public String getFILE_PATH() {
        return FILE_PATH;
    }

    public String getDataDir() {
        return DataManager.DATA_DIR;
    }

    public void ensureDataDirectoryExists() {
        File dataDir = new File(DataManager.DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

}
