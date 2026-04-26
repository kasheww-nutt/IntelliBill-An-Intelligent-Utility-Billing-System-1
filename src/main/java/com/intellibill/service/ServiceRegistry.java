package com.intellibill.service;

public final class ServiceRegistry {
    private static final ConsumerService CONSUMER_SERVICE = new ConsumerService();
    private static final BillService BILL_SERVICE = new BillService(CONSUMER_SERVICE);
    private static final PaymentService PAYMENT_SERVICE = new PaymentService(BILL_SERVICE);
    private static final FileService FILE_SERVICE = new FileService();
    private static final DatabaseSyncService DATABASE_SYNC_SERVICE = new DatabaseSyncService();
    private static boolean databaseEnabled;
    private static boolean initialized;

    private ServiceRegistry() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

        try {
            DATABASE_SYNC_SERVICE.seedServices();
            DATABASE_SYNC_SERVICE.importAllFromDatabase();
            databaseEnabled = true;
            System.out.println("Startup: Loaded data from MySQL.");
        } catch (Exception dbEx) {
            databaseEnabled = false;
            System.out.println("Startup: MySQL unavailable, using file fallback. Reason: " + dbEx.getMessage());
            try {
                FILE_SERVICE.loadAll();
                System.out.println("Startup: Loaded fallback data from files.");
            } catch (Exception fileEx) {
                System.out.println("Startup: No fallback files loaded. Starting fresh.");
            }
        }
    }

    public static ConsumerService consumerService() {
        return CONSUMER_SERVICE;
    }

    public static BillService billService() {
        return BILL_SERVICE;
    }

    public static PaymentService paymentService() {
        return PAYMENT_SERVICE;
    }

    public static FileService fileService() {
        return FILE_SERVICE;
    }

    public static DatabaseSyncService databaseSyncService() {
        return DATABASE_SYNC_SERVICE;
    }

    public static boolean isDatabaseEnabled() {
        return databaseEnabled;
    }

    public static synchronized boolean ensureDatabaseMode() {
        if (databaseEnabled) {
            return true;
        }
        try {
            DATABASE_SYNC_SERVICE.seedServices();
            databaseEnabled = true;
            System.out.println("Database reconnect successful. Switching back to MySQL mode.");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
