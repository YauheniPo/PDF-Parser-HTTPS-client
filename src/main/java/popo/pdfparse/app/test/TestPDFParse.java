package popo.pdfparse.app.test;

import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import popo.pdfparse.app.TestGroup;
import popo.pdfparse.app.page.PDFViewerPage;
import popo.pdfparse.framework.base.PDFParseTest;
import popo.pdfparse.framework.util.pdf.models.PDFProcessModel;
import popo.pdfparse.framework.util.pdf.models.PDFTableModel;
import ru.yandex.qatools.allure.annotations.Description;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Severity;
import ru.yandex.qatools.allure.model.SeverityLevel;

import java.util.ArrayList;

@Log4j2
public class TestPDFParse extends PDFParseTest {

    @Description(value = "PDF validation")
    @Features(value = "PDF parse")
    @Severity(value = SeverityLevel.NORMAL)
    @Test(groups = {TestGroup.PDF_GROUP})
    public void testPDFParse() {
        ArrayList<String> pdfTableColumns = new ArrayList<String>() {{
            add("Cell1");
            add("Cell2");
            add("Cell3");
        }};

        assertPDFHelper.assertPDF(new PDFViewerPage().getPDFHelper(),
                PDFProcessModel.builder().pdfTableModel(PDFTableModel.builder().columns(pdfTableColumns).build()).build());
    }
}
