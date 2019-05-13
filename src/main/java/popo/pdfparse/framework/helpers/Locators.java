package popo.pdfparse.framework.helpers;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import popo.pdfparse.framework.util.ResourcePropertiesManager;

import static com.codeborne.selenide.Selectors.*;

@Log4j2
public class Locators {

    private static final ResourcePropertiesManager LOCATORS = new ResourcePropertiesManager("locators/locators.properties");

    public static By get(String locatorName) {
        String locatorProperty = LOCATORS.getStringProperty(locatorName);
        String[] locatorProperties = locatorProperty.split("=");
        String locator = locatorProperties[1];
        LocatorType locatorType = LocatorType.valueOf(locatorProperties[0]);
        switch (locatorType) {
            case text:
                return getByText(locator);
            case partText:
                return getWithText(locator);
            case id:
                return byId(locator);
            case className:
                return byClassName(locator);
            case xPath:
                return getByXpath(locator);
            default:
                throw new IllegalArgumentException(String.format("No suck locator addValue: %s", locatorType.toString()));
        }
    }

    public static String getLocator(String locatorName) {
        log.info(String.format("%s <-- string", locatorName));
        String locatorProperty = LOCATORS.getStringProperty(locatorName);
        String[] locatorProperties = locatorProperty.split("=");
        return locatorProperties[locatorProperties.length - 1];
    }

    public static By getByText(String locator) {
        log.info(String.format("%s <-- by text", locator));
        return byText(locator);
    }

    public static By getWithText(String locator) {
        log.info(String.format("%s <-- with text", locator));
        return withText(locator);
    }

    public static By getByXpath(String locator) {
        log.info(String.format("%s <-- xpath", locator));
        return byXpath(locator);
    }

    private enum LocatorType {
        id, xPath, text, partText, className
    }
}
