package popo.pdfparse.framework.util.pdf;

import lombok.Getter;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;

import java.util.Map;

@Getter
public class PDFProcessModel {

    private Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap;
    private PDFFontType font;
    private Integer size;
    private PDFTextType type;
    private String[] searchStrings;

    public PDFProcessModel(String... searchStrings) {
        this.searchStrings = searchStrings;
    }

    public PDFProcessModel(PDFFontType font, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap, String... searchStrings) {
        this.font = font;
        this.textPositionPDGraphicsStateMap = textPositionPDGraphicsStateMap;
        this.searchStrings = searchStrings;
    }

    public PDFProcessModel(PDFTextType type, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap, String... searchStrings) {
        this.type = type;
        this.textPositionPDGraphicsStateMap = textPositionPDGraphicsStateMap;
        this.searchStrings = searchStrings;
    }

    public PDFProcessModel(Integer size, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap, String... searchStrings) {
        this.size = size;
        this.textPositionPDGraphicsStateMap = textPositionPDGraphicsStateMap;
        this.searchStrings = searchStrings;
    }

    public PDFProcessModel(int size, PDFFontType font, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap, String... searchStrings) {
        this.size = size;
        this.font = font;
        this.textPositionPDGraphicsStateMap = textPositionPDGraphicsStateMap;
        this.searchStrings = searchStrings;
    }

    public PDFProcessModel(PDFTextType type, PDFFontType font, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap, String... searchStrings) {
        this.type = type;
        this.font = font;
        this.textPositionPDGraphicsStateMap = textPositionPDGraphicsStateMap;
        this.searchStrings = searchStrings;
    }

    public PDFProcessModel(PDFTextType type, Integer size, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap, String... searchStrings) {
        this.type = type;
        this.size = size;
        this.textPositionPDGraphicsStateMap = textPositionPDGraphicsStateMap;
        this.searchStrings = searchStrings;
    }

    public PDFProcessModel(PDFTextType type, Integer size, PDFFontType font, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap, String... searchStrings) {
        this.type = type;
        this.size = size;
        this.font = font;
        this.textPositionPDGraphicsStateMap = textPositionPDGraphicsStateMap;
        this.searchStrings = searchStrings;
    }
}
