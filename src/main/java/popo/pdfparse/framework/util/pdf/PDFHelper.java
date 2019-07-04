package popo.pdfparse.framework.util.pdf;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

    private static PDFTextStripperHelper pdfTextStripperHelper;

    public PDFHelper(String url) {
        fetchPDFTextStripperHelper(url);
    }

    private synchronized static void fetchPDFTextStripperHelper(String urlStr) {
        InputStream inputStreamContent = null;
        if (urlStr.contains(Browser.URL)) {
            try {
                inputStreamContent = new FileInputStream(new String(URLDecoder.decode(
                        urlStr.replace(Browser.URL, ""), "UTF-8").getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            HttpClient httpClient = new HttpClient(urlStr).fetchHttpGet();
            BasicCookieStore cookieStore = Browser.getDriverCookieStore();

            try (CloseableHttpResponse response = httpClient.setSSLContext().setCookieStore(cookieStore).execute().getCloseableHttpResponse()) {
                HttpEntity entity = Objects.requireNonNull(response).getEntity();
                inputStreamContent = entity.getContent();
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new HttpException("Error: " + response.getStatusLine());
                }
            } catch (Throwable t) {
                log.fatal(ExceptionUtils.getStackTrace(t));
            }
        }
        try {
            pdfTextStripperHelper = new PDFTextStripperHelper(Objects.requireNonNull(inputStreamContent));
            Objects.requireNonNull(inputStreamContent).close();
        } catch (IOException e) {
            log.fatal(ExceptionUtils.getStackTrace(e));
        }
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
