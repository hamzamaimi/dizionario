package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private Properties props = null;

    public String getValue(String pathToFile, String key) throws IOException {
        if(props == null) {
            getPropertyObject(pathToFile);
        }
        return props.getProperty(key);
    }

    private void getPropertyObject(String pathToFile) throws IOException{
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(pathToFile);
        props = new Properties();
        props.load(inputStream);
    }




}
