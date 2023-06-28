package ch.unisg.library.systemlibrarian;

import ch.unisg.library.systemlibrarian.sru.SruClient;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestDataHelper {

	public String getResponseFromFile(final String fileName) throws IOException {
		URL inputFile = getClass().getClassLoader().getResource("sru-responses/" + fileName);
		assert inputFile != null;
		final Path responseFile = new File(inputFile.getFile()).toPath();
		return Files.readString(responseFile, StandardCharsets.UTF_8);
	}

	public Stream<MarcRecord> getRecordStreamFromXml(final String xml) {
		final SruClient sruClient = new SruClient();
		return sruClient.extractRecords(getDocumentFromXmlString(xml));
	}

	public Document getDocumentFromXmlString(String xml) {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String print(Node node) {
		try {
			StringWriter writer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(node), new StreamResult(writer));
			final String xml = writer.toString();
			System.out.println(xml);
			return xml;
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
}
