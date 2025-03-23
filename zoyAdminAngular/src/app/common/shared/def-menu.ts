import { FinancesSubMenu } from "./finances-sub-menu";
import { OwnersSubMenu } from "./owners-sub-menu";
import { ReportsSubMenu } from "./reports-sub-menu";
import { SettingsSubMenu } from "./settings-sub-menu";
import { SupportsSubMenu } from "./supports-sub-menu";
import { TenantsSubMenu } from "./tenants-sub-menu";
import { UsersSubMenu } from "./users-sub-menu";

export class DefMenu{
    public owners : boolean;
    public ownersSubMenu : OwnersSubMenu;
    public users : boolean ;
    public usersSubmenu : UsersSubMenu;
    public tenants : boolean;
    public tenantsSubMenu : TenantsSubMenu;
    public finances : boolean;
    public financesSubMenu : FinancesSubMenu;
    public supports : boolean;
    public supportsSubMenu : SupportsSubMenu;
    public settings : boolean;
    public settingsSubMenu : SettingsSubMenu;
    public reports : boolean;
    public reportsSubMenu : ReportsSubMenu;
 

    } 


