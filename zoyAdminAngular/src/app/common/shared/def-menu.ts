import { AccountSubMenu } from './account-sub-menu';
import { AdminReportSubMenu } from './admin-reports-sub-menu';
import { AdminSubMenu } from './admin-sub-menu';
import { ApprovalSubMenu } from './approvals-sub-menu';
import { ConfigurationSettingsSubMenu } from './configuration-settings-sub-menu';
import { OwnerManagementSubMenu } from './owner-management-sub-menu';
import { PersonalSubMenu } from './personal-sub-menu';


export class DefMenu{

    public ownerManagement:boolean;
    public ownerManagementSubMenu:OwnerManagementSubMenu;
    public userManagement:boolean;
    public userManagementSubMenu:OwnerManagementSubMenu;
    public financialManagement:boolean;
    public financialManagementSubMenu:OwnerManagementSubMenu;
    public configurationSettings:boolean;
    public configurationSettingsSubMenu: ConfigurationSettingsSubMenu;

    public reports:boolean;
    public reportsSubMenu:OwnerManagementSubMenu;

    public owners:boolean;
    public ownersSubMenu:OwnerManagementSubMenu;
    

    // public personalMenu:boolean;
    // public personalSubMenu:PersonalSubMenu;
    // public accountMenu:boolean;
    // public accountSubMenu:AccountSubMenu;
    // public adminMenu:boolean;
    // public adminSubMenu:AdminSubMenu;
    // public adminreportMenu:boolean;
    // public adminReportSubMenu:AdminReportSubMenu;
    // public approvalMenu:boolean;
    // public approvalSubMenu:ApprovalSubMenu;
    // public settingsMenu:boolean;
    } 