package com.alohaclass.jdbc.config;

import java.io.InputStream;
import java.util.Properties;


public class Config {
    public static boolean mapUnderscoreToCamelCase = false;
    public static boolean mapCamelCaseToUnderscore = false;
    public static boolean autoCommit = true;
    public static String url = null;
    public static String username = null;
    public static String password = null;
   
    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
            }
            prop.load(input);
            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
            mapUnderscoreToCamelCase = Boolean.parseBoolean(prop.getProperty("mapUnderscoreToCamelCase", "false"));
            mapCamelCaseToUnderscore = Boolean.parseBoolean(prop.getProperty("mapCamelCaseToUnderscore", "false"));
            autoCommit = Boolean.parseBoolean(prop.getProperty("autoCommit", "true"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
