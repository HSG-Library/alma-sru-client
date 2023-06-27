package ch.unisg.library.systemlibrarian.sru;

import ch.unisg.library.systemlibrarian.helper.DomUtil;
import ch.unisg.library.systemlibrarian.sru.response.Controlfield;
import ch.unisg.library.systemlibrarian.sru.response.Record;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SruClientTest {

	@Test
	void getRecordsFromResponseCountTest() throws IOException {
		final String mockResponse = getResponseFromFile("records-12.xml");
		SruClient sruClient = new SruClient();
		Stream<Record> recordStream = sruClient.extractRecords(DomUtil.getDocumentFromXmlString(mockResponse));
		assertEquals(12, recordStream.count());
	}

	@Test
	void getRecordsFromResponseTest() throws IOException {
		final String mockResponse = getResponseFromFile("records-12.xml");
		SruClient sruClient = new SruClient();
		Stream<Record> recordStream = sruClient.extractRecords(DomUtil.getDocumentFromXmlString(mockResponse));
		Optional<Record> record5 = recordStream.skip(4).findFirst();
		assertTrue(record5.isPresent());
		Optional<Controlfield> controlfield001 = record5.get().getControlfield("001");
		assertTrue(controlfield001.isPresent());
		final String text001 = controlfield001.get().getText();
		assertEquals("991008097069705501", text001);
	}

	private String getResponseFromFile(final String fileName) throws IOException {
		URL inputFile = getClass().getClassLoader().getResource("sru-responses/" + fileName);
		assert inputFile != null;
		final Path responseFile = new File(inputFile.getFile()).toPath();
		return Files.readString(responseFile, StandardCharsets.UTF_8);
	}
}