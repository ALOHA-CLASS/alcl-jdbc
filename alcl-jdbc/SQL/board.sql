DROP TABLE IF EXISTS board;
CREATE TABLE board (
    no INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
    id VARCHAR(64) NOT NULL UNIQUE COMMENT 'UK',
    title VARCHAR(255) NOT NULL COMMENT '제목',
    writer VARCHAR(100) NOT NULL COMMENT '작성자 PK',
    content TEXT NOT NULL COMMENT '내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) COMMENT '게시판';


INSERT INTO board (id, title, writer, content) VALUES
(UUID(), '첫 번째 글 제목', 'writer01', '첫 번째 글 내용입니다.'),
(UUID(), '두 번째 글 제목', 'writer02', '두 번째 글 내용입니다.'),
(UUID(), '세 번째 글 제목', 'writer03', '세 번째 글 내용입니다.'),
(UUID(), '네 번째 글 제목', 'writer04', '네 번째 글 내용입니다.'),
(UUID(), '다섯 번째 글 제목', 'writer05', '다섯 번째 글 내용입니다.');
