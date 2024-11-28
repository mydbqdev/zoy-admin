import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DocumentUploadComponent } from './components/document-upload.component';


const routes: Routes = [
  { path: 'document-upload', component: DocumentUploadComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppDocumentUploadRoutingModule { }