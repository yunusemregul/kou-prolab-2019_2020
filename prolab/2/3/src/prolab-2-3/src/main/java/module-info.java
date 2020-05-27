module com.yunusemregul.prolab. {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.yunusemregul.prolab. to javafx.fxml;
    exports com.yunusemregul.prolab.;
}