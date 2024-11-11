import { RoleModel } from "../../role-master/models/role-model";

 
export class UserDetails{
    userEmail ?:string="" ;
    firstName ?:string="";
    lastName ?:string="";
    contactNumber:number;
    password?:string="";
    repeatPassword?:string="";
    designation ?:string="";
    status?:boolean=false;
    roleModel?:RoleModel[]=[];
 
}