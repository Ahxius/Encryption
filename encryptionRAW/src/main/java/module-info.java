module com.example.encryptionraw {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.desktop;
    requires org.bouncycastle.provider;
    requires commons.codec;


    opens com.example.encryptionraw to javafx.fxml;
    exports com.example.encryptionraw;
}