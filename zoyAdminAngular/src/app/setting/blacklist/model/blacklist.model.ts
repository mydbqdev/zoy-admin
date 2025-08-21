export class BlacklistGetModel{
    public id: string | null = null;
    public email: string="";
    public mobile: string="";
}

export class BlacklistPostModel{
    public pg_blacklisted_id :string | null = null;
    public pg_blocklisted_email :string = '';
    public pg_blacklisted_mobile :string = '';
}

export class RequestParam {
    searchContent :string  = '';
    pageIndex :number = 0;
    pageSize :number  = 0;
}