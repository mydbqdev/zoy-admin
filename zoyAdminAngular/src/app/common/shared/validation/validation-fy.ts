import { AbstractControl } from '@angular/forms';

export function ValidateFY(control: AbstractControl) {
        if(control.value==null || control.value==""){
            return { invalidFY: true };
        }else if(!control.value.includes('-')){
            return { invalidFY: true };
        }else{
        var val=control.value.split("-");
        let fy1=Number(val[0]);
        let fy2=Number(val[1]);
        if(String(val[0]).length!=4 || String(val[1]).length!=4 || fy1==NaN || fy2==NaN || fy2!=(fy1+1) || (fy1>fy2) ){
            return { invalidFY: true };
        }
        }
        return null;
}