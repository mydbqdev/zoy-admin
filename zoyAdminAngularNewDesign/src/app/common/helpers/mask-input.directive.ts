import { Directive, HostListener } from "@angular/core";

@Directive({
    selector: '[appMaskInput]',
  })
  export class MaskInputDirective {
    @HostListener('input', ['$event'])
    onInput(event: any) {
      const input = event.target as HTMLInputElement;
      const maskedValue = input.value.replace(/[a-zA-Z0-9]/g, '•');
      input.value = maskedValue;
    }
  }