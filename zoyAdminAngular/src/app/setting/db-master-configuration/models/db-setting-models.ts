
export class settingTypeObjClmApiDetailsModel{
    type : string ;
    columns : string[] ;
    api : string  ;
}

export class DbSettingDataModel{
    share_id ?: string  ;
    share_type ?: string  ;
    share_occupancy_count ?: string  ;

    room_type_id ?: string  ;
    room_type_name ?: string  ;

    cycle_first: string; 
    cycle_second: string;

    cycle_id ?: string  ;
    cycle_name ?: string  ;

    pg_type_id ?: string  ;
    pg_type_name ?: string  ;

    notification_mode_id ?:string ;
    notification_mod_name ?:string ;

    factor_id ?:string ;
    factor_name ?:string ;

    due_type_id ?: string;
    due_name ?: string;
    due_type_upload ?: string;
    due_image ?: string;
    due_status ?:boolean;

    currency_id ?: string;
    currency_name ?: string;

    billing_type_id ?: string;
    billing_type_name ?: string;

    ameneties_id ?: string;
    ameneties_name ?: string;
    ameneties_upload ?: string;
    ameneties_image ?: string;
    special_amenity ?:boolean;

    floor_name_id ?:string;
    floor_name ?:string;

    rentalAgreementDocId ?: string;
    rentalAgreementDoc ?: string;
    uploadedAt?: string;
}

export class DbSettingSubmitDataModel{

    id ?:string ;

    shareType ?:string ;
    shareOccupancyCount ?:string ;

    roomTypeName ?:string ;

    rentCycleName ?:string ;

    pgTypeName ?:string ;

    notificationModeName ?:string ;

    factorName ?:string ;

    dueTypeName ?: string;

    currencyName ?: string;

    billingTypeName ?: string;

    ameneties ?: string;

    floorName ?:string;

    specialAmenity ?:boolean;

}
export class GenderTypesModel{
    gender_id:string;
    gender_name:string;
    checked :boolean=false;
}
