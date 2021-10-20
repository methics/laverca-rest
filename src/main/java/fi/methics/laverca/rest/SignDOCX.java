package fi.methics.laverca.rest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.facets.KeyInfoSignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.OOXMLSignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.Office2010SignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.XAdESSignatureFacet;

import fi.methics.laverca.rest.docx.DummyPrivateKey;
import fi.methics.laverca.rest.docx.MssDOMXMLSignatureFactory;
import fi.methics.laverca.rest.docx.MssSignatureInfo;

public class SignDOCX {

    // PWD authn
    private static final String AP_NAME       = "laverca-test";
    private static final String PASSWORD      = "3Zqka5MR1vfzzSxPHIYf";
    
    // API_KEY authn
    private static final String AP_ID         = "http://laverca-test";
    private static final String API_KEY       = "h4h428QAiNuhnljvw9U1fsYRUDwuv9ytDzAiepv1ywtvyJpy";
    
    private static final String MSISDN        = "35847001001";
    private static final String SIG_PROFILE   = "http://alauda.mobi/digitalSignature";
    private static final String REST_URL      = "http://localhost:9069/rest/service";
    
    public static void main(String[] args) throws Exception {
        new SignDOCX().run();
    }
    
    public void run() throws Exception {

        MssClient            client = MssClient.initWithPassword(AP_NAME, PASSWORD, REST_URL);
        List<X509Certificate> chain = client.getCertificateChain(MSISDN, SIG_PROFILE);
        
        System.out.println("Got cert chain of size " + chain.size() + ":");
        System.out.println(chain.stream().map(c -> c.getSubjectDN().toString()).collect(Collectors.toList()));
        
        SignatureConfig signatureConfig = new SignatureConfig();
        signatureConfig.setSigningCertificateChain(chain);
        signatureConfig.setIncludeEntireCertificateChain(true);
        signatureConfig.setKey(new DummyPrivateKey());

        signatureConfig.setSignatureFacets(Arrays.asList(new OOXMLSignatureFacet(), 
                                                         new KeyInfoSignatureFacet(),
                                                         new XAdESSignatureFacet(),
                                                         new Office2010SignatureFacet()));
        
        
        File example = new File("./example.docx");
        File signed  = new File("./example.signed.docx");
        Files.copy(example.toPath(), signed.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        OPCPackage pkg = OPCPackage.open("./example.signed.docx", PackageAccess.READ_WRITE);
        
        MssSignatureInfo si = new MssSignatureInfo(client, MSISDN, SIG_PROFILE);
        si.setOpcPackage(pkg);
        si.setSignatureConfig(signatureConfig);
        si.setSignatureFactory(new MssDOMXMLSignatureFactory());
        si.confirmSignature();
        
        // optionally verify the generated signature
        si.verifySignature();

        // write the changes back to disc
        pkg.close();
    }
    
}
