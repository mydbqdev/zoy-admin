export class PostSearchResult {
	postId: string;
	userId: string;
    creatorName:string;
    zipCode:string;
    description:string;
    likeCount:number;
    isLiked:boolean;
    commentsCount:number;
    createdAt:string;
    updatedAt:string;
    mediaDetails:MediaDetails[]=[];

    //listing objects
    listingId:string;
    category:string;
    title:string;
    creatorId:string;
    free:boolean;
    price:number;
    condition:string;
    discount:boolean;
    discountAmount:number;
    pickupLocation:string;
    mediaPaths:MediaDetails[]=[];

    //ads 
    hyperLink:string;//post ads
    privacy:string;
    adsHyperLink:string;
    moreContent:boolean=false;
}

export class MediaDetails {
	contentType: string;
	type: string;
    url:string; 
}