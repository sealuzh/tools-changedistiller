package ch.uzh.ifi.seal.changedistiller.ast;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class to handle {@link File}s.
 * 
 * @author Beat Fluri
 */
public final class FileUtils {

    private FileUtils() {}

    /**
     * Returns the content of the {@link File} as {@link String}.
     * 
     * @param file
     *            to read the content from
     * @return the content of the file
     */
    public static String getContent(File file) {
        char[] b = new char[1024];
        StringBuilder sb = new StringBuilder();
        try {
            FileReader reader = new FileReader(file);
            int n = reader.read(b);
            while (n > 0) {
                sb.append(b, 0, n);
                n = reader.read(b);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}
