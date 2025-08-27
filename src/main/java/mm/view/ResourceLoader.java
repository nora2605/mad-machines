package mm.view;

import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {
    /**
     * Returns a resource URL for the given path.
     * @param path
     * @return
     */
    public static URL getResource(String path) {
        URL url = ResourceLoader.class.getResource(path);
        if (url == null) {
            System.err.println("Resource not found: " + path);
        }
        return url;
    }

    /**
     * Returns an InputStream for the resource at the given path.
     * This can be used to read the contents of the resource.
     * @param path
     * @return
     */
    public static InputStream getResourceStream(String path) {
        InputStream stream = ResourceLoader.class.getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Resource not found: " + path);
        }
        return stream;
    }
}
