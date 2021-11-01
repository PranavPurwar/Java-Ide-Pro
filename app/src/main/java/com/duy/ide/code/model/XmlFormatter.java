package com.duy.ide.code.model;

import android.support.annotation.Nullable;
import com.duy.ide.code.api.CodeFormatter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlFormatter implements CodeFormatter {

   private CharSequence formatImpl(CharSequence string) throws TransformerException, ParserConfigurationException, IOException, SAXException {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(string.toString())));
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      StreamResult stream = new StreamResult(new StringWriter());
      transformer.transform(new DOMSource(document), stream);
      return stream.getWriter().toString();
   }

   @Nullable
   public CharSequence format(CharSequence sequence) {
      try {
         sequence = this.formatImpl(sequence);
         return sequence;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }
}