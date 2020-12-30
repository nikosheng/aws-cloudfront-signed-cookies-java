package aws.cloudfront;

import com.amazonaws.services.cloudfront.CloudFrontCookieSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.joda.time.Instant;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * CloudFront Signed Cookies Sample
 *
 * Author: Niko Feng (nikosheng@gmail.com)
 */
public class App 
{
    public static void main(String[] args) throws IOException, InvalidKeySpecException {
        /**
         * CloudFront Basic Configuration including domain, private key and resource path
         *
         * Signed URLs for a private distribution
         * Note that Java only supports SSL certificates in DER format,
         * so you will need to convert your PEM-formatted file to DER format.
         * To do this, you can use openssl:
         * openssl pkcs8 -topk8 -nocrypt -in origin.pem -inform PEM -out new.der -outform DER
         * So the encoder works correctly, you should also add the bouncy castle jar
         * to your project and then add the provider.
         */
        Security.addProvider(new BouncyCastleProvider());
        String distributionDomain = "xxx.cloudfront.net";
        String privateKeyFilePath = Objects.requireNonNull(App.class.getClassLoader().getResource("private_key.der")).getPath();
        File privateKeyFile = new File(privateKeyFilePath);
        String s3ObjectKey = "img/aws-web.png";

        /**
         * CloudFront Policy Setup
         */
        SignerUtils.Protocol protocol = SignerUtils.Protocol.https;
        String keyPairId = "YOUR_KEY_PAIR_ID";
        Instant activeFrom = Instant.now();
        Instant expiresOn = activeFrom.plus(TimeUnit.HOURS.toMillis(24));
        String ipRange = "0.0.0.0/0";

        /**
         * CookiesForCannedPolicy Setup
         *
         * Return the cookies which could be attached to the following request with cookies
         */
//        CloudFrontCookieSigner.CookiesForCannedPolicy cookies = CloudFrontCookieSigner.getCookiesForCannedPolicy(
//                protocol, distributionDomain, privateKeyFile, s3ObjectKey,
//                keyPairId, expiresOn.toDate());

        /**
         * CookiesForCustomPolicy Setup
         *
         * Return the cookies which could be attached to the following request with cookies
         */
        CloudFrontCookieSigner.CookiesForCustomPolicy cookies = CloudFrontCookieSigner.getCookiesForCustomPolicy(
                protocol, distributionDomain, privateKeyFile, s3ObjectKey,
                keyPairId, expiresOn.toDate(), activeFrom.toDate(), ipRange);

        /**
         * Setup the httpclient to validate the request
         */
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(
                SignerUtils.generateResourcePath(protocol, distributionDomain,
                        s3ObjectKey));

//        addCannedPolicyHeader(httpGet, cookies);
        addCustomPolicyHeader(httpGet, cookies);

        HttpResponse response = client.execute(httpGet);
    }

    private static void addCannedPolicyHeader(HttpGet httpGet, CloudFrontCookieSigner.CookiesForCannedPolicy cookies) {
        httpGet.addHeader("Cookie", cookies.getExpires().getKey() + "=" +
                cookies.getExpires().getValue());
        httpGet.addHeader("Cookie", cookies.getSignature().getKey() + "=" +
                cookies.getSignature().getValue());
        httpGet.addHeader("Cookie", cookies.getKeyPairId().getKey() + "=" +
                cookies.getKeyPairId().getValue());
    }

    private static void addCustomPolicyHeader(HttpGet httpGet, CloudFrontCookieSigner.CookiesForCustomPolicy cookies) {
        httpGet.addHeader("Cookie", cookies.getPolicy().getKey() + "=" +
                cookies.getPolicy().getValue());
        httpGet.addHeader("Cookie", cookies.getSignature().getKey() + "=" +
                cookies.getSignature().getValue());
        httpGet.addHeader("Cookie", cookies.getKeyPairId().getKey() + "=" +
                cookies.getKeyPairId().getValue());
    }
}
