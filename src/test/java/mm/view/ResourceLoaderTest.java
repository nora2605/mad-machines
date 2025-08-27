package mm.view;

import org.junit.jupiter.api.Test;

import mm.view.ResourceLoader;

import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ResourceLoaderTest {

    @Test
    void testGetResourceReturnsNullForNonexistentResource() {
        URL url = ResourceLoader.getResource("/nonexistent.file");
        assertNull(url);
    }

    @Test
    void testGetResourceStreamReturnsNullForNonexistentResource() {
        InputStream stream = ResourceLoader.getResourceStream("/nonexistent.file");
        assertNull(stream);
    }
}