
DROP TABLE IF EXISTS board;
CREATE TABLE board (
    no INT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
    id VARCHAR(64) NOT NULL UNIQUE COMMENT 'UK',
    title VARCHAR(255) NOT NULL COMMENT '제목',
    writer VARCHAR(100) NOT NULL COMMENT '작성자',
    content TEXT NOT NULL COMMENT '내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일'
) COMMENT '게시판';

INSERT INTO board(id, title, writer, content)
VALUES 
(UUID(), '제목001', '작성자001', '내용001'),
(UUID(), '제목002', '작성자002', '내용002'),
(UUID(), '제목003', '작성자003', '내용003'),
(UUID(), '제목004', '작성자004', '내용004'),
(UUID(), '제목005', '작성자005', '내용004')
;
