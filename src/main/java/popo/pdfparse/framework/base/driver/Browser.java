package popo.pdfparse.framework.base.driver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.remote.RemoteWebDriver;
import popo.pdfparse.framework.util.ResourcePropertiesManager;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Log4j2
public final class Browser {

    private static ResourcePropertiesManager rpStage = new ResourcePropertiesManager("stage.properties");
    private static ResourcePropertiesManager rpBrowser = new ResourcePropertiesManager("browser.properties");
    public static final String STAGE = rpStage.getStringProperty("stage");
    public static final String URL = rpStage.getStringProperty("urlStage");
    private static final String BROWSER_URL = getStage();
    private static BrowserType currentBrowser = BrowserType.valueOf((System.getenv("browser") == null
            ? rpBrowser.getStringProperty("browser") : ResourcePropertiesManager.getSystemEnvProperty("browser"))
            .toUpperCase(Locale.ENGLISH));
    private static final boolean IS_BROWSER_HEADLESS = rpBrowser.getBooleanProperties("browser.headless");
    public static final long IMPLICITLY_WAIT = rpBrowser.getLongProperties("browser.timeout");
    private static Browser instance = null;

    public static void getInstance() {
        if (instance == null) {
            synchronized (Browser.class) {
                instance = new Browser();
                initDriverConfigs();
            }
        }
    }

    private static void initDriverConfigs() {
        Configuration.timeout = IMPLICITLY_WAIT;
        Configuration.headless = IS_BROWSER_HEADLESS;
        Configuration.baseUrl = BROWSER_URL;
        Configuration.startMaximized = true;
        DriverManager.setUp(currentBrowser);
    }

    public static void openStartPage() {
        Selenide.open("");
    }

    public static RemoteWebDriver getDriver() {
        return (RemoteWebDriver) WebDriverRunner.getWebDriver();
    }

    @AllArgsConstructor()
    public enum BrowserType {
        FIREFOX("firefox"),
        CHROME("chrome"),
        EDGE("edge"),
        IE("ie");

        @Getter
        private final String value;
    }

    public static BasicCookieStore getDriverCookieStore() {
        Set<Cookie> cookies = getDriver().manage().getCookies();
        BasicCookieStore cookieStore = new BasicCookieStore();
        for (Cookie c : cookies) {
            BasicClientCookie cookie = new BasicClientCookie(c.getName(), c.getValue());
            Objects.requireNonNull(cookie).setDomain(c.getDomain());
            cookie.setPath(c.getPath());
            cookieStore.addCookie(cookie);
        }
        return cookieStore;
    }

    private static String getStage() {
        String stage = STAGE;
        if (Objects.requireNonNull(URL).contains("///")) {
            stage = System.getProperty("user.dir") + stage;
        }
        return URL + stage;
    }
}
