package mm.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import mm.controller.GameWindow;

import java.util.concurrent.CountDownLatch;

class GameWindowTest {
    
    @BeforeAll
    static void initJfxRuntime() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @Test
    void testSetLevelDoesNotThrow() {
        GameWindow.setLevel("testlevel.json");
    }
}
