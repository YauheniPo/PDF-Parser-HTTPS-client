package popo.pdfparse.framework.util;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
public final class ResourcePropertiesManager {

    private final Properties properties = new Properties();

    public ResourcePropertiesManager(@NonNull final String resourceName) {
        appendFromResource(resourceName);
    }

    public String getStringProperty(@NonNull final String key) {
        return this.properties.getProperty(key).trim();
    }

    public String getStringProperty(@NonNull final String key, @NonNull final String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    public long getLongProperties(@NonNull final String key) {
        return Long.parseLong(this.properties.getProperty(key));
    }

    public boolean getBooleanProperties(@NonNull final String key) {
        return Boolean.parseBoolean(this.properties.getProperty(key));
    }

    public void setProperty(@NonNull final String key, final String value) {
        this.properties.setProperty(key, value);
    }

    @SneakyThrows(IOException.class)
    private void appendFromResource(@NonNull final String resourceName) {
        @Cleanup InputStream inStreamBase = this.getClass().getClassLoader().getResourceAsStream(resourceName);
        if (inStreamBase != null) {
            this.properties.load(inStreamBase);
        } else {
            log.error(String.format("Base resource \"%1$s\" could not be found", resourceName));
        }
    }

    public static String getSystemEnvProperty(@NonNull final String key) {
        return System.getenv(key);
    }
}