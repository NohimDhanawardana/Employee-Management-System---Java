module org.example.employeefx {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.employeefx to javafx.fxml;
    exports org.example.employeefx;
}