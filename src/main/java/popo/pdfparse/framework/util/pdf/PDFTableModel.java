package popo.pdfparse.framework.util.pdf;

import lombok.Builder;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@Builder
public class PDFTableModel {

    private LinkedHashMap<String, String> tableValidationMap;
    private LinkedList<String> columns;
}
