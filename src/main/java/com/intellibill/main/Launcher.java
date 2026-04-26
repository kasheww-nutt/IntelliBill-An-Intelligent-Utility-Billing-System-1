package com.intellibill.main;

import com.intellibill.service.BillingSystem;
import com.intellibill.service.ServiceRegistry;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        boolean consoleMode = args != null && args.length > 0 && "--console".equalsIgnoreCase(args[0]);
        ServiceRegistry.initialize();
        if (consoleMode) {
            new BillingSystem().startConsole();
            return;
        }

        try {
            Application.launch(MainApp.class, args);
        } catch (Exception ex) {
            System.out.println("JavaFX launch failed. Falling back to console mode.");
            System.out.println("Reason: " + ex.getMessage());
            ex.printStackTrace();
            new BillingSystem().startConsole();
        }
    }
}
