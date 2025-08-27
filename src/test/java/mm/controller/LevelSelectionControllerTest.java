package mm.controller;

import org.junit.jupiter.api.Test;

import mm.controller.LevelSelectionController;

import static org.junit.jupiter.api.Assertions.*;

class LevelSelectionControllerTest {

    @Test
    void testConstructorDoesNotThrow() {
        assertDoesNotThrow(LevelSelectionController::new);
    }
}
