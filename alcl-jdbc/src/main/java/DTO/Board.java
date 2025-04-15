package DTO;

import java.util.Date;

import com.alohaclass.jdbc.annotation.Pk;
import com.alohaclass.jdbc.annotation.Table;
import com.alohaclass.jdbc.dto.Entity;

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
	private Long no;
	private String id;
	private String title;
	private String writer;
	private String content;
	private Date createdAt;
	private Date updatedAt;
	

}
