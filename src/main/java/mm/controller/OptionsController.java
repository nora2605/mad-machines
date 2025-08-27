package mm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import mm.view.ResourceLoader;

public class OptionsController implements Initializable {

    @FXML
    private Slider options_VolumeSlider;
    @FXML
    private ImageView optionsScreen_imageSound;
    @FXML
    private Image volumeImage;
    @FXML
    private CheckBox options_fullscreenCheckbox;
    @FXML
    private StackPane optionsScreen_container;

    private double volume;

    @Override
    public void initialize(URL url, ResourceBundle resource) {

        options_VolumeSlider.setValue(App.getVolume() * 100);
        options_VolumeSlider.valueProperty().addListener((Observable observable) -> {
            volume = options_VolumeSlider.getValue() / 100;
            App.setVolume(volume);
            changeVolumeImage(volume);
        });

        // set image via controller because it doesn't load via url in FXML
        Image image = new Image(ResourceLoader.getResource("/fxml/images/halfVolume.png").toExternalForm());
        optionsScreen_imageSound.setImage(image);
        options_fullscreenCheckbox.setSelected(App.getFullscreen());

        // all of this BS is just so the little checkmark appers/disappears when you press F11/ESC
        Platform.runLater(() -> {
            Scene scene = options_fullscreenCheckbox.getScene();
            if (scene != null) {
                scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F11) {
                        options_fullscreenCheckbox.setSelected(App.getFullscreen());
                    }
                });
            }
        });
    }

    /**
     * Sets the Root of the scene back to our Start Screen.
     * @throws IOException
     */
    @FXML
    private void returnToMenu() throws IOException {
        App.setRoot("StartScreen");
    }

    /**
     * Changes the Volume icon based on the current volume value of the Slider Element. For a volume of 0 it's the crossed out Volume icon, for a volume greater or equal to 70% it is the high volume icon.
     * @param volume the value which is used to set the image
     * 
     */
    @FXML
    private void changeVolumeImage(double volume) {
        if (volume == 0) {
            volumeImage = new Image(ResourceLoader.getResource("/fxml/images/noVolume.png").toExternalForm());
        } else if (volume > 0.7) {
            volumeImage = new Image(ResourceLoader.getResource("/fxml/images/fullVolume.png").toExternalForm());
        } else {
            volumeImage = new Image(ResourceLoader.getResource("/fxml/images/halfVolume.png").toExternalForm());
        }
        optionsScreen_imageSound.setImage(volumeImage);
    }

    /**
     * Sets fullscreen in the Application.
     */
    @FXML
    private void changeFullscreen() {
        App.setFullscreen(options_fullscreenCheckbox.isSelected());
    }
}
