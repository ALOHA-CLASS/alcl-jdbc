# alcl-jdbc
DAO 자동 CRUD 라이브러리


# alcl-jdbc 사용법

## 1. Entity 클래스 생성
`@Table`과 `@Pk` 어노테이션을 사용하여 엔티티 클래스를 정의합니다. Lombok을 함께 사용하면 편리합니다.

```java
@Table("board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    @Pk
    private Long no;
    private String id;
    private String title;
    private String writer;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
```

## 2. DAO 클래스 생성
`BaseDAOImpl<T>`를 상속받아 DAO 클래스를 생성하면 CRUD 기능이 자동으로 제공됩니다.

```java
package DAO;

import com.alohaclass.jdbc.dao.BaseDAOImpl;
import DTO.Board;

public class BoardDAO extends BaseDAOImpl<Board> {
    // 필요 시 메서드 오버라이드 가능
}
```

## 3. DAO 사용 예시
```java
BoardDAO boardDAO = new BoardDAO();

// INSERT
Board newBoard = Board.builder()
    .id("testuser")
    .title("제목입니다")
    .writer("작성자")
    .content("내용입니다")
    .createdAt(new Date())
    .updatedAt(new Date())
    .build();
boardDAO.insert(newBoard);

// SELECT
Board board = boardDAO.findById(1L);

// UPDATE
board.setTitle("수정된 제목");
boardDAO.update(board);

// DELETE
boardDAO.delete(board);
```

## 4. 기타 기능 (BaseDAO 인터페이스 기준)
- `list()` : 전체 목록 조회
- `listBy(Map<Object, Object> fields)` : 특정 조건 목록 조회
- `page()` 및 오버로딩된 `page(...)` : 페이징 처리 목록 조회
- `select(Object pk)` : 기본키로 조회
- `selectBy(Map<Object, Object> fields)` : 조건 기반 단건 조회
- `insert(T entity)` : 엔티티 저장 (null 제외)
- `insertKey(T entity)` : 저장 후 PK 값 반환
- `update(T entity)` : 전체 필드 업데이트
- `update(T entity, String... fields)` : 일부 필드 업데이트
- `delete(Object pk)` : PK 기반 삭제
- `deleteBy(Map<Object, Object> fields)` : 조건 기반 삭제
- `in(String col, List<String> values)` : IN 조건 목록 조회
- `count()` : 전체 레코드 수 반환
- `count(String keyword, List<String> searchOptions)` : 검색 조건 개수
- `getSearchOptions(List<String> searchOptions)` : LIKE 검색 쿼리 생성
- `getFilterOptions(Map<String, String> filterOptions)` : ORDER BY 생성

## 5. 주의 사항
- `@Pk`는 반드시 한 개만 선언해야 합니다.
- `@Table` 이름은 실제 DB 테이블 이름과 일치해야 합니다.
- 날짜 필드(Date)는 java.sql.Date 또는 java.util.Date 모두 지원합니다.
- `BaseDAO<T>`를 직접 구현하거나 확장할 수 있습니다. 쿼리 최적화 또는 특별한 조건이 필요할 경우 오버라이딩하여 사용하세요.

---

라이브러리 내부 동작 방식이나 커스터마이징 방법은 [docs 디렉토리] 또는 소스코드를 참조하세요.

