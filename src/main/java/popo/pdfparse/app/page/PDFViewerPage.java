package popo.pdfparse.app.page;

import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;
import org.openqa.selenium.By;
import popo.pdfparse.framework.base.PDFParsePage;
import popo.pdfparse.framework.helpers.Locators;

import java.util.Map;

public class PDFViewerPage extends PDFParsePage {

    private final By pdfContentSelector = Locators.get("pdf.content");

    public Map<TextPosition, PDGraphicsState> getPdfContent() {
        Map<TextPosition, PDGraphicsState> context = getTextPositionPDGraphicsStateMap(pdfContentSelector);
        return context;
    }

}
