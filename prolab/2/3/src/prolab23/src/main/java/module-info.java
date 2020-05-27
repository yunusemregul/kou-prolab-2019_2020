module com.yunusemregul.prolab23 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.yunusemregul.prolab23 to javafx.fxml;
    exports com.yunusemregul.prolab23;
}