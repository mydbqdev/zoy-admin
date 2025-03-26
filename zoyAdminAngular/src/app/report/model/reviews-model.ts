export class ReviewsModel{
    pgName : string = '';
    customerName : string = '';
    customerMobileNo : string = '';
    writtenReview : string = '';
    customerImage : string = '';
    reviewDate : string = '';
    overallRating : string = '';
    reviews:ReviewsReplyModel[]=[];                
}

class ReviewsReplyModel{
    customerId: string = '';
    customerName: string = '';
    customerImage: string = '';
    partnerId: string = '';
    partnerName: string = '';
    partnerImage: string = '';
    timeStamp: string = '';
    isEdited: boolean = false;
    review: string = '';
}