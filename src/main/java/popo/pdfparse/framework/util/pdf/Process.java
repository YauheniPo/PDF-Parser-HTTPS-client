package popo.pdfparse.framework.util.pdf;

import java.util.Map;

public interface Process {

    Map<Boolean, String> doProcess(PDFHelper pdfHelper, PDFProcessModel model);
}
