module com.yunusemregul.prolab23 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.yunusemregul.prolab23 to javafx.fxml;
    exports com.yunusemregul.prolab23;
}