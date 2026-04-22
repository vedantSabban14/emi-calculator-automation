package utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class configReader{

    static Properties prop = new Properties();
    static{
        try {
            FileInputStream file = new FileInputStream(
                    "src/main/resources/config.properties"
            );
            prop.load(file);
            file.close();
            System.out.println("Config file loaded successfully.");
        } catch (
                IOException e) {
            System.out.println("ERROR: config.properties file not found.");
            e.printStackTrace();
        }
    }

    public static String getURL(){
        return prop.getProperty("url");
    }

    public static String getCarLoanAmount(){
        return prop.getProperty("loanAmount");
    }

    public static String getInterestRate(){
        return prop.getProperty("interestRate");
    }

    public static String getTenure(){
        return prop.getProperty("tenure");
    }
}
