package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

public class Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Properties properties;

    private Settings (String fileName) {
        this.properties = this.getProperties(fileName);
    }

    /**
     * Reads content from the given file and transforms it to the {@link Properties} object.
     * If the file is a resource (placed in the resources folder) it will try to read it directly from resources folder,
     * otherwise file will be read as an absolute path.
     * @param fileName Name of the file or an absolute path to the file.
     * @return {@link Properties} object with all the properties.
     */
    private Properties getProperties(String fileName) {
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            // Reading from properties failed, try to read file from given path
            if (inputStream == null) {
                inputStream = new FileInputStream(fileName);
            }
            properties.load(inputStream);
        } catch (Exception exception) {
            Settings.LOGGER.error("Error reading file:"+exception.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException exception) {
                Settings.LOGGER.error("Error closing input stream:"+exception.getMessage());
            }
        }
        return properties;
    }

    /**
     * From the {@link #properties} attribute retrieves all the properties that are needed to form connection url
     * to a socket.
     * @return String that represents url to PubSub component of the Tracking System (See more at https://github.com/dejvv/trackingsystem).
     * {@link java.net.URI} is able to parse returned url.
     */
    public String getPubSubConnectionUrl () {
        return this.getProperties().getProperty("pubsub.protocol") +
                "://" + this.getProperties().getProperty("pubsub.baseurl") +
                ":" + this.getProperties().getProperty("pubsub.port") +
                this.getProperties().getProperty("pubsub.path");
    }

    /**
     * Returns pubsub.accountsTopic property from {@link #properties}. It represents topic to which {@link messages.WebSocketConnection}
     * will be subscribed and will receive messages.
     * @return Topic from which {@link messages.WebSocketConnection} will receive messages.
     */
    public String getAccountsTopic () {
        return this.getProperties().getProperty("pubsub.accountsTopic");
    }

    /**
     * Creates new instance of Settings object. When new instance is created given file will already be parsed and properties
     * will be saved in {@link #properties} attribute.
     * @param fileName Name of the file or an absolute path to the file.
     * @return Settings object that contains info about parsed file.
     */
    public static Settings getSettings(String fileName) {
        return new Settings(fileName);
    }

    public Properties getProperties() {
        return this.properties;
    }
}

