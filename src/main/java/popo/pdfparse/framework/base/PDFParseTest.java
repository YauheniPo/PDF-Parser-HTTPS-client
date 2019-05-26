package popo.pdfparse.framework.base;

import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import popo.pdfparse.framework.helpers.AssertPDFHelper;
import popo.pdfparse.framework.util.pdf.PDFFontType;
import popo.pdfparse.framework.util.pdf.PDFTextType;
import popo.pdfparse.framework.util.pdf.TextVerifyPDFProcessor;

import java.util.Arrays;

@Log4j2
public class PDFParseTest extends BaseTest {

    protected AssertPDFHelper assertPDFHelper = new AssertPDFHelper();

//    protected synchronized boolean isContainsPDFAllStrings(String pdfContentFromPDFPlugin, String... data) {
//        return new TextVerifyPDFProcessor(data).doProcess(pdfContentFromPDFPlugin);
//    }
//
//    protected synchronized boolean isContainsPDFDataFontForString(String pdfContentFromPDFPlugin, PDFFontType font, String... data) {
//        Assert.assertTrue(isContainsPDFAllStrings(pdfContentFromPDFPlugin, data), String.format("PDF content does not have all data from list %s", Arrays.toString(data)));
//        return new TextVerifyPDFProcessor(data).doProcess(font, pdfContentFromPDFPlugin, getTextPositionPDGraphicsStateMap());
//    }
//
//    protected synchronized boolean isContainsPDFDataSizeForString(String pdfContentFromPDFPlugin, int font, String... data) {
//        Assert.assertTrue(isContainsPDFAllStrings(pdfContentFromPDFPlugin, data), String.format("PDF content does not have all data from list %s", Arrays.toString(data)));
//        return new TextVerifyPDFProcessor(data).doProcess(font, pdfContentFromPDFPlugin, getTextPositionPDGraphicsStateMap());
//    }
//
//    protected synchronized boolean isContainsPDFDataTextTypeForString(String pdfContentFromPDFPlugin, PDFTextType type, String... data) {
//        Assert.assertTrue(isContainsPDFAllStrings(pdfContentFromPDFPlugin, data), String.format("PDF content does not have all data from list %s", Arrays.toString(data)));
//        return new TextVerifyPDFProcessor(data).doProcess(type, pdfContentFromPDFPlugin, getTextPositionPDGraphicsStateMap());
//    }
}
