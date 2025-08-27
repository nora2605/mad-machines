package mm.controller;

import org.junit.jupiter.api.Test;

import mm.controller.App;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void testSetAndGetVolumeWithoutBgm() {
        // bgm is null initially, so getVolume should return 0.0 and print error
        assertEquals(0.0, App.getVolume(), 1e-6);
        // setVolume should not throw even if bgm is null
        assertDoesNotThrow(() -> App.setVolume(0.7));
    }

    @Test
    void testStopMusicWithoutBgm() {
        // bgm is null initially, so stopMusic should not throw
        assertDoesNotThrow(App::stopMusic);
    }

    // Note: Methods that require JavaFX runtime (like playMusic, setRoot, start) are not tested here,
    // as they require a JavaFX application thread and resources.
}