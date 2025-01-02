export class TenantDetailPortfolio{
    profile : Profile = new Profile();
    activeBookings :ActiveBooking = new ActiveBooking();

}

class Profile {
    tenantName: string = '';
    contactNumber: string = '';
    userEmail: string = '';
    status: string = '';
    ekycStatus: string = '';
    registeredDate: string = '';
    currentPropertyName: string = '';
    emergencyContactNumber: string = '';
    alternatePhone: string = '';
    tenantType: string = '';
    gender: string = '';
    dateOfBirth: string = '';
    bloodGroup: string = '';
    fatherName: string = '';
    currentAddress: string = '';
    permanentAddress: string = '';
    nationality: string = '';
    motherTongue: string = '';
    userProfile: string = '';
}

class ActiveBooking {
    pgName: string = '';
    monthlyRent: number = 0;
    securityDeposit: number = 0;
    totalDueAmount: number = 0;
    roomBedName: string = '';
    checkInDate: string = '';
    checkOutDate: string = '';
    rentCycle: string = '';
    noticePeriod: string = '';
}