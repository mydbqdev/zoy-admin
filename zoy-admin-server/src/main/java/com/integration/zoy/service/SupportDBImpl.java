package com.integration.zoy.service;

import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.model.SupportTicketDTO;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.PaginationRequest;

public interface SupportDBImpl {

	CommonResponseDTO<SupportTicketDTO> zoySupportTicketList(PaginationRequest paginationRequest, boolean isClose,boolean isFinanceUser)  throws WebServiceException;
}
