// Main Data Model
export class PgOwnerData {
    profile: Profile;
    pg_ownerbasic_information: PgOwnerBasicInformation;
    pg_owner_property_information: PgOwnerPropertyInformation[]=[];
    pg_owner_business_info: PgOwnerBusinessInfo[]=[];
  }

// Profile Model
export class Profile {
    owner_name: string;
    owner_i_d: string;
    profile_photo: string;
    status: string;
    reason:string;
  }
  
  // PG Owner Basic Information Model
  export class PgOwnerBasicInformation {
    first_name: string;
    last_name: string;
    email: string;
    contact: string;
    address: string;
    aadhar: string;
  }

   // PG Owner Business Info Model
   export class PgOwnerBusinessInfo {
    account_number: string;
    ifsc_code: string;
    account_type: string;
    bank_name :string;
    bank_branch:string;
  }
  
  // PG Owner Property Information Model
  export class PgOwnerPropertyInformation {
    property_id:string;
    property_name: string;
    status: string;
    basic_property_information: BasicPropertyInformation;
    pg_owner_additional_info: PgOwnerAdditionalInfo;
    floor_information: FloorInformation[];
    zoy_fixed_share:string;
    zoy_variable_share:string;
  }
  
  // Basic Property Information Model
  export class BasicPropertyInformation {
    pg_type: string;
    pg_address: string;
    manager_name: string;
    manager_contact: string;
    manager_emailid: string;
    number_of_floors: string;
    total_occupancy: string;
    gst_number: string;
    cin: string;
  }
  
  // PG Owner Additional Info Model
  export class PgOwnerAdditionalInfo {
    security_deposit: string;
    notice_period: string;
    rent_cycle: string;
    late_payment_fee: string;
    additional_charges: string;
    grace_period: string;
    agreement_charges: string;
    ekyc_charges: string;
    property_description: string;
  }
  
  // Floor Information Model
  export class FloorInformation {
    floor_id:string
    floor_name: string;
    total_rooms: string;
    total_occupancy: string;
    occupied: string;
    vacant: string;
    rooms: Room[];
  }
  
  // Room Model
  export class Room {
    room_no: string;
    number_of_beds: string;
    beds_available: string;
    beds : Beds[];
  }

   // Beds Model
   export class Beds {
    bed_id: string;
    bed_name: string;
    availability_status: string;
  }
  
  export class AadhaarVerif {
    aadhaar : string;
    sessionid : string;
    captcha : string;
    otp : string;
    session_id : string;
  }
  
  
  
  