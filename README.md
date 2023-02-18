# Laverca-REST

Laverca-REST is a simple client library for Kiuru REST API. It uses a JSON format that closely mimics the _ETSI TS 102 204_ SOAP API.

Features
=========
1. User authentication
2. Certificate retrieval
3. Simple document signing

Usage
=====

Authentication
-----
``` 
MssClient client = MssClient.initWithPassword("TestAP", "9TMzfH7EKXETOB8FT5gz", "https://demo.methics.fi/restapi/")
try {
    MSS_SignatureResp resp = client.authenticate("35847001001", "Authentication test", "http://alauda.mobi/digitalSignature");
    if (resp.isSuccess()) {
        System.out.println("Successfully authenticated " + resp.getSubjectDN()); 
    }
} catch (RestException e) {
    System.out.println("Failed to authenticate user", e);
}
``` 

PDF Signing
-----

```
MssClient client = MssClient.initWithPassword("TestAP", "9TMzfH7EKXETOB8FT5gz", "https://demo.methics.fi/restapi/")
PdfSigner signer = new PdfSigner(client);                                                                                       
                                                                                                                                
File pdf = new File("C:\\test.pdf");                                                                                            
InputStream is = new FileInputStream(pdf);                                                                                      
ByteArrayOutputStream  os = signer.signDocument("35847001001", "Please sign test.pdf", is, "http://alauda.mobi/nonRepudiation");
try (FileOutputStream fos = new FileOutputStream(new File("C:\\test.signed.pdf"))) {                                            
    os.writeTo(fos);                                                                                                            
    os.flush();                                                                                                                 
}                                                                                                                               
```
