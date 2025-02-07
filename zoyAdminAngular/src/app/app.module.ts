import { NgModule } from '@angular/core';
import { BrowserModule, Meta, Title } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule }    from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MaterialModule } from './material.module';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { HttpClientModule,HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgxSpinnerModule } from 'ngx-spinner';
import { environment } from 'src/environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaskInputDirective } from './common/helpers/mask-input.directive';
import { AppAuthService } from './common/service/app-auth.service';
import { AppService } from './common/service/application.service';
import { AuthService } from './common/service/auth.service';
import { HttpInterceptorService } from './common/service/httpInterceptor.service';
import { UserService } from './common/service/user.service';
import { AlertDialogComponent } from './common/shared/alert-dialog/alert-dialog.component';
import { AlertDialogService } from './common/shared/alert-dialog/alert-dialog.service';
import { ConfirmationDialogComponent } from './common/shared/confirm-dialog/confirm-dialog.component';
import { ConfirmationDialogService } from './common/shared/confirm-dialog/confirm-dialog.service';
import { SafePipe } from './common/shared/pipe/safe-pipe';
import { BASE_PATH, defMenuEnable,BASE_PATH_EXTERNAL_SERVER, WEDSOCKET_BASE_PATH} from './common/shared/variables';
import { ForbiddenComponent } from './components/forbidden/forbidden.component';
import { PageNotFoundComponent } from './components/pagenotfound/pagenotfound.component';
import { SigninComponent } from './components/signin/signin.component';
import { MainPipe } from './main-pipe.module';
import { Routes, RouterModule } from '@angular/router';
import { UnderConstructionComponent } from './under-construction/under-construction.component';
import { ToastrModule } from 'ngx-toastr';
import { HomeComponent } from './components/home/home.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { HeaderComponent } from './components/header/header.component';
import { RoleMasterComponent } from './setting/role-master/components/role-master.component';
import { AppSettingMenuRoutingModule } from './setting/settings-menu-routing.module';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ReportListComponent } from './finance/reports/component/report-list.component';
import { AppOwnerMenuRoutingModule } from './owners/owners-menu-routing.model';
import { AppUsersMenuRoutingModule } from './user-management/users-menu-routing.module';
import { PermissionApprovalComponent } from './user-management/permission-approval/component/permission-approval.component';
import { UserMasterComponent } from './user-management/user-master/components/user-master.component';
import { ManageOwnerComponent } from './owners/managing-owner/components/managing-owner.component';
import { ZoyCodeComponent } from './owners/zoy-code/components/zoy-code.component';
import { ProfileComponent } from './profile/profile/profile.component';
import { ActivityLogComponent } from './profile/activity-log/activity-log.component';
import { SettingsComponent } from './profile/settings/settings.component';
import { TicketsComponent } from './supports/tickets/tickets.component';
import { TenantMenuRoutingModule } from './tenants/tenents-menu-routing.module';
import { BulkUploadComponent } from './tenants/bulk-upload/component/bulk-upload.component';
import { PaymentApprovalComponent } from './finance/payment-appoval/component/payment-approval.component';
import { OwnerDetailsComponent } from './owners/owner-details/components/managing-owner-details.component';
import { DbMasterConfigurationComponent } from './setting/db-master-configuration/components/db-master-configuration.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { DoughnutNgxChartComponent } from './chart/doughnut-ngx';
import { BarNgxChartComponent } from './chart/barchart-ngx';
import { DocumentUploadComponent } from './external/components/document-upload.component';
import { AppDocumentUploadRoutingModule } from './external/document-upload-routing.module';
import { ConfigurationMasterComponent } from './setting/configuration-master/components/configuration-master.component';
import { UserAuditComponent } from './setting/user-audit/components/user-audit.component';
import { LineNgxChartComponent } from './chart/linechart-home-ngx';
import { AreaNgxChartComponent } from './chart/areachart-home-ngx';
import { LockedUserComponent } from './user-management/locked-user/component/locked-user.component';
import { TenantsComponent } from './tenants/tenants/component/tenants.component';
import { TenantProfileComponent } from './tenants/tenant-profile/component/tenant-profile.component';
import { AlertNotificationComponent } from './alert-notification/component/alert-notification.component';
import { AppNotificationRoutingModule } from './alert-notification/notification-routing.module';
import { OrganizationInfoConfigComponent } from './setting/organization-info-config/components/organization-info-config.component';


const appRoutes: Routes = [
  {path:'**',component:PageNotFoundComponent,pathMatch:'full'}
];

@NgModule({
  declarations: [
    SafePipe,
    AppComponent,
    ConfirmationDialogComponent,
    AlertDialogComponent,
    ForbiddenComponent,
    MaskInputDirective,
    SigninComponent,
    UnderConstructionComponent,
    HomeComponent,
    SidebarComponent,
    HeaderComponent,
    ReportListComponent,
    UserMasterComponent,
    RoleMasterComponent,
    ForgotPasswordComponent,
    PermissionApprovalComponent,
    LockedUserComponent,
    ZoyCodeComponent,
    ManageOwnerComponent,
    ProfileComponent,
    ActivityLogComponent,
    SettingsComponent,
    TicketsComponent,
    TenantsComponent,
    TenantProfileComponent,
    BulkUploadComponent,
    PaymentApprovalComponent,
    OwnerDetailsComponent,
    DbMasterConfigurationComponent,
    DoughnutNgxChartComponent,
    BarNgxChartComponent,
    LineNgxChartComponent,
    AreaNgxChartComponent,
    DocumentUploadComponent,
    OrganizationInfoConfigComponent,
    ConfigurationMasterComponent,
    UserAuditComponent,
    AlertNotificationComponent,
   
  ],
  imports: [
    RouterModule.forRoot(appRoutes,
      {useHash:true,enableTracing:true} // debugging purposes only
      ),
    BrowserModule,
    AppRoutingModule,
    AppOwnerMenuRoutingModule,
    AppUsersMenuRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    MaterialModule,
    MainPipe,
    NgxSpinnerModule.forRoot(),
    ToastrModule.forRoot(),
    NgMultiSelectDropDownModule.forRoot(),
    DragDropModule,
    AppSettingMenuRoutingModule,
    TenantMenuRoutingModule,
    NgxChartsModule,
    AppDocumentUploadRoutingModule,
    AppNotificationRoutingModule,
    
    ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpInterceptorService,
      multi: true
    },
    {provide:AuthService,useClass:AppAuthService},
    {provide:AppService,useClass:AppService},
    {provide:UserService,useClass:UserService},
    {provide:BASE_PATH,useValue:environment.basePath},
    {provide:BASE_PATH_EXTERNAL_SERVER,useValue:environment.basePathExternal},
    {provide:defMenuEnable,useValue:environment.defMenuEnable},
    {provide:WEDSOCKET_BASE_PATH,useValue:environment.websocketBasePath},
    ConfirmationDialogService,AlertDialogService,
    Title,Meta
  ],
  
  bootstrap: [AppComponent],
})
 
export class AppModule { }
declare global { interface Navigator { msSaveBlob: (blob: Blob, fileName: string) => boolean } }
