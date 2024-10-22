package com.alohaclass.jdbc.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class JDBConnection {
    
    public Connection con;                // 연결된 드라이버에 SQL을 요청할 객체를 생성하는 클래스
    public Statement stmt;                // SQL 실행 요청을 하는 클래스
    public PreparedStatement psmt;        // Statement 에서 ? 파라미터 확장기능을 추가로 제공하는 클래스
    public ResultSet rs;                // SQL 실행 결과를 받아오는 클래스
    
    // 기본 생성자
    public JDBConnection() {
        // JDBC 드라이버 로드
        // MySQL
        try {
            // mysql-connector-j.xxx.jar 드라이버의 클래스를 로드한다.
            Class.forName("com.mysql.cj.jdbc.Driver");         
            
            // 설정 파일 읽기
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    System.err.println("db.properties 파일을 찾을 수 없습니다.");
                    return;
                }
                // load a properties file from class path, inside static method
                props.load(input);
            }

            // DB에 연결
            String url = props.getProperty("db.url");
            String id = props.getProperty("db.username");
            String pw = props.getProperty("db.password");
            
            // 자바 프로그램에서 JDBC 드라이버를 연결시켜주는 클래스
            // getConnection() 메소드로 DB에 연결 요청하고 생성된 Connection 객체를 반환받는다.
            con = DriverManager.getConnection(url, id, pw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}