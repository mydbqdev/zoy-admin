export class PaymentApprovalModel{    
  zoy_code: string;
  owner_name: string;
  total_amount: string;
  transaction_date: string;
  transaction_no: string;
  transaction_approval: 'Received' |' Not Received';	
}
