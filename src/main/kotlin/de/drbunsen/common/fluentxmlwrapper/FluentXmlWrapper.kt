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

    fun getElement(name: String): FluentXmlWrapper {
        return getElementOrNull(name)!!
    }

    fun getElementOrNull(name: String): FluentXmlWrapper? {
        val nodeList = w3cElement.getElementsByTagName(name)
        if (nodeList.length > 0) {
            val node = nodeList.item(0)
            return of(node)
        }
        return null
    }

    fun getElements(name: String): List<FluentXmlWrapper> {
        return getXmlElementWrapperList(w3cElement.getElementsByTagName(name))
    }

    fun hasElement(name: String): Boolean {
        return getElementCount(name) > 0
    }

    fun removeElement(name: String): FluentXmlWrapper {
        val nodeList = w3cElement.getElementsByTagName(name)
        if (nodeList.length > 0) {
            w3cElement.removeChild(nodeList.item(0))
        }
        return this
    }

    fun getElementCount(name: String): Int {
        return w3cElement.getElementsByTagName(name).length
    }

    val allElements: List<FluentXmlWrapper>
        get() {
            val nodeList = w3cElement.childNodes
            return getXmlElementWrapperList(nodeList)
        }

    fun hasAttribute(name: String): Boolean {
        return w3cElement.hasAttribute(name)
    }

    fun getAttribute(name: String): String {
        return w3cElement.getAttribute(name)
    }

    fun setAttribute(name: String, value: String): FluentXmlWrapper {
        w3cElement.setAttribute(name, value)
        return this
    }

    fun removeAttribute(name: String): FluentXmlWrapper {
        w3cElement.removeAttribute(name)
        return this
    }

    val name: String
        get() = w3cElement.tagName

    var text: String?
        get() = w3cElement.textContent
        set(value) {w3cElement.textContent = value}

    val parentElement: FluentXmlWrapper
        get() = of(w3cElement.parentNode)
    val rootElement: FluentXmlWrapper
        get() = of(w3cElement.ownerDocument.documentElement)

    fun toXmlWithDefaultUtf8Header(): String {
        return toXmlWithCustomHeader("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
    }

    fun toXmlWithoutHeader(): String {
        return toXmlWithCustomHeader(null)
    }

    private fun toXmlWithCustomHeader(header: String?): String {
        val tf = TransformerFactory.newInstance()
        val transformer = tf.newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
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