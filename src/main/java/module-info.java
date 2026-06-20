module com.quickhire {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    opens com.quickhire.ui      to javafx.fxml;
    opens com.quickhire.model   to javafx.base, javafx.fxml;
    opens com.quickhire.service to javafx.fxml;

    exports com.quickhire;
}