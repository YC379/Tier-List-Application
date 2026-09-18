module com.application.sae201 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires java.logging;

    opens com.application.sae201 to javafx.fxml, com.google.gson;

    exports com.application.sae201;
}