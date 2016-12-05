package net.wouto.proxy;

import java.io.*;
import java.util.Properties;

public class Config extends Properties {

    private static final long serialVersionUID = -2452195490742797839L;
    private File file;

    private Config(File file) throws IOException {
        this.file = file;
        if (!this.file.exists()) {
            this.file.createNewFile();
        }
        this.load(new FileInputStream(file));
    }

    public static Config getConfig(File file) throws IOException {
        return new Config(file);
    }

    public void save() throws IOException {
        this.store(new FileOutputStream(this.file), "");
    }

}
