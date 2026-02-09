package com.example.project1;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A temporary helper class to upload the service catalog to Firestore.
 */
public class CatalogUploader {

    private static final String TAG = "CatalogUpload";

    public static void uploadAllCategories(FirebaseFirestore db) {
        uploadElectricianCatalog(db);
        uploadCarpenterCatalog(db);
        uploadPainterCatalog(db);
        uploadWelderCatalog(db);
        uploadCleanerCatalog(db);
        uploadAcRepairCatalog(db);
        upload2wMechanicCatalog(db);
        upload4wMechanicCatalog(db);
        uploadEventHelperCatalog(db);
    }

    private static void uploadElectricianCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("electrician").set(getElectricianData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: Electrician catalog uploaded."));
    }
    private static void uploadCarpenterCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("carpenter").set(getCarpenterData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: Carpenter catalog uploaded."));
    }
    private static void uploadPainterCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("painter").set(getPainterData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: Painter catalog uploaded."));
    }
    private static void uploadWelderCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("welder").set(getWelderData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: Welder catalog uploaded."));
    }
    private static void uploadCleanerCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("cleaner").set(getCleanerData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: Cleaner catalog uploaded."));
    }
    private static void uploadAcRepairCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("ac repair").set(getAcRepairData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: AC Repair catalog uploaded."));
    }
    private static void upload2wMechanicCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("2w mechanic").set(get2wMechanicData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: 2W Mechanic catalog uploaded."));
    }
    private static void upload4wMechanicCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("4w mechanic").set(get4wMechanicData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: 4W Mechanic catalog uploaded."));
    }
    private static void uploadEventHelperCatalog(FirebaseFirestore db) {
        db.collection("service_catalog").document("event helper").set(getEventHelperData()).addOnSuccessListener(aVoid -> Log.d(TAG, "SUCCESS: Event Helper catalog uploaded."));
    }

    private static Map<String, Object> getElectricianData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "electrician");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "install_fan");
                    put("title", "Fan Installation & Replacement");
                    put("description", "Install a new ceiling fan, wall fan, or exhaust fan, or replace an existing one.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "fan_type");
                                put("text", "What type of fan is it?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("Ceiling Fan", "Exhaust Fan", "Wall-Mounted Fan"));
                            }},
                            new HashMap<String, Object>() {{
                                put("questionId", "fan_action");
                                put("text", "Is this a new installation or a replacement?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("Install a new one", "Replace an existing one"));
                            }},
                            new HashMap<String, Object>() {{
                                put("questionId", "fan_quantity");
                                put("text", "How many fans need to be installed/replaced?");
                                put("type", "QUANTITY");
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "repair_fan");
                    put("title", "Fan Repair");
                    put("description", "Repair for issues like slow speed, noise, or not starting.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "fan_repair_issue");
                                put("text", "What is the problem with the fan?");
                                put("type", "MULTIPLE_CHOICE");
                                put("options", Arrays.asList("Not turning on", "Making excessive noise", "Running slow", "Wobbling"));
                            }},
                            new HashMap<String, Object>() {{
                                put("questionId", "fan_repair_type");
                                put("text", "What type of fan needs repair?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("Ceiling Fan", "Exhaust Fan", "Wall-Mounted Fan"));
                            }}
                    ));
                }}
        ));
        return data;
    }

    private static Map<String, Object> getCarpenterData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "carpenter");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "furniture_assembly");
                    put("title", "Furniture Assembly");
                    put("description", "Professional assembly of flat-pack or new furniture items.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "assembly_item_type");
                                put("text", "What type of furniture needs assembling?");
                                put("type", "MULTIPLE_CHOICE");
                                put("options", Arrays.asList("Table / Desk", "Chair / Stool", "Bed Frame", "Wardrobe / Cupboard", "Bookshelf / Rack"));
                            }},
                            new HashMap<String, Object>() {{
                                put("questionId", "assembly_quantity");
                                put("text", "How many items of furniture need to be assembled?");
                                put("type", "QUANTITY");
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "door_services");
                    put("title", "Door Repair & Installation");
                    put("description", "Repairing existing doors or installing new ones, including handles, locks, and hinges.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "door_service_needed");
                                put("text", "What service do you need for your door?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("Repair an existing door", "Install a new door"));
                            }},
                            new HashMap<String, Object>() {{
                                put("questionId", "door_issue");
                                put("text", "What is the issue with the door?");
                                put("type", "MULTIPLE_CHOICE");
                                put("options", Arrays.asList("Not closing properly / Jamming", "Hinges are loose or broken", "Lock or handle issue", "Door is damaged (cracked, hole)"));
                            }}
                    ));
                }}
        ));
        return data;
    }

    private static Map<String, Object> getPainterData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "painter");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "interior_painting");
                    put("title", "Interior Wall Painting");
                    put("description", "Professional painting for interior walls, ceilings, and rooms.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "interior_area");
                                put("text", "What needs to be painted?");
                                put("type", "MULTIPLE_CHOICE");
                                put("options", Arrays.asList("Single Room", "Multiple Rooms", "Full House Interior"));
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "exterior_painting");
                    put("title", "Exterior Wall Painting");
                    put("description", "Painting for the outside of your building, including walls and facades.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "exterior_building_type");
                                put("text", "What type of property is it?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("Standalone House / Villa", "Apartment Building (specific walls)"));
                            }}
                    ));
                }}
        ));
        return data;
    }

    private static Map<String, Object> getWelderData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "welder");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "gate_grill_fabrication");
                    put("title", "Gate & Grill Fabrication");
                    put("description", "Fabrication of new metal gates, window grills, and railings.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "fabrication_item");
                                put("text", "What do you need to be made?");
                                put("type", "MULTIPLE_CHOICE");
                                put("options", Arrays.asList("Main Gate", "Window Grill", "Balcony/Stair Railing"));
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "welding_repair");
                    put("title", "Welding Repair Work");
                    put("description", "Repairing broken or cracked metal items like gates, furniture, and structures.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "repair_photo");
                                put("text", "Please upload a clear photo of the broken part.");
                                put("type", "PHOTO");
                            }}
                    ));
                }}
        ));
        return data;
    }

    private static Map<String, Object> getCleanerData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "cleaner");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "full_home_cleaning");
                    put("title", "Full Home Deep Cleaning");
                    put("description", "Comprehensive deep cleaning for your entire home.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "home_size");
                                put("text", "What is the size of your home?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("1 BHK", "2 BHK", "3 BHK", "4 BHK or larger"));
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "bathroom_cleaning");
                    put("title", "Bathroom Deep Cleaning");
                    put("description", "Intensive cleaning and sanitization of bathroom tiles, fixtures, and toilet.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "bathroom_quantity");
                                put("text", "How many bathrooms need to be deep cleaned?");
                                put("type", "QUANTITY");
                            }}
                    ));
                }}
        ));
        return data;
    }

    private static Map<String, Object> getAcRepairData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "ac repair");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "ac_service");
                    put("title", "AC Service & Cleaning");
                    put("description", "Jet cleaning for indoor and outdoor units to improve cooling and efficiency.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "ac_type");
                                put("text", "What type of AC is it?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("Split AC", "Window AC"));
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "ac_repair");
                    put("title", "AC Repair");
                    put("description", "Diagnose and repair issues like no cooling, water leakage, or strange noises.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "ac_repair_issue");
                                put("text", "What is the problem with the AC?");
                                put("type", "MULTIPLE_CHOICE");
                                put("options", Arrays.asList("AC not turning on", "Not cooling at all", "Less cooling than usual", "Water leaking from indoor unit"));
                            }}
                    ));
                }}
        ));
        return data;
    }

    private static Map<String, Object> get2wMechanicData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "2w mechanic");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "general_service_2w");
                    put("title", "General Service");
                    put("description", "Complete periodic service including engine oil change, washing, and general check-up.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "2w_make_model");
                                put("text", "What is the make and model of your two-wheeler?");
                                put("type", "TEXT");
                                put("placeholder", "e.g., 'Honda Activa 5G', 'Bajaj Pulsar 150'");
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "starting_problem_2w");
                    put("title", "Starting Problem");
                    put("description", "Diagnose and fix issues related to the vehicle not starting.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "2w_start_issue");
                                put("text", "What happens when you try to start it?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("No sound or power at all", "Makes a clicking sound but doesn't start", "Starts but stops immediately"));
                            }}
                    ));
                }}
        ));
        return data;
    }

    private static Map<String, Object> get4wMechanicData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "4w mechanic");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "general_service_4w");
                    put("title", "Car General Service");
                    put("description", "Periodic maintenance service for your car, including oil, filters, and a full check-up.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "4w_make_model");
                                put("text", "What is the make, model, and year of your car?");
                                put("type", "TEXT");
                                put("placeholder", "e.g., 'Maruti Suzuki Swift 2018'");
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "starting_problem_4w");
                    put("title", "Car Starting Problem");
                    put("description", "Diagnose and fix issues related to the car not starting.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "4w_start_issue");
                                put("text", "What happens when you try to start the car?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("No power or lights on the dashboard", "Makes a 'tak-tak' clicking sound", "Engine cranks but doesn't start"));
                            }}
                    ));
                }}
        ));
        return data;
    }

    private static Map<String, Object> getEventHelperData() {
        Map<String, Object> data = new HashMap<>();
        data.put("skill", "event helper");
        data.put("services", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("serviceId", "serving_guests");
                    put("title", "Serving Food & Guests");
                    put("description", "General assistance with serving food and drinks, and attending to guests.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "event_type_serving");
                                put("text", "What type of event is it?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("Birthday Party", "Wedding / Reception", "Corporate Event"));
                            }},
                            new HashMap<String, Object>() {{
                                put("questionId", "guest_count");
                                put("text", "What is the approximate number of guests?");
                                put("type", "SINGLE_CHOICE");
                                put("options", Arrays.asList("Up to 50", "50-100", "100+"));
                            }}
                    ));
                }},
                new HashMap<String, Object>() {{
                    put("serviceId", "setup_cleanup");
                    put("title", "Setup & Cleanup");
                    put("description", "Help with setting up decorations, chairs, tables, and post-event cleanup.");
                    put("questions", Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("questionId", "task_type");
                                put("text", "What tasks do you need help with?");
                                put("type", "MULTIPLE_CHOICE");
                                put("options", Arrays.asList("Pre-event Setup", "Post-event Cleanup"));
                            }}
                    ));
                }}
        ));
        return data;
    }
}
