
export enum VendorStatus {
  PendingApproval = 'Pending Approval',
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