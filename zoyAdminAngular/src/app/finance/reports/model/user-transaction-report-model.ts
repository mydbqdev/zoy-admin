export class UserTransactionReportModel {
    customerId : string='' ;
    transactionDate : string='' ;
    transactionNumber? :string;
    transactionStatus? :string;
    baseAmount: number = 0;
    gstAmount: number = 0;
    totalAmount: number = 0;
    customerName : string='' ;
    PgPropertyName : string='' ;
    PgPropertyId : string='' ;
    bedNumber : string='' ;
    category : string='' ;
    paymentMethod : string='' ;
}
