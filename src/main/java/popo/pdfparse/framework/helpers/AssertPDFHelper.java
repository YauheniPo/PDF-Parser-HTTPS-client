package popo.pdfparse.framework.helpers;

import org.testng.asserts.SoftAssert;
import popo.pdfparse.framework.util.pdf.PDFHelper;
import popo.pdfparse.framework.util.pdf.PDFProcessModel;
import popo.pdfparse.framework.util.pdf.TextVerifyPDFProcessor;

public class AssertPDFHelper {

    public void assertPDF(PDFHelper pdfHelper, PDFProcessModel model) {
        SoftAssert softAssert = new SoftAssert();
        new TextVerifyPDFProcessor(model).doProcess(pdfHelper).forEach(softAssert::assertTrue);
        softAssert.assertAll();
    }
}
