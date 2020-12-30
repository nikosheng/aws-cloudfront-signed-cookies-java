# aws-cloudfront-signed-cookies-java
A sample java code to produce signed cookies request in CloudFront with Canned and Custom policies

## Setup

*Replace the cloudfront domain / s3ObjectKey /  keyPairId in the sample code*

Signed URLs for a private distribution. Note that Java only supports SSL certificates in DER format, 
so you will need to convert your PEM-formatted file to DER format. To do this, you can use openssl:

```
openssl pkcs8 -topk8 -nocrypt -in origin.pem -inform PEM -out new.der -outform DER
```

So the encoder works correctly, you should also add the bouncy castle jar to your project and then add the provider.

```
Security.addProvider(new BouncyCastleProvider());
String distributionDomain = "xxx.cloudfront.net";
String privateKeyFilePath = Objects.requireNonNull(App.class.getClassLoader().getResource("private_key.der")).getPath();
File privateKeyFile = new File(privateKeyFilePath);
String s3ObjectKey = "img/aws-web.png";

SignerUtils.Protocol protocol = SignerUtils.Protocol.https;
String keyPairId = "YOUR_KEY_PAIR_ID";
Instant activeFrom = Instant.now();
Instant expiresOn = activeFrom.plus(TimeUnit.HOURS.toMillis(24));
String ipRange = "0.0.0.0/0";
```

