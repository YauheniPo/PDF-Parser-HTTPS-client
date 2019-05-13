package popo.pdfparse.framework.util.pdf;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.TextPosition;
import popo.pdfparse.framework.base.driver.Browser;
import popo.pdfparse.framework.util.http.HttpClient;

import java.awt.image.RenderedImage;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PDFHelper {

    private static PDFTextStripperHelper pdfTextStripperHelper;

    private synchronized static void fetchPDFTextStripperHelper(String urlStr) {
        HttpClient httpClient = new HttpClient(urlStr).fetchHttpGet();
        BasicCookieStore cookieStore = Browser.getDriverCookieStore();

        try (CloseableHttpResponse response = httpClient.setSSLContext().setCookieStore(cookieStore).execute().getCloseableHttpResponse()) {
            HttpEntity entity = Objects.requireNonNull(response).getEntity();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                pdfTextStripperHelper = new PDFTextStripperHelper(entity.getContent());
            } else {
                throw new HttpException("Error: " + response.getStatusLine());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public synchronized static String getPDFContent(String urlStr) {
        fetchPDFTextStripperHelper(urlStr);
        return Objects.requireNonNull(pdfTextStripperHelper).getText();
    }

    public synchronized static Map<TextPosition, PDGraphicsState> getTextPositionPDGraphicsStateMap() {
        return Objects.requireNonNull(pdfTextStripperHelper).getCharactersMap();
    }

    public synchronized static List<RenderedImage> getPDFImages(String urlStr) {
        fetchPDFTextStripperHelper(urlStr);
        return Objects.requireNonNull(pdfTextStripperHelper).getImagesFromPDF();
    }
}
