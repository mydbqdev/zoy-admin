import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { HttpClient, HttpEventType, HttpRequest } from '@angular/common/http';
import { FileUploadModel } from '../model/file-upload.model';
import { last, map, tap } from 'rxjs';


@Component({
  selector: 'app-document-upload',
  templateUrl: './document-upload.component.html',
  styleUrl: './document-upload.component.css'
})
export class DocumentUploadComponent implements OnInit, AfterViewInit {
		@Input() text = "Upload";
		@Input() param = "file";
		@Input() target = "https://file.io";
		@Input() accept = "image/*";
		@Output() complete = new EventEmitter<string>();
        private files: Array<FileUploadModel> = [];
		
	  constructor( private http: HttpClient) {}

	  ngOnDestroy() {
	  }
	  ngOnInit() {
	  }
	  ngAfterViewInit() {
	  }
	 
	  uploaded = false;
	  submit() {
		this.uploaded = true;
		}

	  onDrop(files: FileList) {
		for (let index = 0; index < files.length; index++) {
		  const file = files[index];
		  this.uploadFiles();
		}
	  }
	

	  onClick() {
		const fileUpload = document.getElementById(
		  "fileUpload"
		) as HTMLInputElement;
		fileUpload.onchange = () => {
		  for (let index = 0; index < fileUpload.files.length; index++) {
			const file = fileUpload.files[index];
		  }
		  this.uploadFiles();
		};
		fileUpload.click();
	  }
	  cancelFile(file: FileUploadModel) {
		file.sub.unsubscribe();
		this.removeFileFromArray(file);
	  }
	  retryFile(file: FileUploadModel) {
		this.uploadFile(file);
		file.canRetry = false;
	  }
	  private uploadFile(file: FileUploadModel) {
		const fd = new FormData();
		fd.append(this.param, file.data);
	
		const req = new HttpRequest("POST", this.target, fd, {
		  reportProgress: true
		});
		file.inProgress = true;
		file.sub = this.http
		  .request(req)
		  .pipe(
			map(event => {
			  switch (event.type) {
				case HttpEventType.UploadProgress:
				  file.progress = Math.round((event.loaded * 100) / event.total);
				  break;
				case HttpEventType.Response:
				  return event;
			  }
			}),
			tap(message => {}),
			last(),
		  )
		  .subscribe((event: any) => {
			if (typeof event === "object") {
			  this.removeFileFromArray(file);
			  this.complete.emit(event.body);
			}
		  });
	  }
	
	  private uploadFiles() {
		const fileUpload = document.getElementById(
		  "fileUpload"
		) as HTMLInputElement;
		fileUpload.value = "";
	
		this.files.forEach(file => {
		  this.uploadFile(file);
		});
	  }
	
	  private removeFileFromArray(file: FileUploadModel) {
		const index = this.files.indexOf(file);
		if (index > -1) {
		  this.files.splice(index, 1);
		}
	  }
  }  