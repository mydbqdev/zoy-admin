export class FileUploadModel {
    data: File;
    state: string;
    inProgress: boolean;
    progress: number;
    canRetry: boolean;
    canCancel: boolean;
  	sub: any;
    
  }