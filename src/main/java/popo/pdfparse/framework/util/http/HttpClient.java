package popo.pdfparse.framework.util.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Objects;

public class HttpClient {

    private String url;
    private HttpGet httpGet;
    private HttpClientBuilder builder = HttpClientBuilder.create();
    private CloseableHttpClient client;
    private CloseableHttpResponse closeableHttpResponse;

    public HttpClient(String url) {
        this.url = url;
    }

    public HttpClient fetchHttpGet() {
        this.httpGet = new HttpGet(Objects.requireNonNull(this.url));
        return this;
    }

    public HttpClient setSSLContext() {
        this.builder.setSSLContext(new TrustAnyTrustManager().getTrustingAllSslContext());
        return this;
    }

    public HttpClient setCookieStore(BasicCookieStore cookieStore) {
        this.builder.setDefaultCookieStore(cookieStore);
        return this;
    }

    public HttpClient execute() throws IOException {
        this.client = this.builder.build();
        this.closeableHttpResponse = client.execute(httpGet);
        return this;
    }

    public CloseableHttpResponse getCloseableHttpResponse() {
        return this.closeableHttpResponse;
    }
}
