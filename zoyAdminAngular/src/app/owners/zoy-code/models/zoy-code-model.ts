export class ZoyData {
    zoy_code: string;
    owner_name: string;
    email_id: string;
    mobile_no: string;
    created_date: string;
    status: 'registered' | 'pending'; 

    firstName: string;
    lastName: string;
    contactNumber: string;
    userEmail: string;
    zoyShare :string;
    public property_city:string="";
    public property_state:string="";
    public property_state_short_name:string="";
    public property_name:string;
    public property_pincode:number;
    public property_locality:string="";
    public property_house_area:string="";
    public property_location_latitude:string="";
    public property_location_longitude:string="";
  }