package mm.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mm.controller.OptionsController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

class OptionsControllerTest {

    private OptionsController controller;
    private ChoiceBox<String> mockChoiceBox;
    private Slider mockSlider;
    private ImageView mockImageView;

    @BeforeAll
    static void initJfxRuntime() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }


    @BeforeEach
    void setUp() {
        controller = new OptionsController();

        mockChoiceBox = new ChoiceBox<>();
        mockSlider = new Slider();
        mockImageView = mock(ImageView.class);

        // Use reflection to inject mocks into private fields
        try {
            var field1 = OptionsController.class.getDeclaredField("options_ResolutionCheckBox");
            field1.setAccessible(true);
            field1.set(controller, mockChoiceBox);

            var field2 = OptionsController.class.getDeclaredField("options_VolumeSlider");
            field2.setAccessible(true);
            field2.set(controller, mockSlider);

            var field3 = OptionsController.class.getDeclaredField("optionsScreen_imageSound");
            field3.setAccessible(true);
            field3.set(controller, mockImageView);
        } catch (Exception e) {
            fail("Reflection setup failed: " + e.getMessage());
        }
    }

    @Test
    void testInitializeSetsDefaults() {
        controller.initialize((URL) null, (ResourceBundle) null);
        assertEquals("1000x600", mockChoiceBox.getValue());
        assertTrue(mockChoiceBox.getItems().contains("1920x1080"));
        // Volume slider value should be set to App.getVolume() * 100 (App.getVolume() is 0.0 by default)
        assertEquals(0.0, mockSlider.getValue(), 1e-6);
        // Should set an image on the image view
        verify(mockImageView, atLeastOnce()).setImage(any(Image.class));
    }

    @Test
    void testReturnToMenuDoesNotThrow() {
        assertDoesNotThrow(() -> {
            // Use reflection to make returnToMenu accessible
            var method = OptionsController.class.getDeclaredMethod("returnToMenu");
            method.setAccessible(true);
            method.invoke(controller);
        });
    }
}
