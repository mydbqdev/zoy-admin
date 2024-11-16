export class ForgotPassword {
    email?: string = "";
    newPassword?: string = "";
    repeatPassword?: string = "";
    firstName?: string = "";
    lastName?: string = "";
    designation?: string = "";
    
    // Individual OTP fields
    otp1: string = '';
    otp2: string = '';
    otp3: string = '';
    otp4: string = '';
    otp5: string = '';
    otp6: string = '';
  
    // Full OTP (concatenated from individual digits)
    get otp(): string {
      return `${this.otp1}${this.otp2}${this.otp3}${this.otp4}${this.otp5}${this.otp6}`;
    }
  
    set otp(value: string) {
      // Optionally, split the OTP string into individual digits
      this.otp1 = value.charAt(0);
      this.otp2 = value.charAt(1);
      this.otp3 = value.charAt(2);
      this.otp4 = value.charAt(3);
      this.otp5 = value.charAt(4);
      this.otp6 = value.charAt(5);
    }
  }
  