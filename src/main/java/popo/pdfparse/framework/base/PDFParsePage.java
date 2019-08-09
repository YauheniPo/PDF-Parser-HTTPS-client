package popo.pdfparse.framework.base;

import com.codeborne.selenide.Selectors;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import popo.pdfparse.framework.util.pdf.PDFHelper;

import java.util.Arrays;

import static com.codeborne.selenide.Selectors.byXpath;

@Log4j2
public class PDFParsePage extends WebPage {

    protected synchronized String getPDFPluginSrcAttributeUrl(String pdfContextElementLocator, String... pdfPluginFrame) {
        By[] pdfPluginFrameSelector = Arrays.stream(pdfPluginFrame).map(Selectors::byXpath).toArray(By[]::new);
        return getPDFPluginSrcAttributeUrl(byXpath(pdfContextElementLocator), pdfPluginFrameSelector);
    }

    protected synchronized String getPDFPluginSrcAttributeUrl(By pdfContextElementSelector, By... pdfPluginFrame) {
        String pdfContentSrcUrl;
        String parentWindow = getParentWindowHandle();
        boolean isChildWindow = isExistChildWindowAfterWait(parentWindow);
        if (isChildWindow) {
            switchToChildWindow(parentWindow);
            pdfContentSrcUrl = getPDFPluginSrcAttributeUrl(null, pdfContextElementSelector);
            switchToWindow(parentWindow);
        } else {
            pdfContentSrcUrl = getPDFPluginSrcAttributeUrl(
                    pdfPluginFrame.length == 0 ? null : pdfPluginFrame[0], pdfContextElementSelector);
        }
        switchToDefaultFrame();
        return pdfContentSrcUrl;
    }

    private synchronized String getPDFPluginSrcAttributeUrl(String iFrameReportViewerLocator, String elementLocator) {
        return getPDFPluginSrcAttributeUrl(
                iFrameReportViewerLocator == null ? null : byXpath(iFrameReportViewerLocator),
                byXpath(elementLocator));
    }

    private synchronized String getPDFPluginSrcAttributeUrl(By iFrameReportViewerSelector, By elementSelector) {
        WebElement pdfFileReportViewerElement = getElementFromIFrame(iFrameReportViewerSelector, elementSelector);
        return pdfFileReportViewerElement.getAttribute(AttributeElement.SRC.toString());
    }

    protected synchronized PDFHelper getPDFHelper(By pdfContextElementSelector, By... pdfPluginFrameSelector) {
        return new PDFHelper(getPDFPluginSrcAttributeUrl(pdfContextElementSelector, pdfPluginFrameSelector)).fetchPDFTextStripperHelper();
    }
}
