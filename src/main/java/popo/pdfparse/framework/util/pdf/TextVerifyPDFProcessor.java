package popo.pdfparse.framework.util.pdf;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;
import popo.pdfparse.framework.helpers.DataUtils;
import popo.pdfparse.framework.util.Verification;

import java.io.IOException;
import java.util.*;

@Log4j2
@AllArgsConstructor
public class TextVerifyPDFProcessor implements Verification {

    private PDFProcessModel model;

    private boolean verifyPDFContainsAllStrings(PDFHelper pdfHelper) {
        return verifyPDFContainsAllStrings(pdfHelper.getPDFContent());
    }

    private boolean verifyPDFContainsAllStrings(String pdfContext) {
        String normalizedReportString = StringUtils.deleteWhitespace(pdfContext);

        for (String aSearchString : this.model.getSearchStrings()) {
            String searchString = StringUtils.deleteWhitespace(aSearchString);
            if (!StringUtils.containsIgnoreCase(normalizedReportString, searchString)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyPDFFontForStrings(PDFFontType font, PDFHelper pdfHelper) {
        return verifyPDFFontForStrings(font, pdfHelper.getPDFContent(), pdfHelper.getTextPositionPDGraphicsStateMap());
    }

    private boolean verifyPDFFontForStrings(PDFFontType font, String pdfContext, Map<TextPosition, PDGraphicsState> graphicsStateMap) {
        String pdfContentWithoutSpecialSymbols = getCleanPDFContent(pdfContext);
        Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap = getCleanTextPositionPdGraphicsStateMap(graphicsStateMap);
        List<TextPosition> textPositions = new LinkedList<>(textPositionPDGraphicsStateMap.keySet());
        for (String searchString : this.model.getSearchStrings()) {
            String validateText = StringUtils.deleteWhitespace(searchString);
            int validationPdfTextLength = validateText.length();
            int startTextIndex = pdfContentWithoutSpecialSymbols.indexOf(validateText);

            for (int i = startTextIndex, n = startTextIndex + validationPdfTextLength; i < n; ++i) {
                try {
                    if (!((PDType1Font) textPositions.get(i).getFont()).getFontBoxFont().getName().equals(font.getFontTypeName())) {
                        return false;
                    }
                } catch (IOException e) {
                    log.fatal(ExceptionUtils.getStackTrace(e));
                }
            }
        }
        return true;
    }

    private boolean verifyPDFContentSizeForStrings(int size, PDFHelper pdfHelper) {
        return verifyPDFContentSizeForStrings(size, pdfHelper.getPDFContent(), pdfHelper.getTextPositionPDGraphicsStateMap());
    }

    private boolean verifyPDFContentSizeForStrings(int size, String pdfContext, Map<TextPosition, PDGraphicsState> graphicsStateMap) {
        String pdfContentWithoutSpecialSymbols = getCleanPDFContent(pdfContext);
        Map<TextPosition, PDGraphicsState> textPositionPDGraphicsStateMap = getCleanTextPositionPdGraphicsStateMap(graphicsStateMap);
        List<TextPosition> textPositions = new LinkedList<>(textPositionPDGraphicsStateMap.keySet());
        for (String searchString : this.model.getSearchStrings()) {
            String validateText = StringUtils.deleteWhitespace(searchString);
            int validationPdfTextLength = validateText.length();
            int startTextIndex = pdfContentWithoutSpecialSymbols.indexOf(validateText);

            for (int i = startTextIndex, n = startTextIndex + validationPdfTextLength; i < n; ++i) {
                if (textPositions.get(i).getFontSizeInPt() != size) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verifyPDFContentTextTypeForStrings(PDFTextType type, PDFHelper pdfHelper) {
        return verifyPDFContentTextTypeForStrings(type, pdfHelper.getPDFContent(), pdfHelper.getTextPositionPDGraphicsStateMap());
    }

    private boolean verifyPDFImages(List<byte[]> expectedImages, PDFHelper pdfHelper) {
        List<byte[]> actualImages = DataUtils.convertImagesToBytes(pdfHelper.getPDFImages());
        if (expectedImages.size() == 0 && actualImages.size() == 0) {
            log.fatal(ExceptionUtils.getStackTrace(new AssertionError(
                    String.format("Images not found: actual list = %d; expected list = %d", actualImages.size(), expectedImages.size()))));
        }
        return expectedImages.containsAll(actualImages);
    }

    private boolean verifyPDFContentTextTypeForStrings(PDFTextType type, String pdfContext, Map<TextPosition, PDGraphicsState> graphicsStateMap) {
        String pdfContentWithoutSpecialSymbols = getCleanPDFContent(pdfContext);
        Map<TextPosition, PDGraphicsState> textPositionPdGraphicsStateMap = getCleanTextPositionPdGraphicsStateMap(graphicsStateMap);
        List<TextPosition> textPositions = new LinkedList<>(textPositionPdGraphicsStateMap.keySet());
        for (String searchString : this.model.getSearchStrings()) {
            String validateText = StringUtils.deleteWhitespace(searchString);
            int validationPdfTextLength = validateText.length();
            int startTextIndex = pdfContentWithoutSpecialSymbols.indexOf(validateText);

            for (int i = startTextIndex, n = startTextIndex + validationPdfTextLength; i < n; ++i) {
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

    @Override
    public Map<Boolean, String> doProcess(Object helper) {
        PDFHelper pdfHelper = (PDFHelper) helper;
        Map<Boolean, String> validateResultsMap = new HashMap<>();
        try {
            if (this.model.getSearchStrings() != null) {
                validateResultsMap.put(verifyPDFContainsAllStrings(pdfHelper),
                        String.format("PDF content '%s' does not contains data '%s'",
                                pdfHelper.getPDFContent(), Arrays.toString(this.model.getSearchStrings())));
            }
            if (this.model.getFont() != null) {
                validateResultsMap.put(verifyPDFFontForStrings(this.model.getFont(), pdfHelper),
                        String.format("PDF content does not contains data '%s' of font '%s'",
                                Arrays.toString(this.model.getSearchStrings()), this.model.getFont()));
            }
            if (this.model.getSize() != null) {
                validateResultsMap.put(verifyPDFContentSizeForStrings(this.model.getSize(), pdfHelper),
                        String.format("PDF content does not contains data '%s' of size '%s'",
                                Arrays.toString(this.model.getSearchStrings()), this.model.getSize().toString()));
            }
            if (this.model.getType() != null) {
                validateResultsMap.put(verifyPDFContentTextTypeForStrings(this.model.getType(), pdfHelper),
                        String.format("PDF content does not contains data '%s' of type '%s'",
                                Arrays.toString(this.model.getSearchStrings()), this.model.getType()));
            }
            if (this.model.getImages() != null) {
                validateResultsMap.put(verifyPDFImages(this.model.getImages(), pdfHelper),
                        "PDF images does not contains expected images");
            }
        } catch (Throwable t) {
            log.fatal(ExceptionUtils.getStackTrace(t));
        }
        return validateResultsMap;
    }
}
