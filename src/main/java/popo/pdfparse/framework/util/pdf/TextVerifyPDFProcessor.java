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
import popo.pdfparse.framework.util.pdf.models.PDFProcessModel;
import popo.pdfparse.framework.util.pdf.pdftable.models.ParsedTablePage;
import popo.pdfparse.framework.util.pdf.types.PDFFontType;
import popo.pdfparse.framework.util.pdf.types.PDFTextType;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
                    e.printStackTrace();
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

    private Map<Boolean, String> verifyPDFTable(PDFHelper pdfHelper) {
        List<ParsedTablePage> parsedTablePages = getOptimizedPDFTableContent(pdfHelper.getParsedTablePages());
        if (this.model.getPdfTableModel().getColumns() != null && !this.model.getPdfTableModel().getColumns().isEmpty()) {
            return verifyColumnsPDFTable(parsedTablePages);
        }
        if (this.model.getPdfTableModel().getTableValidationMap() != null
                && !this.model.getPdfTableModel().getTableValidationMap().isEmpty()) {
            return verifyContentColumnPDFTable(parsedTablePages);
        }
        log.debug("PDF table validation did not completed");
        return null;
    }

    private Map<Boolean, String> verifyColumnsPDFTable(List<ParsedTablePage> parsedTablePages) {
        List<String> pdfTableColumns = getPDFTableColumns(parsedTablePages);
        List<String> columns =
                this.model.getPdfTableModel().getColumns().stream().map(this::getCleanPDFContent).collect(Collectors.toList());
        Collections.sort(pdfTableColumns);
        Collections.sort(columns);
        return new HashMap<Boolean, String>() {{
            put(pdfTableColumns.equals(columns),
                    String.format("Columns from PDF table: %s; Expected columns of PDF table: %s", pdfTableColumns.toString(), columns.toString()));
        }};
    }

    private List<String> getPDFTableColumns(List<ParsedTablePage> pdfTableContent) {
        return pdfTableContent.get(0).getRow(0).getCells().stream().map(this::getCleanPDFContent).collect(Collectors.toList());
    }

    private Map<Boolean, String> verifyContentColumnPDFTable(List<ParsedTablePage> parsedTablePages) {
        removeFirstRowTable(parsedTablePages, 0);
        Map<Boolean, String> verifyMap = new HashMap<>();
        for (Map.Entry<String, String> entry : this.model.getPdfTableModel().getTableValidationMap().entrySet()) {
            String column = entry.getKey();
            String verifyContent = getCleanPDFContent(entry.getKey());
            int columnIndex = getColumnIndex(column, getPDFTableColumns(parsedTablePages));
            parsedTablePages.forEach(page ->
                    page.getRows().forEach(cell -> {
                        String columnValue = cell.getCell(columnIndex);
                        if (!columnValue.equals(verifyContent)) {
                            verifyMap.put(Boolean.FALSE, String.format("Column '%s' has invalid value '%s' on the page number %d",
                                    column, columnValue, parsedTablePages.indexOf(page) + 1));
                        }
                    }));
        }
        return verifyMap;
    }

    private void removeFirstRowTable(List<ParsedTablePage> pdfTableContent, int pageNumber) {
        pdfTableContent.get(pageNumber).getRows().remove(0);
    }

    private List<ParsedTablePage> getCleanPDFTableContent(List<ParsedTablePage> parsedTablePages) {
        for (ParsedTablePage parsedTablePage : parsedTablePages) {
            for (int iRows = 0; iRows < parsedTablePage.getRows().size(); ++iRows) {
                boolean isEmptyRow = parsedTablePage.getRows().get(iRows).getCells().stream().allMatch(cell -> cell.equals("\r\n"));
                if (isEmptyRow) {
                    parsedTablePage.getRows().remove(iRows);
                    --iRows;
                } else {
                    List<String> cellsContent = parsedTablePage.getRows().get(iRows).getCells();
                    for (int iCell = 0, cells = cellsContent.size(); iCell < cells; ++iCell) {
                        cellsContent.set(iCell, getCleanPDFContent(cellsContent.get(iCell)));
                    }
                }
            }
        }
        return parsedTablePages;
    }

    private int getColumnIndex(String column, List<String> columns) {
        return columns.indexOf(getCleanPDFContent(column));
    }

    private List<ParsedTablePage> getOptimizedPDFTableContent(List<ParsedTablePage> parsedTablePages) {
        List<ParsedTablePage> pdfTableContent = getCleanPDFTableContent(parsedTablePages);

        for (int iPages = 1, pages = pdfTableContent.size(); iPages < pages; ++iPages) {
            removeFirstRowTable(pdfTableContent, iPages);
            List<String> cellsChildContent = pdfTableContent.get(iPages).getRows().get(0).getCells();
            boolean isChildRow = cellsChildContent.stream().anyMatch(String::isEmpty);
            if (isChildRow) {
                List<String> cellsParentContent = pdfTableContent.get(iPages - 1).getRows().get(pdfTableContent.get(iPages - 1).getRows().size() - 1).getCells();
                for (int i = 0; i < cellsChildContent.size(); ++i) {
                    cellsParentContent.set(i, cellsParentContent.get(i) + cellsChildContent.get(i));
                }
                removeFirstRowTable(pdfTableContent, iPages);
            }
        }
        return pdfTableContent;
    }

    @Override
    public Map<Boolean, String> doProcess(Object helper) {
        PDFHelper pdfHelper = (PDFHelper) helper;
        Map<Boolean, String> validateResultsMap = new HashMap<>();
        try {
            if (this.model.getSearchStrings() != null) {
                validateResultsMap.put(verifyPDFContainsAllStrings(pdfHelper),
                        String.format("PDF content '%s' does not contains data '%s'",
                                pdfHelper.getPDFContent(), this.model.getSearchStrings()));
            }
            if (this.model.getFont() != null) {
                validateResultsMap.put(verifyPDFFontForStrings(this.model.getFont(), pdfHelper),
                        String.format("PDF content does not contain data '%s' of font '%s'",
                                this.model.getSearchStrings(), this.model.getFont()));
            }
            if (this.model.getSize() != null) {
                validateResultsMap.put(verifyPDFContentSizeForStrings(this.model.getSize(), pdfHelper),
                        String.format("PDF content does not contain data '%s' of size '%s'",
                                this.model.getSearchStrings(), this.model.getSize().toString()));
            }
            if (this.model.getType() != null) {
                validateResultsMap.put(verifyPDFContentTextTypeForStrings(this.model.getType(), pdfHelper),
                        String.format("PDF content does not contain data '%s' of type '%s'",
                                this.model.getSearchStrings(), this.model.getType()));
            }
            if (this.model.getImages() != null) {
                validateResultsMap.put(verifyPDFImages(this.model.getImages(), pdfHelper),
                        "PDF images does not contain expected images");
            }
            if (this.model.getPdfTableModel() != null) {
                validateResultsMap.putAll(Objects.requireNonNull(verifyPDFTable(pdfHelper)));
            }
        } catch (Throwable t) {
            log.fatal(t);
            t.printStackTrace();
        }
        return validateResultsMap;
    }
}
