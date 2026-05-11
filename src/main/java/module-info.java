module org.example.tripstrapstrull2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.tripstrapstrull2 to javafx.fxml;
    exports org.example.tripstrapstrull2;
}