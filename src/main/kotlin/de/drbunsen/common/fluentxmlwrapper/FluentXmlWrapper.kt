package de.drbunsen.common.fluentxmlwrapper

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.File
import java.io.FileInputStream
import java.io.StringReader
import java.io.StringWriter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class FluentXmlWrapper private constructor(private val w3cElement: Element) {

    fun addElement(name: String): FluentXmlWrapper {
        val newChild = w3cElement.ownerDocument.createElement(name)
        w3cElement.appendChild(newChild)
        return of(newChild)
    }

    fun addElementBefore(name: String, referenceElement: FluentXmlWrapper): FluentXmlWrapper {
        val newChild = w3cElement.ownerDocument.createElement(name)
        w3cElement.insertBefore(newChild, referenceElement.w3cElement)
        return of(newChild)
    }

    fun getElement(name: String): FluentXmlWrapper = getElementOrNull(name)!!

    fun getElementOrNull(name: String): FluentXmlWrapper? {
        val nodeList = w3cElement.getElementsByTagName(name)
        if (nodeList.length > 0) {
            val node = nodeList.item(0)
            return of(node)
        }
        return null
    }

    fun getElements(name: String): List<FluentXmlWrapper> =
        getXmlElementWrapperList(w3cElement.getElementsByTagName(name))

    fun hasElement(name: String): Boolean = getElementCount(name) > 0

    fun removeElement(name: String): FluentXmlWrapper {
        val nodeList = w3cElement.getElementsByTagName(name)
        if (nodeList.length > 0) {
            w3cElement.removeChild(nodeList.item(0))
        }
        return this
    }

    fun getElementCount(name: String): Int = w3cElement.getElementsByTagName(name).length

    fun getAllElements(): List<FluentXmlWrapper> {
        val nodeList = w3cElement.childNodes
        return getXmlElementWrapperList(nodeList)
    }

    fun hasAttribute(name: String): Boolean = w3cElement.hasAttribute(name)

    fun getAttribute(name: String): String = w3cElement.getAttribute(name)

    fun setAttribute(name: String, value: String): FluentXmlWrapper {
        w3cElement.setAttribute(name, value)
        return this
    }

    fun removeAttribute(name: String): FluentXmlWrapper {
        w3cElement.removeAttribute(name)
        return this
    }

    fun getName(): String = w3cElement.tagName

    fun getText(): String = w3cElement.textContent

    fun setText(value: String): FluentXmlWrapper {
        w3cElement.textContent = value
        return this
    }

    fun getParentElement(): FluentXmlWrapper = of(w3cElement.parentNode)

    fun getRootElement(): FluentXmlWrapper = of(w3cElement.ownerDocument.documentElement)

    fun toXmlWithDefaultUtf8Header(intend: Boolean): String = toXmlWithCustomHeader("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>", intend)

    fun toXmlWithoutHeader(intend: Boolean): String = toXmlWithCustomHeader(null, intend)

    private fun toXmlWithCustomHeader(header: String?, intend: Boolean): String {
        val tf = TransformerFactory.newInstance()
        val transformer = tf.newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        if (intend) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        }
        val writer = StringWriter()
        if (header != null) {
            writer.append(header)
            writer.append("\n")
        }
        transformer.transform(DOMSource(w3cElement), StreamResult(writer))
        return writer.buffer.toString()
    }

    companion object {
        private fun getXmlElementWrapperList(nodeList: NodeList): List<FluentXmlWrapper> {
            val result: MutableList<FluentXmlWrapper> = ArrayList()
            for (i in 0 until nodeList.length) {
                val newElement = nodeList.item(i)
                if (newElement.nodeType == Node.ELEMENT_NODE) {
                    result.add(of(newElement))
                }
            }
            return result
        }

        fun of(element: Element): FluentXmlWrapper = FluentXmlWrapper(element)

        fun of(xmlString: String): FluentXmlWrapper = of(getXmlDocument(xmlString).documentElement)

        fun of(file: File): FluentXmlWrapper = of(getXmlDocument(file).documentElement)

        fun of(inputSource: InputSource): FluentXmlWrapper = of(getXmlDocument(inputSource).documentElement)

        fun ofNewRootElement(rootElementName: String): FluentXmlWrapper {
            val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
            val rootElement = document.createElement(rootElementName)
            document.appendChild(rootElement)
            return of(rootElement)
        }

        private fun of(node: Node): FluentXmlWrapper {
            require(node.nodeType == Node.ELEMENT_NODE) { "not an element" }
            return of(node as Element)
        }

        private fun getXmlDocument(xmlString: String): Document = getXmlDocument(InputSource(StringReader(xmlString)))

        private fun getXmlDocument(file: File): Document = getXmlDocument(InputSource(FileInputStream(file)))

        private fun getXmlDocument(inputSource: InputSource): Document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource)
                .also { it.documentElement.normalize() }
    }
}