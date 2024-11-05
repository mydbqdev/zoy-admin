import { AccountSubMenu } from './account-sub-menu';
import { AdminReportSubMenu } from './admin-reports-sub-menu';
import { AdminSubMenu } from './admin-sub-menu';
import { ApprovalSubMenu } from './approvals-sub-menu';
import { PersonalSubMenu } from './personal-sub-menu';


export class DefMenu{
    public personalMenu:boolean;
    public personalSubMenu:PersonalSubMenu;
    public accountMenu:boolean;
    public accountSubMenu:AccountSubMenu;
    public adminMenu:boolean;
    public adminSubMenu:AdminSubMenu;
    public adminreportMenu:boolean;
    public adminReportSubMenu:AdminReportSubMenu;
    public approvalMenu:boolean;
    public approvalSubMenu:ApprovalSubMenu;
    }