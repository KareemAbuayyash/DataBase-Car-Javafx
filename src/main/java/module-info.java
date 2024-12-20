module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.logging;
    requires java.xml;
    requires java.base;
    requires java.sql.rowset;
    requires java.naming;
    requires java.management;
    requires java.rmi;
    requires java.security.jgss;
    requires java.security.sasl;
    requires java.transaction.xa;
    requires java.xml.crypto;
    requires jdk.unsupported;
    requires org.kordamp.ikonli.javafx;
    requires javafx.graphics;
    requires javafx.base;
    requires java.prefs;

    opens com.example to javafx.fxml;

    exports com.example;
}