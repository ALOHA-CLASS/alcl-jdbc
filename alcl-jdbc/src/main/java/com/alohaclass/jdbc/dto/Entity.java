package com.alohaclass.jdbc.dto;

import com.alohaclass.jdbc.annotation.Pk;
import com.alohaclass.jdbc.annotation.Table;

public class Entity {
    public String tableName;
    public String pk;
    
    public Entity() {
		initializeTableName();
		initializePk();
	}
	
	public void initializePk() {
		// 특정 변수 위에 @Pk 붙어 있으면 그 변수를 pk로 설정
		Class<?> clazz = this.getClass();
		if (clazz.isAnnotationPresent(Pk.class)) {
			Pk pk = clazz.getAnnotation(Pk.class);
			this.pk = pk.toString();
		}
	}
    
    public void initializeTableName() {
        Class<?> clazz = this.getClass();
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = clazz.getAnnotation(Table.class);
            this.tableName = table.value();
        }
    }
    

    public String getTableName() {
        return tableName;
    }

    public String getPk() {
        return pk;
    }
    
    
}




