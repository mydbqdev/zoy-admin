import { RoleScreenModel } from "../../../common/models/rolescreen-model";

export class RoleModel {

    public id : number;
    public roleName : string;
    public description : string;

  
    dataread : string[];
    datawrite : string[];

    roleScreen : RoleScreenModel;
   
}