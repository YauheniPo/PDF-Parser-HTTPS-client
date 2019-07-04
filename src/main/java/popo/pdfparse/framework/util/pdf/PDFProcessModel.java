package popo.pdfparse.framework.util.pdf;

import lombok.Builder;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class PDFProcessModel {

    private Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap;
    private PDFFontType font;
    private Integer size;
    private PDFTextType type;
    private String[] searchStrings;
    private List<byte[]> images;
    private PDFTableModel pdfTableModel;
}
