module htl.steyr.minesweeper_lmikota {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.google.gson;
    requires java.desktop;

    opens htl.steyr.minesweeper_lmikota to javafx.fxml;
    exports htl.steyr.minesweeper_lmikota;
}