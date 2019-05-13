package popo.pdfparse.framework.base.driver;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class DriverManager {

    public static void setUp(Browser.BrowserType browserType) {
        String browserName;
        switch (browserType) {
            case CHROME:
                browserName = Browsers.CHROME;
                break;
            case FIREFOX:
                browserName = Browsers.FIREFOX;
                break;
            case EDGE:
                browserName = Browsers.EDGE;
                break;
            case IE:
                browserName = Browsers.INTERNET_EXPLORER;
                break;
            default:
                browserName = Browsers.CHROME;
                log.info(String.format("Init '%s' default browser", browserName));
                break;
        }
        log.info(String.format("Set up '%s' browser", browserName));
        Configuration.browser = browserName;
    }
}
