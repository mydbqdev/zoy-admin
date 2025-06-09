package com.integration.zoy.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "properties", schema = "pgsales")
@Data
public class Property {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "property_id")
	private String propertyId;

	@ManyToOne
	@JoinColumn(name = "owner_id", referencedColumnName = "owner_id", nullable = false)
	private PgOwner owner;

	@Column(name = "sales_id")
	private String salesId;

	@Column(name = "category")
	private String category;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "who_can_stay", nullable = false)
	private String whoCanStay;

	@Column(name = "floor_count", nullable = false)
	private Integer floorCount;

	@Column(name = "room_count", nullable = false)
	private Integer roomCount;

	@Column(name = "total_occupancy", nullable = false)
	private Integer totalOccupancy;

	@Column(name = "current_occupancy")
	private Integer currentOccupancy = 0;

	@Column(name = "pincode", nullable = false)
	private String pincode;

	@Column(name = "state", nullable = false)
	private String state;

	@Column(name = "city", nullable = false)
	private String city;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "locality")
	private String locality;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@Column(name = "pg_master_amenities_id")
	private String pgMasterAmenitiesId;

	@Column(name = "pg_registration_status")
	private Boolean pgRegistrationStatus;

	@Column(name = "pg_property_deposit")
	private BigDecimal pgPropertyDeposit;

	@Column(name = "pg_property_food_charges")
	private BigDecimal pgPropertyFoodCharges;

	@Column(name = "pg_property_electricity_charges_status")
	private String pgPropertyElectricityChargesStatus;

	@Column(name = "pg_property_elecrticity_charges")
	private BigDecimal pgPropertyElectricityCharges;

	@Column(name = "pg_property_other_utilities")
	private String pgPropertyOtherUtilities;

	@Column(name = "pg_property_rental_duration_start_date")
	private Timestamp rentalStartDate;

	@Column(name = "pg_property_rental_duration_end_date")
	private Timestamp rentalEndDate;

	@Column(name = "electricity_bill")
	private BigDecimal electricityBill;

	@Column(name = "water_charges")
	private BigDecimal waterCharges;

	@Column(name = "house_keeping")
	private BigDecimal houseKeeping;

	@Column(name = "security_staff")
	private BigDecimal securityStaff;

	@Column(name = "chef_groceries")
	private BigDecimal chefGroceries;

	@Column(name = "maintenance_charges")
	private BigDecimal maintenanceCharges;

	@Column(name = "miscellaneous")
	private BigDecimal miscellaneous;

	@Column(name = "additional_amenities")
	private String additionalAmenities;

	@Column(name = "property_tax_receipt")
	private Boolean propertyTaxReceipt;

	@Column(name = "local_rental_law_compliance")
	private Boolean localRentalLawCompliance;

	@Column(name = "fire_safety_compliance")
	private Boolean fireSafetyCompliance;

	@Column(name = "emergency_exit")
	private Boolean emergencyExit;

	@Column(name = "smoke_detectors")
	private Boolean smokeDetectors;

	@Column(name = "photo_video_collected")
	private Boolean photoVideoCollected;

	@Column(name = "physical_inspection_date")
	private Timestamp physicalInspectionDate;

	@Column(name = "current_problem_faced")
	private String currentProblemFaced;

	@Column(name = "bbmp_other_municipal_issue")
	private String bbmpOtherMunicipalIssue;

	@Column(name = "area_specific_complaints")
	private String areaSpecificComplaints;

	@Column(name = "feedback")
	private String feedback;

	@Column(name = "building_lease_amount")
	private BigDecimal buildingLeaseAmount;

	@Column(name = "wifi_internet_charges")
	private BigDecimal wifiInternetCharges;

	@Column(name = "camera_charges")
	private BigDecimal cameraCharges;

	@Column(name = "router_charges")
	private BigDecimal routerCharges;

	@Column(name = "property_location_time_capture")
	private Timestamp propertyLocationTimeCapture;

	@Column(name = "property_location_latitude", precision = 10, scale = 7)
	private BigDecimal propertyLocationLatitude;

	@Column(name = "property_location_longitude", precision = 10, scale = 7)
	private BigDecimal propertyLocationLongitude;

	@Column(name = "live_location_latitude", precision = 10, scale = 7)
	private BigDecimal liveLocationLatitude;

	@Column(name = "live_location_longitude", precision = 10, scale = 7)
	private BigDecimal liveLocationLongitude;

	// === Basic PG Standards ===
	@Column(name = "daily_cleaning")
	private boolean dailyCleaning;

	@Column(name = "weekly_deep_cleaning")
	private boolean weeklyDeepCleaning;

	@Column(name = "high_touch_disinfection")
	private boolean highTouchDisinfection;

	@Column(name = "regular_pest_control")
	private boolean regularPestControl;

	@Column(name = "well_ventilated_rooms")
	private boolean wellVentilatedRooms;

	@Column(name = "functional_beds_and_storage")
	private boolean functionalBedsAndStorage;

	@Column(name = "weekly_bed_linen_change")
	private boolean weeklyBedLinenChange;

	@Column(name = "working_fans_lights_charging")
	private boolean workingFansLightsCharging;

	@Column(name = "adequate_bathroom_ratio")
	private boolean adequateBathroomRatio;

	@Column(name = "continuous_water_supply")
	private boolean continuousWaterSupply;

	@Column(name = "proper_drainage_ventilation")
	private boolean properDrainageVentilation;

	@Column(name = "daily_cleaned_toilets")
	private boolean dailyCleanedToilets;

	@Column(name = "hot_water_availability")
	private boolean hotWaterAvailability;

	@Column(name = "hygienic_kitchen")
	private boolean hygienicKitchen;

	@Column(name = "freshly_cooked_meals")
	private boolean freshlyCookedMeals;

	@Column(name = "ro_filtered_cooking_water")
	private boolean roFilteredCookingWater;

	@Column(name = "weekly_menu_variety")
	private boolean weeklyMenuVariety;

	@Column(name = "timely_food_serving")
	private boolean timelyFoodServing;

	@Column(name = "secure_entrance")
	private boolean secureEntrance;

	@Column(name = "security_guard_or_biometric")
	private boolean securityGuardOrBiometric;

	@Column(name = "visitor_log_and_guest_approval")
	private boolean visitorLogAndGuestApproval;

	@Column(name = "fire_safety_equipment")
	private boolean fireSafetyEquipment;

	@Column(name = "power_backup")
	private boolean powerBackup;

	@Column(name = "high_speed_internet")
	private boolean highSpeedInternet;

	@Column(name = "functional_water_and_drainage")
	private boolean functionalWaterAndDrainage;

	@Column(name = "regular_maintenance")
	private boolean regularMaintenance;

	@Column(name = "tenant_kyc_records")
	private boolean tenantKycRecords;

	@Column(name = "signed_agreement_or_policy")
	private boolean signedAgreementOrPolicy;

	@Column(name = "fire_noc_and_trade_license")
	private boolean fireNocAndTradeLicense;

	@Column(name = "local_pg_regulation_compliance")
	private boolean localPgRegulationCompliance;

	@Column(name = "guest_policy")
	private boolean guestPolicy;

	@Column(name = "noise_level_limits")
	private boolean noiseLevelLimits;

	@Column(name = "alcohol_smoking_rules")
	private boolean alcoholSmokingRules;

	@Column(name = "curfew_timings")
	private boolean curfewTimings;

	@Column(name = "common_area_guidelines")
	private boolean commonAreaGuidelines;

	@Column(name = "complaint_logging_system")
	private boolean complaintLoggingSystem;

	@Column(name = "prompt_issue_resolution")
	private boolean promptIssueResolution;

	@Column(name = "monthly_maintenance_checks")
	private boolean monthlyMaintenanceChecks;

	@Column(name = "tenant_communication")
	private boolean tenantCommunication;

	@Column(name = "respectful_grievance_handling")
	private boolean respectfulGrievanceHandling;

	@Column(name = "regular_feedback_collection")
	private boolean regularFeedbackCollection;

	@Column(name = "basic_note", length = 500)
	private String basicNote;

	// === Luxury PG Standards ===

	@Column(name = "spacious_airconditioned_rooms")
	private boolean spaciousAirconditionedRooms;

	@Column(name = "premium_beds_and_mattresses")
	private boolean premiumBedsAndMattresses;

	@Column(name = "ergonomic_furniture")
	private boolean ergonomicFurniture;

	@Column(name = "private_balconies_or_windows")
	private boolean privateBalconiesOrWindows;

	@Column(name = "smart_lighting_and_charging")
	private boolean smartLightingAndCharging;

	@Column(name = "attached_bathrooms_branded_fittings")
	private boolean attachedBathroomsBrandedFittings;

	@Column(name = "twentyfour_seven_hot_water")
	private boolean twentyfourSevenHotWater;

	@Column(name = "daily_cleaning_and_towel_restock")
	private boolean dailyCleaningAndTowelRestock;

	@Column(name = "premium_bathroom_features")
	private boolean premiumBathroomFeatures;

	@Column(name = "multi_cuisine_rotating_menus")
	private boolean multiCuisineRotatingMenus;

	@Column(name = "custom_meal_options")
	private boolean customMealOptions;

	@Column(name = "breakfast_dinner_included")
	private boolean breakfastDinnerIncluded;

	@Column(name = "trained_chef_hygiene")
	private boolean trainedChefHygiene;

	@Column(name = "pantry_appliances")
	private boolean pantryAppliances;

	@Column(name = "daily_housekeeping")
	private boolean dailyHousekeeping;

	@Column(name = "weekly_deep_cleaning_and_laundry")
	private boolean weeklyDeepCleaningAndLaundry;

	@Column(name = "linen_change_every_3_days")
	private boolean linenChangeEvery3Days;

	@Column(name = "professional_cleaning_staff")
	private boolean professionalCleaningStaff;

	@Column(name = "twentyfour_seven_security")
	private boolean twentyfourSevenSecurity;

	@Column(name = "biometric_or_smart_card_entry")
	private boolean biometricOrSmartCardEntry;

	@Column(name = "cctv_common_areas")
	private boolean cctvCommonAreas;

	@Column(name = "fire_alarms_and_exits")
	private boolean fireAlarmsAndExits;

	@Column(name = "high_speed_wifi")
	private boolean highSpeedWifi;

	@Column(name = "smart_tvs_with_ott")
	private boolean smartTvsWithOtt;

	@Column(name = "app_based_complaints")
	private boolean appBasedComplaints;

	@Column(name = "motion_sensor_lights")
	private boolean motionSensorLights;

	@Column(name = "gym_or_fitness")
	private boolean gymOrFitness;

	@Column(name = "rooftop_or_lounge")
	private boolean rooftopOrLounge;

	@Column(name = "coworking_space")
	private boolean coworkingSpace;

	@Column(name = "entertainment_zones")
	private boolean entertainmentZones;

	@Column(name = "inhouse_laundry_service")
	private boolean inhouseLaundryService;

	@Column(name = "digital_onboarding_ekyc")
	private boolean digitalOnboardingEkyc;

	@Column(name = "license_agreements")
	private boolean licenseAgreements;

	@Column(name = "guest_policy_defined")
	private boolean guestPolicyDefined;

	@Column(name = "rent_refund_transparency")
	private boolean rentRefundTransparency;

	@Column(name = "handyman_support")
	private boolean handymanSupport;

	@Column(name = "monthly_pest_and_appliance")
	private boolean monthlyPestAndAppliance;

	@Column(name = "concierge_or_manager")
	private boolean conciergeOrManager;

	@Column(name = "monthly_community_events")
	private boolean monthlyCommunityEvents;

	@Column(name = "feedback_suggestion_channels")
	private boolean feedbackSuggestionChannels;

	@Column(name = "welcome_kits")
	private boolean welcomeKits;

	@Column(name = "tenant_app_features")
	private boolean tenantAppFeatures;

	@Column(name = "luxury_note", length = 500)
	private String luxuryNote;

}