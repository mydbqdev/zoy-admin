export class SupportList {
  ticket_id: string;
  created_date: string;
  ticket_type: string;
  priority: boolean;
  assign_name: string;
  assign_email: string; 
  status:string;
  type:string;
  }


  export class SupportTeamList{
    email: string; 
    name:string;
    type:string;
  }


  export class TicketAssign{
    email: string; 
    name:string;
    inquiryNumber:string;
    inquiryType:string;
    isSelf:boolean;
  }


  export class UpdateStatus{
    status:string;
    inquiryNumber:string;
    inquiryType:string;
    comment:string;
  }