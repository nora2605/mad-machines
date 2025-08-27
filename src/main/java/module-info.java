/**
 * The main module of the mm application.
 */
module mm {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.media;
    requires javafx.fxml;
    requires transitive jbox2d.library;
    requires com.fasterxml.jackson.databind;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires reactfx;

    opens mm.controller to javafx.fxml, javafx.graphics;
    opens mm.level to com.fasterxml.jackson.databind;
    opens mm.view;
}
