package popo.pdfparse.framework.base;

import com.codeborne.selenide.Selectors;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import popo.pdfparse.framework.util.pdf.PDFFontType;
import popo.pdfparse.framework.util.pdf.PDFHelper;
import popo.pdfparse.framework.util.pdf.PDFTextType;
import popo.pdfparse.framework.util.pdf.TextVerifyPDFProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selectors.byXpath;

@Log4j2
public class PDFParsePage extends WebPage {

    protected String getPDFPluginSrcAttributeUrl(String pdfContextElementLocator, String... pdfPluginFrame) {
        By[] pdfPluginFrameSelector = Arrays.stream(pdfPluginFrame).map(Selectors::byXpath).toArray(By[]::new);
        return getPDFPluginSrcAttributeUrl(byXpath(pdfContextElementLocator), pdfPluginFrameSelector);
    }

    protected String getPDFPluginSrcAttributeUrl(By pdfContextElementSelector, By... pdfPluginFrame) {
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
            switchToDefaultFrame();
        }
        return pdfContentSrcUrl;
    }

    private synchronized String getPDFPluginSrcAttributeUrl(String iFrameReportViewerLocator, String elementLocator) {
        return getPDFPluginSrcAttributeUrl(byXpath(iFrameReportViewerLocator), byXpath(elementLocator));
    }

    private synchronized String getPDFPluginSrcAttributeUrl(By iFrameReportViewerSelector, By elementSelector) {
        WebElement pdfFileReportViewerElement = getElementFromIFrame(iFrameReportViewerSelector, elementSelector);
        return pdfFileReportViewerElement.getAttribute(AttributeElement.SRC.toString());
    }

    protected synchronized String getPDFContentFromPDFPlugin(String pdfContextElementLocator, String... pdfPluginFrame) {
        return PDFHelper.getPDFContent(getPDFPluginSrcAttributeUrl(pdfContextElementLocator, pdfPluginFrame));
    }

    protected synchronized List getPDFImagesFromPDFPlugin(String pdfContextElementLocator, String... pdfPluginFrame) {
        return PDFHelper.getPDFImages(getPDFPluginSrcAttributeUrl(pdfContextElementLocator, pdfPluginFrame));
    }

    protected synchronized Map<TextPosition, PDGraphicsState> getTextPositionPDGraphicsStateMap() {
        return PDFHelper.getTextPositionPDGraphicsStateMap();
    }

    protected synchronized boolean isContainsPDFAllStrings(String pdfContentFromPDFPlugin, String... data) {
        return new TextVerifyPDFProcessor(data).doProcess(pdfContentFromPDFPlugin);
    }

    protected synchronized boolean isContainsPDFDataFontForString(String pdfContentFromPDFPlugin, PDFFontType font, String... data) {
        Assert.assertTrue(isContainsPDFAllStrings(pdfContentFromPDFPlugin, data), String.format("PDF content does not have all data from list %s", Arrays.toString(data)));
        return new TextVerifyPDFProcessor(data).doProcess(font, pdfContentFromPDFPlugin, getTextPositionPDGraphicsStateMap());
    }

    protected synchronized boolean isContainsPDFDataSizeForString(String pdfContentFromPDFPlugin, int font, String... data) {
        Assert.assertTrue(isContainsPDFAllStrings(pdfContentFromPDFPlugin, data), String.format("PDF content does not have all data from list %s", Arrays.toString(data)));
        return new TextVerifyPDFProcessor(data).doProcess(font, pdfContentFromPDFPlugin, getTextPositionPDGraphicsStateMap());
    }

    protected synchronized boolean isContainsPDFDataTextTypeForString(String pdfContentFromPDFPlugin, PDFTextType type, String... data) {
        Assert.assertTrue(isContainsPDFAllStrings(pdfContentFromPDFPlugin, data), String.format("PDF content does not have all data from list %s", Arrays.toString(data)));
        return new TextVerifyPDFProcessor(data).doProcess(type, pdfContentFromPDFPlugin, getTextPositionPDGraphicsStateMap());
    }
}
