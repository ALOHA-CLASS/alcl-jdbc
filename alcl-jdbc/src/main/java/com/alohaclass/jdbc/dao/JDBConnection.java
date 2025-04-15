package com.alohaclass.jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.alohaclass.jdbc.config.Config;

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
            con = DriverManager.getConnection(Config.url, Config.username, Config.password);
            // Auto Commit 여부 설정
        	try {
    			con.setAutoCommit(Config.autoCommit);
    			// System.out.println("Auto Commit : " + Config.autoCommit);
        	} catch (Exception e) {
        		System.err.println("Auto Commit 설정 실패");
        	}
            	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}