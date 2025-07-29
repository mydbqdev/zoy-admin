import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable, of } from 'rxjs';
import { ServiceHelper } from 'src/app/common/shared/service-helper';
import { BASE_PATH } from 'src/app/common/shared/variables';
import { MessageService } from 'src/app/message.service';
import { Vendor, VendorStatus } from "../model/admin-vendor-management.model";

@Injectable({
  providedIn: 'root'
})
export class VendorService {
  message: string;
  httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  constructor(
    private http: HttpClient,
    private httpclient: HttpClient,
    @Inject(BASE_PATH) private basePath: string,
    private messageService: MessageService
  ) {}


  private apiUrl = 'YOUR_BACKEND_API_BASE_URL/api/vendors'; // **IMPORTANT: Replace with your actual backend API URL**
  private currentVendors: Vendor[] = [
    {
      id: 'v1', companyName: 'Alpha Solutions', contactPersonName: 'Alice Smith', email: 'alice@alpha.com',
      address: '123 Main St', contactNumber: '111-222-3333', servicesOffered: ['IT Support'],
      status: VendorStatus.PendingApproval, createdAt: new Date('2023-01-15T10:00:00Z'),
      idAttachmentUrl: 'https://placehold.co/600x400/FF0000/FFFFFF?text=ID+Attachment+1'
    },
    {
      id: 'v2', companyName: 'Beta Builders', contactPersonName: 'Bob Johnson', email: 'bob@beta.com',
      address: '456 Oak Ave', contactNumber: '444-555-6666', servicesOffered: ['Construction'],
      status: VendorStatus.Approved, createdAt: new Date('2023-02-01T11:30:00Z')
    },
    {
      id: 'v3', companyName: 'Gamma Graphics', contactPersonName: 'Charlie Brown', email: 'charlie@gamma.com',
      address: '789 Pine Ln', contactNumber: '777-888-9999', servicesOffered: ['Design'],
      status: VendorStatus.Rejected, createdAt: new Date('2023-03-10T14:00:00Z'), rejectionReason: 'Incomplete profile'
    },
    {
      id: 'v4', companyName: 'Delta Deliveries', contactPersonName: 'Diana Prince', email: 'diana@delta.com',
      address: '101 Elm St', contactNumber: '000-111-2222', servicesOffered: ['Logistics'],
      status: VendorStatus.Inactive, createdAt: new Date('2023-04-05T09:00:00Z'), inactiveReason: 'Seasonal closure'
    },
    {
      id: 'v5', companyName: 'Epsilon Events', contactPersonName: 'Eve Adams', email: 'eve@epsilon.com',
      address: '202 Birch Rd', contactNumber: '333-444-5555', servicesOffered: ['Event Planning'],
      status: VendorStatus.PendingApproval, createdAt: new Date('2023-05-20T16:00:00Z'),
      idAttachmentUrl: 'https://placehold.co/600x400/0000FF/FFFFFF?text=ID+Attachment+2'
    }
  ];



  getVendors(statusFilter?: VendorStatus): Observable<Vendor[]> {
    let filtered = [...this.currentVendors]; // Deep copy for manipulation
    if (statusFilter) {
      filtered = filtered.filter(v => v.status === statusFilter);
    }
    return of(filtered); // Simulate async operation
    // return this.http.get<Vendor[]>(this.apiUrl, { params: statusFilter ? { status: statusFilter } : {} });
  }

  // Simulate backend actions
  private updateVendorStatus(vendorId: string, newStatus: VendorStatus, reason?: string): Observable<any> {
    const vendorIndex = this.currentVendors.findIndex(v => v.id === vendorId);
    if (vendorIndex > -1) {
      const vendor = this.currentVendors[vendorIndex];
      vendor.status = newStatus;
      vendor.updatedAt = new Date();
      if (newStatus === VendorStatus.Rejected) {
        vendor.rejectionReason = reason;
        delete vendor.inactiveReason;
      } else if (newStatus === VendorStatus.Inactive) {
        vendor.inactiveReason = reason;
        delete vendor.rejectionReason;
      } else {
        delete vendor.rejectionReason;
        delete vendor.inactiveReason;
      }
      // Simulate email sending and audit logging here in a real backend
      console.log(`[MOCK] Vendor ${vendor.companyName} status changed to ${newStatus}. Reason: ${reason || 'N/A'}`);
      return of({ success: true, message: 'Status updated' });
    }
    return of({ success: false, message: 'Vendor not found' });
  }

  approveVendor(vendorId: string): Observable<any> {
    return this.updateVendorStatus(vendorId, VendorStatus.Active);
    // return this.http.post(`${this.apiUrl}/${vendorId}/approve`, {});
  }

  rejectVendor(vendorId: string, reason?: string): Observable<any> {
    return this.updateVendorStatus(vendorId, VendorStatus.Rejected, reason);
    // return this.http.post(`${this.apiUrl}/${vendorId}/reject`, { reason });
  }

  changeVendorStatus(vendorId: string, newStatus: VendorStatus, reason?: string): Observable<any> {
    return this.updateVendorStatus(vendorId, newStatus, reason);
    // return this.http.post(`${this.apiUrl}/${vendorId}/change-status`, { newStatus, reason });
  }


  public getLockedUserDetais(): Observable<any> {
    const url = this.basePath + 'zoy_admin/userListlocked'; 
    return  this.httpclient.get<any>(
      url,
      {
          headers:ServiceHelper.buildHeaders(),
         observe : 'body',
         withCredentials:true
      });
  }

  public doUnlock(data:any): Observable<any> {
    const url = this.basePath + 'zoy_admin/zoyAdminUserUnlock'; 
    return  this.httpclient.post<any>(
      url,
      data,
      {
          headers:ServiceHelper.buildHeaders(),
         observe : 'body',
         withCredentials:true
      });
  }

  private errorHandler(error: HttpErrorResponse) {
    return of(error.message || "server error");
  }

  private log(message: string) {
    this.messageService.add(`AuthService:${message}`, 'info');
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      this.log(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }
}
