package popo.pdfparse.framework.util.pdf;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import popo.pdfparse.framework.util.pdf.pdftable.PdfTableReader;
import popo.pdfparse.framework.util.pdf.pdftable.models.ParsedTablePage;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

@Log4j2
public class PDFTextStripperHelper extends PDFTextStripper {

    private Map<TextPosition, PDGraphicsState> charactersMap = new LinkedHashMap<>();
    private String pdfContent;

    public PDFTextStripperHelper(InputStream pdfInputStream) throws IOException {
        super.document = getPDDocument(pdfInputStream);
        super.writeText(super.document, new StringWriter());
    }

    public List<List<TextPosition>> getCharactersByArticle() {
        return super.charactersByArticle;
    }

    public Map<TextPosition, PDGraphicsState> getCharactersMap() {
        return this.charactersMap;
    }

    public PDDocument getDocument() {
        return super.document;
    }

    public String getText() {
        try {
            if (pdfContent == null) {
                pdfContent = super.getText(Objects.requireNonNull(getDocument()));
            }
            return pdfContent;
        } catch (IOException e) {
            log.fatal(ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }
        return pdfContent;
    }

    public List<ParsedTablePage> getParsedTable() {
        List<ParsedTablePage> parsed = null;
        try {
            parsed = new PdfTableReader().parsePdfTablePages(super.document, 1, super.document.getNumberOfPages());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsed;
    }

    public synchronized List<RenderedImage> getImagesFromPDF() {
        List<RenderedImage> images = new ArrayList<>();
        for (PDPage page : getDocument().getPages()) {
            images.addAll(getImagesFromResources(page.getResources()));
        }
        return images;
    }

    private synchronized List<RenderedImage> getImagesFromResources(PDResources resources) {
        List<RenderedImage> images = new ArrayList<>();
        for (COSName xObjectName : resources.getXObjectNames()) {
            PDXObject xObject;
            try {
                xObject = resources.getXObject(xObjectName);
                if (xObject instanceof PDFormXObject) {
                    images.addAll(getImagesFromResources(((PDFormXObject) xObject).getResources()));
                } else if (xObject instanceof PDImageXObject) {
                    images.add(((PDImageXObject) xObject).getImage());
                }
            } catch (IOException e) {
                log.fatal(ExceptionUtils.getStackTrace(e));
                e.printStackTrace();
            }
        }
        return images;
    }

    private synchronized PDDocument getPDDocument(InputStream pdfInputStream) {
        try {
            PDFParser pdfParser = new PDFParser(new RandomAccessBufferedFileInputStream(pdfInputStream));
            pdfParser.parse();
            COSDocument cosDoc = pdfParser.getDocument();
            return new PDDocument(cosDoc);
        } catch (IOException e) {
            log.fatal(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        if (pdfContent == null) {
            this.charactersMap.put(text, this.getGraphicsState());
        }
        super.processTextPosition(text);
    }
}
