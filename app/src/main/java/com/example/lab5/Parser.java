package com.example.lab5;

import android.util.Log;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static final String TAG = "Parser";

    // Method to parse the XML response
    public static List<String> parseXML(String xmlContent) {
        List<String> rates = new ArrayList<>();
        try {
            Log.d(TAG, "Starting XML parsing...");

            // Parse the XML content directly
            InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();

            // Look for the Cube elements (they are now directly under the root element)
            NodeList cubeNodes = doc.getElementsByTagName("Cube");

            Log.d(TAG, "Number of Cube nodes found: " + cubeNodes.getLength());
            if (cubeNodes.getLength() == 0) {
                Log.e(TAG, "No Cube nodes found in the XML.");
            }

            // Traverse through each Cube node
            for (int i = 0; i < cubeNodes.getLength(); i++) {
                Node cubeNode = cubeNodes.item(i);
                if (cubeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) cubeNode;
                    if (element.hasAttribute("currency") && element.hasAttribute("rate")) {
                        String currency = element.getAttribute("currency");
                        String rate = element.getAttribute("rate");
                        rates.add(currency + ": " + rate);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing XML", e);
        }
        return rates;
    }
}