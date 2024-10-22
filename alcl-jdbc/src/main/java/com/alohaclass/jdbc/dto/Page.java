package com.alohaclass.jdbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * [페이징]
 * ✅ 페이지 필수 정보
 * 페이지 번호          : 
 * 페이지당 게시글 수       
 * 노출 페이지 개수
 * 전체 데이터 개수
 * 
 * ⭐ 페이지 수식 정보
 * 시작 번호
 * 끝 번호
 * 첫 번호
 * 마지막 번호       
 */
@Data
@AllArgsConstructor
@Builder
public class Page {

    // 페이징 기본값
    public static final int PAGE = 1;  		// 현재 페이지 번호 기본값
    public static final int SIZE = 10;      // 페이지당 게시글 수 기본값
    public static final int COUNT = 10;     // 노출 페이지 개수 기본값


    // ✅ 필수 정보
    private int page;   // 페이지 번호
    private int size;   // 페이지당 글 수
    private int count;  // 노출 페이지 개수
    private int total;  // 전체 데이터 개수

    // ⭐ 수식 정보
    private int start;  // 시작 번호
    private int end;    // 끝 번호
    private int first;  // 첫 번호
    private int last;   // 마지막 번호

    private int prev;   // 이전 번호
    private int next;   // 다음 번호

    private int index;  // 데이터 순서 번호

    // 생성자
    public Page() {
        this(0);
    }

    // 데이터 개수
    public Page(int total) {
        this(PAGE, total);
    }

    // 현재 번호, 데이터 개수
    public Page(int page, int total) {
        this(page, SIZE, COUNT, total);
    }

    public Page(int page, int size, int count, int total) {
        this.page = page;
        this.size = size;
        this.count = count;
        this.total = total;
        calc();
    }

    // setter
    // - 데이터 개수 지정 후, 페이징 수식 재계산
    public void setTotal(int total) {
        this.total = total;
        calc();
    } 

    // 페이징 처리 수식
    public void calc() {
        // 첫 번호
        this.first = 1;
        // 마지막 번호
        this.last = (this.total - 1) / size + 1;
        // 시작 번호
        this.start = ( (page-1) / count ) * count + 1;
        // 끝 번호
        this.end = ( (page-1) / count + 1 ) * count;
        if( this.end > this.last ) this.end = this.last;

        // 이전 번호
        this.prev = this.page - 1;
        // 다음 번호
        this.next = this.page + 1;
        // 데이터 순서 번호(index)
        this.index = (this.page - 1) * this.size;
    }
    
}

