package popo.pdfparse.framework.util.http;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Log4j2
public class TrustAnyTrustManager {

    private static final TrustManager[] TRUST_ALL_MANAGER = new TrustManager[]{new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // Ignored
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // Ignored
        }
    }};

    SSLContext getTrustingAllSslContext() {
        try {
            SSLContext trustingAllSslContext = SSLContext.getInstance(org.apache.http.conn.ssl.SSLConnectionSocketFactory.TLS);
            trustingAllSslContext.init(null, TRUST_ALL_MANAGER, new SecureRandom());
            return trustingAllSslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.fatal(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
}