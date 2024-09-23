package fi.methics.laverca.rest.json;

import eu.europa.esig.dss.model.MimeType;
import fi.methics.laverca.rest.enums.Encoding;

public class Message {

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public Message() {

    }

    public Message(String data, Encoding encoding, MimeType mimeType) {
        super();
        this.data = data;
        this.encoding = encoding;
        this.mimeType = mimeType;
    }

    private String data;
    private Encoding encoding;
    private MimeType mimeType;
}
