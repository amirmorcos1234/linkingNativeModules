package ro.vodafone.mcare.android.rest.security;


import android.net.Uri;
import android.util.Log;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import ro.vodafone.mcare.android.application.VodafoneController;

//FIXME: clean this up
public class SSLUtils {
    public static void pinCertificates(String host, OkHttpClient.Builder clientBuilder, String[] certificates){
        if((certificates == null) || (certificates.length == 0))
            return;

        host = Uri.parse(host).getHost();

        final InputStream[] certs = new InputStream[certificates.length];
        for(int i=0; i < certificates.length; i++){
            certs[i] = SSLUtils.getCertificateAsInputStream(certificates[i]);
        }

        try {

            final Triple<X509KeyManager, ChainedPinnedTrustManager, Collection<Certificate>> managers = SSLUtils.trustManagerForCertificates(certs);
            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
            } catch (Exception e) {
                sslContext = SSLContext.getDefault();
            }
            sslContext.init(new KeyManager[]{ managers.getLeft() }, new TrustManager[] { managers.getMiddle() }, null);

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            /*
            CertificatePinner.Builder builder = new CertificatePinner.Builder();
            final Certificate[] c = managers.getRight().toArray(new Certificate[managers.getRight().size()]);

            toPin:
            for(int i=0; i<c.length; i++) {
                final Certificate cert = c[i];
                for(int j=0; j<c.length; j++){
                  if((i!=j) && (c[i].equals(c[j])))
                      //skip Root CA certificates
                      continue toPin;
                }
                Log.d(SSLUtils.class.getName(), "Adding pin: " + CertificatePinner.pin(cert) + " for " + host);// + " from " + cert.toString());
                //builder.add(host, "sha256/5kJvNEMw0KjrCAu7eXY5HZdvyCS13BbA0VJG1RSP91w="); //CertificatePinner.pin(cert));
                //builder.add(host, "sha256/H5APT38M+k4M0LEv3T4+J7D3iUm/brBYMoOSn9BJfFc="); //CertificatePinner.pin(cert));
                builder.add(host, CertificatePinner.pin(cert));
            }*/

            clientBuilder.sslSocketFactory(sslSocketFactory, managers.getMiddle());
            clientBuilder.addNetworkInterceptor(new Interceptor() {
                private final ChainedPinnedTrustManager trustManager = managers.getMiddle();
                @Override
                public Response intercept(Chain chain) throws IOException {
                    if(!chain.request().isHttps())
                        return chain.proceed(chain.request());
                    final X509Certificate[] peerCerts = chain.connection().handshake().peerCertificates().toArray(new X509Certificate[0]);

                    try {
                        trustManager.checkServerTrusted(peerCerts, "");
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                    return chain.proceed(chain.request());
                }
            });
            //clientBuilder.certificatePinner(builder.build());
        } catch (Exception e) {
            Log.e(SSLUtils.class.getName(), "CANNOT PIN CERTIFICATES");
            return;
            //throw new IllegalStateException("Could not pin certificates", e);
        }
    }

    public static InputStream getCertificateAsInputStream(String certificateFilename){
        try {
            return VodafoneController.getInstance().getAssets().open(certificateFilename);
        } catch (IOException e) {
            throw new IllegalStateException("Certificate file not found: " + certificateFilename, e);
        }
    }

    /**
     * Returns a trust manager that trusts {@code certificates} and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a {@code
     * SSLHandshakeException}.
     *
     * <p>This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     *
     * <p>See also {@link CertificatePinner}, which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     *
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     *
     * <p>Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    public static Triple<X509KeyManager, ChainedPinnedTrustManager, Collection<Certificate>> trustManagerForCertificates(InputStream[] streams)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<Certificate> certificates = new LinkedList<>();
        Collection<PinnedTrustManager> trustManagers = new LinkedList<>();
        for(InputStream in : streams) {
            Collection certs = certificateFactory.generateCertificates(in);
            trustManagers.add(new PinnedTrustManager((Collection<Certificate>) certs));
            certificates.addAll(certs);
        }

        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "q1w2e3".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = VodafoneController.getInstance().getPackageName() + "." + Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
        keyManagerFactory.init(keyStore, password);
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        if (keyManagers.length != 1 || !(keyManagers[0] instanceof X509KeyManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(keyManagers));
        }

        /*TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }*/

        return new ImmutableTriple<>((X509KeyManager)keyManagers[0], new ChainedPinnedTrustManager(trustManagers), certificates);
    }

    private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //Enumeration<String> aliases = keyStore.aliases();
            /*if((aliases != null) && (aliases.hasMoreElements())) {
                final String alias = keyStore.aliases().nextElement();
                keyStore.deleteEntry(alias);
            }*/
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private SSLUtils() {}

    static class PinnedTrustManager implements X509TrustManager {
        final Certificate[] certificates;

        public PinnedTrustManager(Collection<Certificate> certificates) {
            this.certificates = certificates.toArray(new Certificate[certificates.size()]);
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if(chain.length != certificates.length)
                throw new CertificateException("Different chain legth");
            boolean[] matchedInChain = new boolean[chain.length];
            boolean[] matchedPins = new boolean[chain.length];
            Arrays.fill(matchedInChain, false);
            Arrays.fill(matchedPins, false);

            checkedCertificate:
            for(int i=0; i< chain.length; i++) {
                X509Certificate certificate = chain[i];
                for (int j=0; j<certificates.length; j++) {
                    Certificate pinnedCertificate = certificates[j];
                    if (pinnedCertificate.equals(certificate)){
                        matchedInChain[i] = true;
                        matchedPins[j] = true;
                        continue checkedCertificate;
                    }
                }
            }

            for(int i=0; i<matchedInChain.length; i++){
                if(!matchedPins[i] || !matchedInChain[i])
                    throw new CertificateException("Chain not matching fully");
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    static class ChainedPinnedTrustManager implements X509TrustManager {
        final Collection<PinnedTrustManager> trustManagers;

        public ChainedPinnedTrustManager(Collection<PinnedTrustManager> managers){
            this.trustManagers = managers;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            Log.i(ChainedPinnedTrustManager.class.getName(), "Checking chain...");
            for(PinnedTrustManager manager: trustManagers){
                try {
                    manager.checkServerTrusted(chain, authType);
                    return;
                } catch (Exception e) {
                    Log.i(SSLUtils.class.getSimpleName(), "Certificate not matched, skipping...");
                    //chain not valid, trying backup chains
                }
            }
            Log.e(SSLUtils.class.getSimpleName(), "Server certificate didn't match any pinned certificate. Stopping SSL handshake");
            throw new CertificateException("Chain could not be validated");
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
