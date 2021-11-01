/* Decompiler 3ms, total 439ms, lines 41 */
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
   private CharSequence formatImpl(CharSequence var1) throws TransformerException, ParserConfigurationException, IOException, SAXException {
      Document var2 = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(var1.toString())));
      Transformer var3 = TransformerFactory.newInstance().newTransformer();
      var3.setOutputProperty("indent", "yes");
      var3.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      StreamResult var4 = new StreamResult(new StringWriter());
      var3.transform(new DOMSource(var2), var4);
      return var4.getWriter().toString();
   }

   @Nullable
   public CharSequence format(CharSequence var1) {
      try {
         var1 = this.formatImpl(var1);
         return var1;
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }
}