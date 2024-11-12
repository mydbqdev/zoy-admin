import { UserRoleModel } from "./user-role-model";

 
export class UserDetails{
    userEmail ?:string="" ;
    firstName ?:string="";
    lastName ?:string="";
    contactNumber:number;
    password?:string="";
    repeatPassword?:string="";
    designation ?:string="";
    status?:boolean=false;
    roleModel?:UserRoleModel[]=[];
 
}