package de.drbunsen.common.fluentxmlwrapper;

import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class FluentXmlWrapper {
    private final Element element;

    private FluentXmlWrapper(@NonNull Element element) {
        this.element = element;
    }

    private String getName() {
        return element.getTagName();
    }

    public FluentXmlWrapper addElement(@NonNull String name) {
        final Element newChild = element.getOwnerDocument().createElement(name);
        element.appendChild(newChild);
        return of(newChild);
    }

    public FluentXmlWrapper getElement(@NonNull String name) {
        final NodeList nodeList = element.getElementsByTagName(name);
        if (nodeList.getLength() > 0) {
            final Node node = nodeList.item(0);
            return of(node);
        }
        return null;
    }

    private static Element getElement(Node node) {
        return (Element) node;
    }

    public List<FluentXmlWrapper> getElements(@NonNull String name) {
        return getXmlElementWrapperList(element.getElementsByTagName(name));
    }

    public boolean hasElement(@NonNull String name) {
        return getElementCount(name) > 0;
    }

    public FluentXmlWrapper removeElement(@NonNull String name) {
        final NodeList nodeList = element.getElementsByTagName(name);
        if (nodeList.getLength() > 0) {
            element.removeChild(nodeList.item(0));
        }
        return this;
    }

    public int getElementCount(@NonNull String name) {
        return element.getElementsByTagName(name).getLength();
    }

    public List<FluentXmlWrapper> getAllElements() {
        NodeList nodeList = element.getChildNodes();
        return getXmlElementWrapperList(nodeList);
    }

    private static List<FluentXmlWrapper> getXmlElementWrapperList(NodeList nodeList) {
        List<FluentXmlWrapper> result = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node newElement = nodeList.item(i);
            if (newElement.getNodeType() == Node.ELEMENT_NODE) {
                result.add(of(newElement));
            }
        }
        return result;
    }

    public boolean hasAttribute(@NonNull String name) {
        return element.hasAttribute(name);
    }

    public String getAttribute(@NonNull String name) {
        return element.getAttribute(name);
    }

    public FluentXmlWrapper setAttribute(@NonNull String name, @NonNull String value) {
        element.setAttribute(name, value);
        return this;
    }

    public FluentXmlWrapper removeAttribute(@NonNull String name) {
        element.removeAttribute(name);
        return this;
    }

    public String getText() {
        return element.getTextContent();
    }

    public FluentXmlWrapper setText(String text) {
        element.setTextContent(text);
        return this;
    }

    public FluentXmlWrapper getParentElement() {
        return of(element.getParentNode());
    }

    public FluentXmlWrapper getRootElement() {
        return of(element.getOwnerDocument().getDocumentElement());
    }

    public Element getW3cElement() {
        return element;
    }

    public String toXmlWithDefaultUtf8Header() throws TransformerException {
        return toXmlWithCustomHeader("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    }

    public String toXmlWithoutHeader() throws TransformerException {
        return toXmlWithCustomHeader(null);
    }

    private String toXmlWithCustomHeader(String header) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        if (header != null) {
            writer.append(header);
            writer.append("\n");
        }
        transformer.transform(new DOMSource(element), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public static FluentXmlWrapper of(Element element) {
        return new FluentXmlWrapper(element);
    }

    public static FluentXmlWrapper of(final String xmlString) throws ParserConfigurationException, SAXException, IOException {
        return FluentXmlWrapper.of(getXmlDocumentFromString(xmlString).getDocumentElement());
    }

    public static FluentXmlWrapper ofNewRootElement(final String rootElementName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        final Document document = docBuilder.newDocument();
        final Element rootElement = document.createElement(rootElementName);
        document.appendChild(rootElement);
        return FluentXmlWrapper.of(rootElement);
    }

    private static FluentXmlWrapper of(Node node) {
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new IllegalArgumentException("not an element");
        }
        return of(getElement(node));
    }

    private static Document getXmlDocumentFromString(final String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(xmlString)));

        doc.getDocumentElement().normalize();
        return doc;
    }
}
