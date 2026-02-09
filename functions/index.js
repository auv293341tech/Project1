const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

// Triggered when a new worker applies for a job
exports.onWorkerApplied = functions.firestore
    .document("applications/{applicationId}")
    .onCreate(async (snap, context) => {
        const application = snap.data();
        const customerId = application.customerId;
        const workerName = application.workerName;
        const jobTitle = application.serviceTitle;

        const payload = {
            notification: {
                title: "New Applicant!",
                body: `${workerName} has applied for your job: ${jobTitle}`,
            },
            data: {
                click_action: "com.example.project1.ApplicantsActivity",
                jobId: application.jobId,
            },
        };

        const customerRef = admin.firestore().collection("users").doc(customerId);
        const customerDoc = await customerRef.get();
        const fcmToken = customerDoc.data().fcmToken;

        if (fcmToken) {
            await admin.messaging().sendToDevice(fcmToken, payload);
        }
    });

// Triggered when a booking is confirmed
exports.onBookingConfirmed = functions.firestore
    .document("bookings/{bookingId}")
    .onCreate(async (snap, context) => {
        const booking = snap.data();
        const customerId = booking.customerId;
        const workerId = booking.workerId;
        const jobTitle = booking.serviceTitle;

        const customerPayload = {
            notification: {
                title: "Booking Confirmed!",
                body: `Your booking for ${jobTitle} has been confirmed.`,
            },
            data: {
                click_action: "com.example.project1.BookingDetailsActivity",
                jobId: booking.jobId,
            },
        };

        const workerPayload = {
            notification: {
                title: "Booking Confirmed!",
                body: `Your booking for ${jobTitle} has been confirmed.`,
            },
            data: {
                click_action: "com.example.project1.BookingDetailsActivity",
                jobId: booking.jobId,
            },
        };

        const customerRef = admin.firestore().collection("users").doc(customerId);
        const customerDoc = await customerRef.get();
        const customerFcmToken = customerDoc.data().fcmToken;

        if (customerFcmToken) {
            await admin.messaging().sendToDevice(customerFcmToken, customerPayload);
        }

        const workerRef = admin.firestore().collection("users").doc(workerId);
        const workerDoc = await workerRef.get();
        const workerFcmToken = workerDoc.data().fcmToken;

        if (workerFcmToken) {
            await admin.messaging().sendToDevice(workerFcmToken, workerPayload);
        }
    });

// Triggered when a worker is assigned to a job
exports.onWorkerAssigned = functions.firestore
    .document("jobPostings/{jobId}")
    .onUpdate(async (change, context) => {
        const before = change.before.data();
        const after = change.after.data();

        if (before.assignedWorkerId !== after.assignedWorkerId && after.assignedWorkerId) {
            const workerId = after.assignedWorkerId;
            const jobTitle = after.serviceTitle;

            const payload = {
                notification: {
                    title: "You've Been Assigned a Job!",
                    body: `You have been assigned to the job: ${jobTitle}`,
                },
                data: {
                    click_action: "com.example.project1.WorkerJobDetailsActivity",
                    jobId: context.params.jobId,
                },
            };

            const workerRef = admin.firestore().collection("users").doc(workerId);
            const workerDoc = await workerRef.get();
            const fcmToken = workerDoc.data().fcmToken;

            if (fcmToken) {
                await admin.messaging().sendToDevice(fcmToken, payload);
            }
        }
    });
