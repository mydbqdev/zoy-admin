export class SupportDetails {
    ticke_number: string;
    name: string;
    owner_email: boolean;
    mobile: string;
    address: string; 
    pincode:string;
    inquired_for:string;
    created_at:string;
    status:string;
    city:string;
    assigned_to:string;
    assigned_to_name:string;
    description:string;
    property_name:string;
    categories_name:string;
    urgency:string;
    images_urls:string;
    type:string;
    user_ticket_history:SupportHistoryList[];
  }


  export class SupportHistoryList{
    user_help_request_id: string; 
    created_at:string;
    user_email:string;
    description:string;
    request_status:string;
  }
