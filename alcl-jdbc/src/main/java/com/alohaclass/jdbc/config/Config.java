package com.alohaclass.jdbc.config;

import java.io.InputStream;
import java.util.Properties;


public class Config {
    public static boolean mapUnderscoreToCamelCase = false;
    public static boolean mapCamelCaseToUnderscore = false;
   
    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
            }
            prop.load(input);
            mapUnderscoreToCamelCase = Boolean.parseBoolean(prop.getProperty("mapUnderscoreToCamelCase", "false"));
            mapCamelCaseToUnderscore = Boolean.parseBoolean(prop.getProperty("mapCamelCaseToUnderscore", "false"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
