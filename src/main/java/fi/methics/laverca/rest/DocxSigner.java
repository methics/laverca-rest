//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.facets.KeyInfoSignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.OOXMLSignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.Office2010SignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.XAdESSignatureFacet;

import fi.methics.laverca.rest.docx.DummyPrivateKey;
import fi.methics.laverca.rest.docx.MssDOMXMLSignatureFactory;
import fi.methics.laverca.rest.docx.MssSignatureInfo;
import fi.methics.laverca.rest.util.DocumentSigner;
import fi.methics.laverca.rest.util.RestException;

/**
 * DOCX document signing helper class
 * <p><b>Note:</b> This class is not thread safe. The recommended usage is to create a new PdfSigner for each document.
 * <p>Usage:
 * <pre>
 *  MssClient client = ...
 *  DocxSigner signer = new DocxSigner(client);
 *  
 *  File doc = new File("C:\\test.docx");
 *  InputStream is = new FileInputStream(doc);
 *  ByteArrayOutputStream  os = signer.signDocument("35847001001", "Please sign test.docx", is, "http://alauda.mobi/nonRepudiation");
 *  try (FileOutputStream fos = new FileOutputStream(new File("C:\\test.signed.docx"))) {
 *      os.writeTo(fos);
 *      os.flush();
 *  }
 * </pre>
 */
public class DocxSigner extends DocumentSigner {

    public DocxSigner(MssClient client) {
        super(client);
    }

    @Override
    public ByteArrayOutputStream signDocument(final String msisdn,
                                              final String message,
                                              final InputStream is,
                                              final String signatureProfile)
        throws IOException, RestException 
    {

        List<X509Certificate> chain = this.client.getCertificates(msisdn, signatureProfile);
        
        SignatureConfig signatureConfig = new SignatureConfig();
        signatureConfig.setSigningCertificateChain(chain);
        signatureConfig.setIncludeEntireCertificateChain(true);
        signatureConfig.setKey(new DummyPrivateKey());

        signatureConfig.setSignatureFacets(Arrays.asList(new OOXMLSignatureFacet(), 
                                                         new KeyInfoSignatureFacet(),
                                                         new XAdESSignatureFacet(),
                                                         new Office2010SignatureFacet()));
        
        try {
            OPCPackage pkg = OPCPackage.open(is);
            
            MssSignatureInfo si = new MssSignatureInfo(this.client, msisdn, message, signatureProfile);
            si.setOpcPackage(pkg);
            si.setSignatureConfig(signatureConfig);
            si.setSignatureFactory(new MssDOMXMLSignatureFactory());
            si.confirmSignature();
            
            // optionally verify the generated signature
            si.verifySignature();
    
            // write the changes back to disc
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            pkg.save(os);
            pkg.close();
            return os;
        } catch (MarshalException | XMLSignatureException | InvalidFormatException e) {
            throw new IOException(e);
        }
    }

}
