package popo.pdfparse.framework.util.pdf;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;
import popo.pdfparse.framework.base.driver.Browser;
import popo.pdfparse.framework.util.http.HttpClient;
import popo.pdfparse.framework.util.pdf.pdftable.models.ParsedTablePage;

import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
public class PDFHelper {

    private String url;
    private Integer pageNumber = 0;
    private PDFTextStripperHelper pdfTextStripperHelper;

    public PDFHelper(String url) {
        this.url = url;
    }

    public PDFHelper setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    @SneakyThrows({ IOException.class })
    public PDFHelper fetchPDFTextStripperHelper() {
        if (url.contains(Browser.URL)) {
            @Cleanup InputStream inputStreamContent = new FileInputStream(new String(URLDecoder.decode(
                    url.replace(Browser.URL, ""), StandardCharsets.UTF_8.name()).getBytes(StandardCharsets.UTF_8)));
            pdfTextStripperHelper = new PDFTextStripperHelper(Objects.requireNonNull(inputStreamContent));
        } else {
            HttpClient httpClient = new HttpClient(url).fetchHttpGet();
            BasicCookieStore cookieStore = Browser.getDriverCookieStore();

            @Cleanup CloseableHttpResponse response = httpClient.setSSLContext().setCookieStore(cookieStore).execute().getCloseableHttpResponse();

            HttpEntity entity = Objects.requireNonNull(response).getEntity();
            InputStream inputStreamContent = entity.getContent();
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error(new HttpException("Error: " + response.getStatusLine()));
            }
            pdfTextStripperHelper = new PDFTextStripperHelper(Objects.requireNonNull(inputStreamContent));
        }
        int startPage = pageNumber;
        int endPage = pageNumber;
        int pages = pdfTextStripperHelper.getDocument().getNumberOfPages();
        if (pageNumber == 0) {
            startPage = 1;
            endPage = pages;
        }
        pdfTextStripperHelper.setStartPage(startPage);
        pdfTextStripperHelper.setEndPage(endPage);
        return this;
    }

    public String getPDFContent() {
        return Objects.requireNonNull(pdfTextStripperHelper).getText();
    }

    public List<ParsedTablePage> getParsedTablePages() {
        return Objects.requireNonNull(pdfTextStripperHelper).getParsedTable();
    }

    public Map<TextPosition, PDGraphicsState> getTextPositionPDGraphicsStateMap() {
        return Objects.requireNonNull(pdfTextStripperHelper).getCharactersMap();
    }

    public List<RenderedImage> getPDFImages() {
        return Objects.requireNonNull(pdfTextStripperHelper).getImagesFromPDF();
    }
}
