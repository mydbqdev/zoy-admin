export class VendorPaymentsModel {

    ownerId?: string;
    ownerName?: string;
    pgId?: string;
    pgName?: string;
    totalAmountFromTenants?: number;  
    amountPaidToOwner?: number;       
    zoyCommission?: number;          
    transactionDate?: string;        
    transactionNumber?: string;
    paymentStatus?: string;

}