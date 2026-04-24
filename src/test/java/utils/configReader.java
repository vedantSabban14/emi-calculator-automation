package utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class configReader{

    static Properties prop = new Properties();
    static{
        try {
            FileInputStream file = new FileInputStream(
                    "src/test/resources/config.properties"
            );
            prop.load(file);
            file.close();
        } catch (
                IOException e) {
            System.out.println("ERROR: config.properties file not found.");
            e.printStackTrace();
        }
    }

    public static String getURL(){
        return prop.getProperty("url");
    }
}
