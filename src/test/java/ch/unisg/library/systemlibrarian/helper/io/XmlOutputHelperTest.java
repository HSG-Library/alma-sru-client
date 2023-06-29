package ch.unisg.library.systemlibrarian.helper.io;

import ch.unisg.library.systemlibrarian.TestDataHelper;
import ch.unisg.library.systemlibrarian.helper.XPathHelper;
import ch.unisg.library.systemlibrarian.helper.XmlHelper;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlOutputHelperTest {

	@Test
	public void testOutputSingleFile() throws IOException {
		final TestDataHelper testDataHelper = new TestDataHelper();
		final String responseXml = testDataHelper.getResponseFromFile("records-12.xml");
		final Stream<MarcRecord> recordStream = testDataHelper.getRecordStreamFromXml(responseXml);

		final XmlOutputHelper xmlOutputHelper = new XmlOutputHelper();
		final File outputFile = Files.createTempFile("sru-test", ".xml").toFile();
		outputFile.deleteOnExit();
		xmlOutputHelper.saveSingleFile(recordStream, outputFile);

		final String readXml = Files.readString(outputFile.toPath(), StandardCharsets.UTF_8);
		final Document readDocument = new XmlHelper().xmlStringToDocument(readXml);
		XPathHelper xPathHelper = new XPathHelper();
		NodeList resultRecords = xPathHelper.query(readDocument, "//record");
		assertEquals(12, resultRecords.getLength());
	}

	@Test
	public void testOutputIndividualFiles() throws IOException {
		final TestDataHelper testDataHelper = new TestDataHelper();
		final String responseXml = testDataHelper.getResponseFromFile("records-12.xml");
		final Stream<MarcRecord> recordStream = testDataHelper.getRecordStreamFromXml(responseXml);

		final XmlOutputHelper xmlOutputHelper = new XmlOutputHelper();
		final Path outputDir = Files.createTempDirectory("sru-test-dir");

		xmlOutputHelper.saveEachRecord(recordStream, outputDir);

		try (Stream<Path> dirListing = Files.list(outputDir)) {
			long fileCount = dirListing
					.filter(Files::isRegularFile)
					.count();
			assertEquals(12, fileCount);
		}
	}
}
