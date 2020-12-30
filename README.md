# aws-cloudfront-signed-cookies-java
A sample java code to produce signed cookies/url request in CloudFront with Canned and Custom policies

## CloudFront Console Configuration
You might refer to the Official Doc [Creating key pairs for your signers](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/private-content-trusted-signers.html#private-content-creating-cloudfront-key-pairs) to finish the following steps

1. Create a key pair for a trusted key group (recommended)

    To create a key pair for a trusted key group, perform the following steps:
    
    - Create the public–private key pair.
    
    The following example command uses OpenSSL to generate an RSA key pair with a length of 2048 bits and save to the file named private_key.pem.
    
    ```
    openssl genrsa -out private_key.pem 2048
    ```
    
    - Upload the public key to CloudFront.
    
    The resulting file contains both the public and the private key. The following example command extracts the public key from the file named private_key.pem.
    
    ```
    openssl rsa -pubout -in private_key.pem -out public_key.pem
    ```
    
    Signed URLs for a private distribution. Note that Java only supports SSL certificates in DER format, 
    so you will need to convert your PEM-formatted file to DER format. To do this, you can use openssl:
    
    ```
    openssl pkcs8 -topk8 -nocrypt -in origin.pem -inform PEM -out new.der -outform DER
    ```
    
    - Add the public key to a CloudFront key group.
    
    Locate to `CloudFront` console and select `Key Management` -> `Key Groups`
    
    Add Key Group to involve the public key you just upload to `CloudFront`
    
    ![create key group](https://github.com/nikosheng/aws-cloudfront-signed-cookies-java/blob/master/img/create-key-group.png)

2. Create Distribution  
![create distribution](https://github.com/nikosheng/aws-cloudfront-signed-cookies-java/blob/master/img/create-distribution%402x.png)

3. Origin Settings
![origin settings](https://github.com/nikosheng/aws-cloudfront-signed-cookies-java/blob/master/img/corigin-settings.png)

4. Cache Behavior Settings
![cache behavior settings](https://github.com/nikosheng/aws-cloudfront-signed-cookies-java/blob/master/img/cache-behavior.png)

## Code Setup

**Replace the cloudfront domain / s3ObjectKey /  keyPairId in the sample code**

You might also change the `iprange` to resrict the ip cidr to visit your contents.


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

