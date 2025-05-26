package com.alohaclass.test.DTO;

import java.util.Date;

import com.alohaclass.jdbc.annotation.Pk;
import com.alohaclass.jdbc.annotation.Table;
import com.alohaclass.jdbc.annotation.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

	@Pk
	private Long boardNo;			// 기본키
	private String id;
	private String title;
	private String writer;
	private String content;
	private Date date;
	private Date createdAt;
	private Date updatedAt;
	
	@Column(exist = false)		// 실제 DB 컬럼에 없는 변수
	private String test;
	

}
