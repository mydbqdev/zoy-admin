package com.integration.zoy.utils;

import java.util.List;
import lombok.Data;


@Data
public class PgBlacklistedResponse {

	private List<String> emailIds;
	private List<String> mobileNos;
}
