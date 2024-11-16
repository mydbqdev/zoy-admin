import { RoleScreenModel } from "../../../common/models/rolescreen-model";
import { RoleScreenPrv } from "./role-screen-model";

export class RoleModel {

    public id : number;
    public roleName : string;
    public desc : string;

  
    dataread : string[];
    datawrite : string[];


    roleScreen :RoleScreenPrv[]=[];

    roleScreenList : RoleScreenModel;
   
}
