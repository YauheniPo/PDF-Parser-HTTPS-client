package popo.pdfparse.framework.util.pdf;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TextVerifyPDFProcessor {

    private String[] searchStrings;

    public TextVerifyPDFProcessor(String... searchStrings) {
        this.searchStrings = searchStrings;
    }

    public boolean doProcess(String pdfContext) {
        try {
            return verifyPDFContainsAllStrings(pdfContext);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public boolean doProcess(PDFFontType font, String pdfContext, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap) {
        try {
            return verifyPDFFontForStrings(font, pdfContext, textPositionPDGraphicsStateMap);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public boolean doProcess(int size, String pdfContext, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap) {
        try {
            return verifyPDFContentSizeForStrings(size, pdfContext, textPositionPDGraphicsStateMap);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public boolean doProcess(PDFTextType type, String pdfContext, Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap) {
        try {
            return verifyPDFContentTextTypeForStrings(type, pdfContext, textPositionPDGraphicsStateMap);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    private boolean verifyPDFContainsAllStrings(String pdfContext) {
        String normalizedReportString = StringUtils.deleteWhitespace(pdfContext);

        for (String aSearchString : searchStrings) {
            String searchString = StringUtils.deleteWhitespace(aSearchString);
            if (!StringUtils.containsIgnoreCase(normalizedReportString, searchString)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyPDFFontForStrings(PDFFontType font, String pdfContext, Map<TextPosition, PDGraphicsState> graphicsStateMap) {
        String pdfContentWithoutSpecialSymbols = getCleanPDFContent(pdfContext);
        Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap = getCleanTextPositionPdGraphicsStateMap(graphicsStateMap);
        List<TextPosition> textPositions = new LinkedList<>(textPositionPDGraphicsStateMap.keySet());
        for (String searchString : searchStrings) {
            String validateText = StringUtils.deleteWhitespace(searchString);
            int validationPdfTextLength = validateText.length();
            int startTextIndex = pdfContentWithoutSpecialSymbols.indexOf(validateText);

            for (int i = startTextIndex; i < startTextIndex + validationPdfTextLength; ++i) {
                try {
                    if (!((PDType1Font) textPositions.get(i).getFont()).getFontBoxFont().getName().equals(font.getFontTypeName())) {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private boolean verifyPDFContentSizeForStrings(int size, String pdfContext, Map<TextPosition, PDGraphicsState> graphicsStateMap) {
        String pdfContentWithoutSpecialSymbols = getCleanPDFContent(pdfContext);
        Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap = getCleanTextPositionPdGraphicsStateMap(graphicsStateMap);
        List<TextPosition> textPositions = new LinkedList<>(textPositionPDGraphicsStateMap.keySet());
        for (String searchString : searchStrings) {
            String validateText = StringUtils.deleteWhitespace(searchString);
            int validationPdfTextLength = validateText.length();
            int startTextIndex = pdfContentWithoutSpecialSymbols.indexOf(validateText);

            for (int i = startTextIndex; i < startTextIndex + validationPdfTextLength; ++i) {
                if (textPositions.get(i).getFontSizeInPt() != size) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verifyPDFContentTextTypeForStrings(PDFTextType type, String pdfContext, Map<TextPosition, PDGraphicsState> graphicsStateMap) {
        String pdfContentWithoutSpecialSymbols = getCleanPDFContent(pdfContext);
        Map<TextPosition, PDGraphicsState> textPositionPdGraphicsStateMap = getCleanTextPositionPdGraphicsStateMap(graphicsStateMap);
        List<TextPosition> textPositions = new LinkedList<>(textPositionPdGraphicsStateMap.keySet());
        for (String searchString : searchStrings) {
            String validateText = StringUtils.deleteWhitespace(searchString);
            int validationPdfTextLength = validateText.length();
            int startTextIndex = pdfContentWithoutSpecialSymbols.indexOf(validateText);

            for (int i = startTextIndex; i < startTextIndex + validationPdfTextLength; ++i) {
                if (!textPositionPdGraphicsStateMap.get(textPositions.get(i)).getTextState().getRenderingMode().name().equals(type.getTextType())) {
                    return false;
                }
            }
        }
        return true;
    }

    private Map<TextPosition, PDGraphicsState> getCleanTextPositionPdGraphicsStateMap(Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap) {
        Map<TextPosition, PDGraphicsState> positionPDGraphicsStateMap = new LinkedHashMap<>(textPositionPDGraphicsStateMap);
        textPositionPDGraphicsStateMap.keySet().forEach(textPosition -> {
            if (textPosition.getUnicode().hashCode() == 32) {
                positionPDGraphicsStateMap.remove(textPosition);
            }
        });
        return positionPDGraphicsStateMap;
    }

    private String getCleanPDFContent(String pdfContext) {
        return StringUtils.deleteWhitespace(pdfContext.replaceAll("[\\r\\n]", ""));
    }
}
