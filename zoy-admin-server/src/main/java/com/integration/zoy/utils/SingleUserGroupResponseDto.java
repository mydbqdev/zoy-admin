package com.integration.zoy.utils;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleUserGroupResponseDto {
	private String id;
	private List<String> userIds;

}
