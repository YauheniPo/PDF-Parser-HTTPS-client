package popo.pdfparse.framework.base;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import popo.pdfparse.framework.base.driver.Browser;

import java.util.Set;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

@Log4j2
public class WebPage extends BaseEntity {

    protected WebElement expandShadowRootElement(WebElement element) {
        return (WebElement) ((JavascriptExecutor) getWebDriver())
                .executeScript("return arguments[0].shadowRoot", element);
    }

    protected void switchToFrame(String frameLocator) {
        switchToFrame((byXpath(frameLocator)));
    }

    protected void switchToFrame(By frameSelector) {
        switchTo().frame($(frameSelector));
    }

    protected void switchToDefaultFrame() {
        switchTo().defaultContent();
    }

    protected WebElement getElementFromIFrame(String iFrameLocator, String elementLocator) {
        return getElementFromIFrame(byXpath(iFrameLocator), byXpath(elementLocator));
    }

    protected WebElement getElementFromIFrame(By iFrameSelector, By elementSelector) {
        new WebDriverWait(getWebDriver(), Browser.TIMEOUT)
                .ignoring(StaleElementReferenceException.class, WebDriverException.class)
                .until(new ExpectedCondition<Boolean>() {

                    @Override
                    public Boolean apply(WebDriver driver) {
                        if (iFrameSelector != null) {
                            switchToFrame(iFrameSelector);
                        }
                        return $(elementSelector).isDisplayed();
                    }

                    @Override
                    public String toString() {
                        return String.format("visibility of element (%s) in iFrame (%s)", elementSelector, iFrameSelector);
                    }
                });

        return $(elementSelector);
    }

    protected String getParentWindowHandle() {
        return getWebDriver().getWindowHandle();
    }

    protected Set<String> getWindowHandles() {
        return getWebDriver().getWindowHandles();
    }

    protected void switchToWindow(String windowHandle) {
        getWebDriver().switchTo().window(windowHandle);
    }

    protected boolean isExistChildWindowAfterWait(String parentWindow) {
        try {
            new WebDriverWait(getWebDriver(), Browser.IMPLICITLY_WAIT).until(webDriver -> {
                Set<String> handles = getWindowHandles();
                for (String handle : handles) {
                    if (!handle.equals(parentWindow)) {
                        return true;
                    }
                }
                return false;
            });
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    protected void switchToChildWindow(String parentWindow) {
        Set<String> handles = getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(parentWindow)) {
                switchToWindow(handle);
                break;
            }
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    protected enum AttributeElement {

        SRC;

        String value = null;

        @Override
        public String toString() {
            return value == null ? name().toLowerCase() : value;
        }
    }
}
