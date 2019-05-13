package popo.pdfparse.app.test;

import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import popo.pdfparse.app.page.PDFViewerPage;
import popo.pdfparse.framework.base.BaseTest;
import ru.yandex.qatools.allure.annotations.Description;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Severity;
import ru.yandex.qatools.allure.model.SeverityLevel;

@Log4j2
public class TestPDFParse extends BaseTest {

    @Description(value = "")
    @Features(value = "")
    @Severity(value = SeverityLevel.NORMAL)
    @Test(groups = {""})
    public void testPDFParse() {
        new PDFViewerPage().getPdfContent();
    }
}