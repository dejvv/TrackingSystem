package filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class InputReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    /**
     * Reads data from an input stream and transforms it to Set of individual strings.
     * @param inputStream Input stream of data.
     * @return Set of individual strings that were read from input stream of data.
     */
    public static Set<String> readStream (InputStream inputStream) {
        Set<String> inputs = new HashSet<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());
            while (stringTokenizer.hasMoreElements()) {
                inputs.add(stringTokenizer.nextToken());
            }
        }
        catch (IOException e) {
            InputReader.LOGGER.error("Error reading stream:"+e.getMessage());
        }
        return inputs;
    }
}
