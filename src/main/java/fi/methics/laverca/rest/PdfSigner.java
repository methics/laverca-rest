//
//  (c) Copyright 2003-2023 Methics Oy. All rights reserved. 
//
package fi.methics.laverca.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.bouncycastle.cms.CMSSignedData;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.TimestampParameters;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import fi.methics.laverca.rest.util.DTBS;
import fi.methics.laverca.rest.util.DocumentSigner;
import fi.methics.laverca.rest.util.LavercaPAdESService;

/**
 * PDF document signing helper class
 * <p><b>Note:</b> This class is not thread safe. The recommended usage is to create a new PdfSigner for each document.
 * <p>Usage:
 * <pre>
 *  MssClient client = ...
 *  PdfSigner signer = new PdfSigner(client);
 *  
 *  File pdf = new File("C:\\test.pdf");
 *  InputStream is = new FileInputStream(pdf);
 *  ByteArrayOutputStream  os = signer.signDocument("35847001001", "Please sign test.pdf", is, "http://alauda.mobi/nonRepudiation");
 *  try (FileOutputStream fos = new FileOutputStream(new File("C:\\test.signed.pdf"))) {
 *      os.writeTo(fos);
 *      os.flush();
 *  }
 * </pre>
 */
public class PdfSigner extends DocumentSigner {

    private static final DigestAlgorithm DIGEST_ALG = DigestAlgorithm.SHA256;
    private static final String FIELD_NAME = "Signature";
    
    private PDAcroForm  form;
    private PDRectangle sigRectangle;
    private int         sigPage;
    
    public PdfSigner(final MssClient client) {
        super(client);
    }
    
    /**
     * Get AcroForm from the PDF, or create a new AcroForm if one does not exist.
     * @param pdf PDF
     * @return {@link PDAcroForm AcroForm}
     */
    public static PDAcroForm createOrGetAcroForm(final PDDocument pdf) {
        if (pdf.getDocumentCatalog().getAcroForm() != null) {
            return pdf.getDocumentCatalog().getAcroForm();
        }
        PDFont font = PDType1Font.HELVETICA;
        PDResources resources = new PDResources();
        resources.put(COSName.getPDFName("Helv"), font);
        
        // Add a new AcroForm and add that to the document
        PDAcroForm acroForm = new PDAcroForm(pdf);
        pdf.getDocumentCatalog().setAcroForm(acroForm);
        acroForm.setDefaultResources(resources);
        acroForm.setDefaultAppearance("/Helv 12 Tf 0 g");
        acroForm.setSignaturesExist(true);
        return acroForm;
    }
    
    /**
     * Create a new SignatureField
     * @param msisdn   MSISDN of the user
     * @param acroForm AcroForm
     * @param rect     Rectangle where the field will be placed
     * @param page     PDF page where the field will be placed
     * @return PDF signature field
     * @throws IOException
     */
    public static PDSignatureField createSignatureField(final String msisdn, final PDAcroForm acroForm, final PDRectangle rect, final PDPage page) throws IOException {
        PDSignatureField signatureField = new PDSignatureField(acroForm);
        signatureField.setPartialName(FIELD_NAME + msisdn);
        PDAnnotationWidget widget = signatureField.getWidgets().get(0);
        widget.setRectangle(rect);
        widget.setPage(page);
        page.getAnnotations().add(widget);
        return signatureField;
    }
    
    /**
     * Set an AcroForm
     * @param form AcroForm
     */
    public void setAcroForm(PDAcroForm form) {
        this.form = form;
    }
    
    /**
     * Set a visual signature rectangle.
     * A visual signature will be added in this place.
     * @param rec  Signature Rectangle
     * @param page Page where to put the 
     */
    public void setSignatureField(PDRectangle rec, int page) {
        this.sigRectangle = rec;
        this.sigPage      = page-1;
    }
    
    @Override
    public ByteArrayOutputStream signDocument(final String msisdn,
                                              final String message,
                                              final InputStream is,
                                              final String signatureProfile) 
        throws IOException
    {
        
        // Read given document and add a new SignatureField in it
        InMemoryDocument doc = new InMemoryDocument(is);
        
        if (this.form != null && this.sigRectangle != null) {
            doc = new InMemoryDocument(this.addSignatureField(msisdn, doc.getBytes()));
        }

        CommonCertificateVerifier verifier   = this.createVerifier();
        LavercaPAdESService       service    = this.createService(verifier);
        PAdESSignatureParameters  parameters = this.createParams(msisdn, signatureProfile);
        
        // Sign digest
        final byte[] digest    = service.computeDocumentDigest(doc, parameters);
        final byte[] signature = this.client.sign(msisdn, message, digest, DTBS.MIME_SHA256, signatureProfile);
        
        final CMSSignedData signedData;
        final DSSDocument   signedDoc;
        try {
            // Attach signature to PDF
            signedData = new CMSSignedData(signature);
            signedDoc  = service.signDocument(doc, parameters, signedData);
        } catch (Exception e) {
            throw new IOException(e);
        }
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        signedDoc.writeTo(os);
        return os;
    }
    
    /**
     * Add a signature field to the document
     * @param msisdn   Document signer MSISDN
     * @param document PDF document
     * @return Modified PDF document
     * @throws IOException
     */
    private byte[] addSignatureField(final String msisdn, final byte[] document) throws IOException {
        try (PDDocument pdf = PDDocument.load(document)) {
            this.addSignatureField(msisdn, this.form, pdf);
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                pdf.save(os);
                os.flush();
                return os.toByteArray();
            }
        } 
    }
    
    private void addSignatureField(final String msisdn, final PDAcroForm acroForm, final PDDocument pdf) throws IOException {
        PDPage           signaturePage  = pdf.getPage(this.sigPage); // this is 0 based
        PDRectangle      signatureBox   = this.sigRectangle;
        PDSignatureField signatureField = createSignatureField(msisdn, acroForm, signatureBox, signaturePage);
        
        if (!acroForm.getFields().stream().anyMatch(f -> Objects.equals(f.getPartialName(), signatureField.getPartialName()))) {
            acroForm.getFields().add(signatureField);
        } else {
            throw new IOException("PDF already has a signature field with the desired name");
        }
    }
    
    /**
     * Create ESIG PAdES parameters
     * @param msisdn MSISDN
     * @param sigprof SignatureProfile 
     * @return PAdES parameters
     */
    private PAdESSignatureParameters createParams(String msisdn, String sigprof) {
        List<X509Certificate> chain = this.client.getCertificates(msisdn, sigprof);
        PAdESSignatureParameters parameters = new PAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        parameters.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        parameters.setDigestAlgorithm(DIGEST_ALG);
        parameters.setSignatureSize(25600);
        parameters.setArchiveTimestampParameters(new TimestampParameters(DIGEST_ALG));
        parameters.setCertificateChain(chain.stream().map(CertificateToken::new).collect(Collectors.toList()));
        parameters.setSigningCertificate(new CertificateToken(chain.get(0)));
        if (this.form != null && this.sigRectangle != null) {
            parameters.setSignatureFieldId(FIELD_NAME + msisdn);
        }
        return parameters;
    }
    
    /**
     * Create ESIG PAdES service 
     * @return PAdES service
     */
    private LavercaPAdESService createService(CertificateVerifier verifier) {
        LavercaPAdESService service = new LavercaPAdESService(verifier);
        return service;
    }
    
    /**
     * Create ESIG PAdES verifier 
     * @return PAdES verifier
     */
    private CommonCertificateVerifier createVerifier() {
        CommonCertificateVerifier verifier = new CommonCertificateVerifier();
        verifier.setExceptionOnMissingRevocationData(false);
        verifier.setCheckRevocationForUntrustedChains(true);
        verifier.setIncludeCertificateRevocationValues(true);
        verifier.setOcspSource(new OnlineOCSPSource());
        verifier.setCrlSource(new OnlineCRLSource());
        return verifier;
    }
    
}
