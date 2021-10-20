package fi.methics.laverca.rest.docx;

import static org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet.XML_DIGSIG_NS;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.dom.DOMSignContext;

import org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo;
import org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fi.methics.laverca.rest.MssClient;

/**
 * Apache POI SignatureInfo extension that uses {@link MssDigestOutputStream}
 * to request signatures from MSSP
 */
public class MssSignatureInfo extends SignatureInfo {

    private static final POILogger LOG = POILogFactory.getLogger(MssSignatureInfo.class);

    private MssClient client;
    private String    msisdn;
    private String    sigprof;
    
    public MssSignatureInfo(final MssClient client, final String msisdn, final String sigprof) {
        this.client  = client;
        this.msisdn  = msisdn;
        this.sigprof = sigprof;
    }
    
    @Override
    public String signDigest(DOMSignContext xmlSignContext, DOMSignedInfo signedInfo) {
        this.initXmlProvider();
        try (final MssDigestOutputStream dos = new MssDigestOutputStream(this.client, this.msisdn, this.sigprof)) {
            dos.init();

            final Document document = (Document)xmlSignContext.getParent();
            final Element  el       = getDsigElement(document, "SignedInfo");
            final DOMSubTreeData subTree = new DOMSubTreeData(el, true);
            signedInfo.getCanonicalizationMethod().transform(subTree, xmlSignContext, dos);

            return Base64.getEncoder().encodeToString(dos.sign());
        } catch (GeneralSecurityException|IOException|TransformException e) {
            throw new EncryptedDocumentException(e);
        }
    }
    
    private Element getDsigElement(final Document document, final String localName) {
        NodeList sigValNl = document.getElementsByTagNameNS(XML_DIGSIG_NS, localName);
        if (sigValNl.getLength() == 1) {
            return (Element)sigValNl.item(0);
        }
        LOG.log(POILogger.WARN, "Signature element '", localName, "' was ", (sigValNl.getLength() == 0 ? "not found" : "multiple times"));
        return null;
    }
}
