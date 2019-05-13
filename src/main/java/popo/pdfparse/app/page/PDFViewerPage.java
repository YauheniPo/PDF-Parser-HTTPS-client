package popo.pdfparse.app.page;

import org.openqa.selenium.By;
import popo.pdfparse.framework.base.PDFParsePage;
import popo.pdfparse.framework.helpers.Locators;

public class PDFViewerPage extends PDFParsePage {

    private final By pdfContentSelector = Locators.get("pdf.content");

    public String getPdfContent() {
        String url = getPDFPluginSrcAttributeUrl(pdfContentSelector);
        return url;
    }

}
