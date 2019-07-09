package popo.pdfparse.framework.util.pdf.models;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Map;

@Builder
public class PDFTableModel {

    @Getter private Map<String, String> tableValidationMap;
    @Getter private ArrayList<String> columns;
}
