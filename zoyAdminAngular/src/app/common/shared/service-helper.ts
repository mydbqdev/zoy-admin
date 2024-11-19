import { HttpHeaders } from '@angular/common/http';

export class ServiceHelper{
    public static buildHeaders(){
        let headers:HttpHeaders = new HttpHeaders({
            'Accept':'application/json',
            'Content-Type':'application/json',
            'Access-Control-Allow-Origin': '*'
        });

        return headers;
    }
    public static filesHeaders(){
        let headers:HttpHeaders = new HttpHeaders({
            'Accept':'application/json',
            'Content-Type':'multipart/form-data',
            'Access-Control-Allow-Origin': '*'
        });

        return headers;
    }

    public static buildImportHeaders(){
        let headers:HttpHeaders = new HttpHeaders({
            'Cache-Control':'no-cache',
            'Content-Type':'application/octet-stream',
            'Access-Control-Allow-Origin': '*'
        });

        return headers;
    }

    public static buildHeadersAuth(pwd:string){
        let headers:HttpHeaders = new HttpHeaders({
            'Accept':'application/json',
            'Content-Type':'application/json',
            'Access-Control-Allow-Origin': '*',
            'DBQ-TOKEN':pwd
        });

        return headers;
    }

    public static buildHeadersBolb(){
        let headers = new HttpHeaders({'Content-Type':'application/octet-stream'});
        let reOption:any ={headers:headers,responseType:'blob' as 'json'};
        return reOption;
    }

    public static buildHeadersForPdf(){
        let headers = new HttpHeaders();
        headers.append('Accept','application/pdf');
        let reOption:any ={headers:headers,responseType:'blob' as 'json'};


        return reOption;
    }

    public static buildHeadersBasicAuth(): HttpHeaders {
        const username = 'zoyadmin';  
        const password = 'zoyadminpass';
        const basicAuthValue = `Basic ${btoa(username + ':' + password)}`;  
        
        let headers: HttpHeaders = new HttpHeaders({
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          'Authorization': 'Basic em95YWRtaW46em95YWRtaW5wYXNz',
          'Access-Control-Allow-Origin': '*'  
        });
        return headers;
      }
}