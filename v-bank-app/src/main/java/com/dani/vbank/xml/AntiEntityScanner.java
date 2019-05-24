package com.dani.vbank.xml;

import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

public class AntiEntityScanner {

    private static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";

    public static void check(final InputStream data) throws Exception {
        final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        final ElementHandler handler = new ElementHandler();
        saxParser.getXMLReader().setProperty(LEXICAL_HANDLER, handler);
        saxParser.parse(data, handler);
    }

    public static final class ElementHandler extends org.xml.sax.ext.DefaultHandler2 {
        @Override
        public void startEntity(final String name) throws SAXException {
            throw new IllegalArgumentException("Entities are illegal");
        }
    }
}
