
export enum VendorStatus {
  PendingApproval = 'Pending for approval',
  Approved = 'Approved',
  Rejected = 'Rejected',
  Inactive = 'Inactive',
  Active = 'Active' // Status after approval
}

export class Vendor {
  id: string;
  companyName: string;
  contactPersonName: string;
  email: string;
  address: string;
  contactNumber: string;
  alternativeNumber?: string;
  servicesOffered: string[];
  gstRegistrationNumber?: string;
  idAttachmentUrl?: string; // Optional ID attachment URL
  status: VendorStatus;
  createdAt: Date;
  updatedAt?: Date;
  rejectionReason?: string;
  inactiveReason?: string;
}

export class VendorServiceModel {
  vendor_service_id: string = '';
  service_name: string = '';
}

export class VendorModel {
  vendor_id: string = '';
  vendor_company_name: string = '';
  vendor_first_name: string = '';
  vendor_last_name: string = '';
  vendor_address: string = '';
  vendor_mobile_no: string = '';
  vendor_alternative_no: string = '';
  vendor_email: string = '';
  vendor_gst_registrastion_no: string = '';
  vendor_status: string = '';
  services: VendorServiceModel[] = [];
  vendor_file_paths: string[] = [];
  inactiveReason: string = '';
  rejectionReason: string = '';
}
