export class TenantDetailPortfolio{
    profile : Profile = new Profile();
    activeBookings :ActiveBooking = new ActiveBooking();
    closedBookings :ClosedAndUpcomingBookings[] = [];
    upcomingBookings :ClosedAndUpcomingBookings[] = [];
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

    resoan: string = '';
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

class ClosedAndUpcomingBookings{
    bookingId: string = '';
    propertyName: string = '';
    bedNumber: string = '';
    bookingDate: string = '';
    cancellationDate: string = '';
    monthlyRent: string = '';
    securityDeposit: string = '';
    propertyAddress: string = '';
    propertyContactNumber: string = '';
    bookingStatus: string = '';
    securityDepositStatus : string = '';
}
