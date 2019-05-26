package popo.pdfparse.app.page;

import org.openqa.selenium.By;
import popo.pdfparse.framework.base.PDFParsePage;
import popo.pdfparse.framework.helpers.Locators;
import popo.pdfparse.framework.util.pdf.PDFHelper;

public class PDFViewerPage extends PDFParsePage {

    private final By pdfContentSelector = Locators.get("pdf.content");

    public PDFHelper getPDFHelper() {
        return super.getPDFHelper(pdfContentSelector);
    }
}
