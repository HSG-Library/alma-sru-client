package ch.unisg.library.systemlibrarian.sru;

import ch.unisg.library.systemlibrarian.TestDataHelper;
import ch.unisg.library.systemlibrarian.sru.response.Controlfield;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SruClientTest {

	@Test
	void getRecordsFromResponseCountTest() throws IOException {
		TestDataHelper testDataHelper = new TestDataHelper();
		final String mockResponse = testDataHelper.getResponseFromFile("records-12.xml");
		SruClient sruClient = new SruClient();
		Stream<MarcRecord> recordStream = sruClient.extractRecords(testDataHelper.getDocumentFromXmlString(mockResponse));
		assertEquals(12, recordStream.count());
	}

	@Test
	void getRecordsFromResponseTest() throws IOException {
		TestDataHelper testDataHelper = new TestDataHelper();
		final String mockResponse = testDataHelper.getResponseFromFile("records-12.xml");
		SruClient sruClient = new SruClient();
		Stream<MarcRecord> recordStream = sruClient.extractRecords(testDataHelper.getDocumentFromXmlString(mockResponse));
		Optional<MarcRecord> record5 = recordStream.skip(4).findFirst();
		assertTrue(record5.isPresent());
		Optional<Controlfield> controlfield001 = record5.get().getControlfield("001");
		assertTrue(controlfield001.isPresent());
		final String text001 = controlfield001.get().getText();
		assertEquals("991008097069705501", text001);
	}
}
