package com.alohaclass.jdbc.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 페이징
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageInfo<T> {
	
	Page page;								// 페이징
	List<T> list;							// 데이터 목록
	String keyword;							// 검색어
	List<String> searchOptions;		// 검색 옵션 (title, writer, content, ...)
    Map<String, String> filterOptions;		// 필터 옵션
    
}








