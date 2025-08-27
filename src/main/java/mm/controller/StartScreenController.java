package mm.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class StartScreenController {
    /**
     * set scene to LevelSelectScreen. Stops the Start Screen Music.
     * @throws IOException
     */
    @FXML
    private void startGame() throws IOException {
        App.stopMusic();
        App.setRoot("LevelSelectScreen");
    }

    @FXML
    private void sandBoxMode() throws IOException {
        App.stopMusic();
        App.setRoot("SandBoxModeMenu");
    }

    @FXML
    private void helpScreen() throws IOException {
        App.setRoot("HelpScreen");
    }

    /**
     * exits game via Platform.exit
     * @throws IOException
     */
    @FXML
    private void endGame() throws IOException {
        Platform.exit();
    }

    /**
     * Sets the Root of the scene to the Options Screen.
     * @throws IOException
     */
    @FXML
    private void openOptions() throws IOException {
        App.setRoot("OptionsScreen");
    }
}
