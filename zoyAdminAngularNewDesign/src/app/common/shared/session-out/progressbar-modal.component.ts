import {Component, Input} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'progressbar-modal-comp',
  template: `
    <div class="modal-header">
      <h4 class="modal-title" style="color:red;">Session Timeout Warning</h4>
    </div>
    <div class="modal-body">
      You will be session out in {{(countMinutes !== 0 ? + countMinutes+' Minute'+(countMinutes > 1 ? 's ' : ' ') : '') + countSeconds+' Seconds'}}

    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-primary" (click)="continue()">Continue</button>
      <button type="button" class="btn btn-danger" (click)="logout()">Logout</button>
    </div>
  `
})
export class ProgressBarModalComponent {

  @Input() countMinutes: number;
  @Input() countSeconds: number;
  @Input() progressCount: number;

  constructor(public activeModal: NgbActiveModal) {
  }
  continue() {
    this.activeModal.close(null);
  }
  logout() {
    this.activeModal.close('logout');
  }
}
