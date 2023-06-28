package ch.unisg.library.systemlibrarian.sru;

import ch.unisg.library.systemlibrarian.TestDataHelper;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.response.Controlfield;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import ch.unisg.library.systemlibrarian.sru.url.SruUrl;
import ch.unisg.library.systemlibrarian.sru.url.SruUrlBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SruClientTest {

	private ClientAndServer mockServer;

	@BeforeAll
	public void beforeAll() {
		mockServer = ClientAndServer.startClientAndServer(9999);
	}

	@AfterAll
	public void afterAll() {
		mockServer.stop();
	}

	@Test
	void getSingleRecordFromSingleResponse() throws IOException {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruUrl.MAXIMUM_RECORDS, "1")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-1.xml"))
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		SruUrl mockUrl = SruUrlBuilder.create("http://localhost:9999/sru").query(fakeQuery).build();
		Optional<MarcRecord> singleRecord = new SruClient().getSingleRecord(mockUrl);
		assertTrue(singleRecord.isPresent());
		assertEquals("Schutzorganisation der privaten Aktiengesellschaften", singleRecord.get().findSubfield("264", "b").get(0).getText());
	}

	@Test
	void getRecords() throws IOException {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruUrl.START_RECORD, "1"))
								.withQueryStringParameter(Parameter.param(SruUrl.MAXIMUM_RECORDS, "50")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-12.xml"))
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		SruUrl mockUrl = SruUrlBuilder.create("http://localhost:9999/sru").query(fakeQuery).build();
		Stream<MarcRecord> records = new SruClient().getRecords(mockUrl);
		List<MarcRecord> recordList = records.toList();
		assertEquals(12, recordList.size());
	}

	@Test
	void getAllRecords() throws IOException {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruUrl.START_RECORD, "1"))
								.withQueryStringParameter(Parameter.param(SruUrl.MAXIMUM_RECORDS, "50")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-1-to-50-of-122.xml"))
				);
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruUrl.START_RECORD, "51"))
								.withQueryStringParameter(Parameter.param(SruUrl.MAXIMUM_RECORDS, "50")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-51-to-100-of-122.xml"))
				);
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruUrl.START_RECORD, "101"))
								.withQueryStringParameter(Parameter.param(SruUrl.MAXIMUM_RECORDS, "50")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-101-to-122-of-122.xml"))
				);
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruUrl.START_RECORD, "151"))
								.withQueryStringParameter(Parameter.param(SruUrl.MAXIMUM_RECORDS, "50")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-151-to-end-of-122.xml"))
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		SruUrl mockUrl = SruUrlBuilder.create("http://localhost:9999/sru").query(fakeQuery).build();
		Stream<MarcRecord> records = new SruClient().getAllRecords(mockUrl);
		List<MarcRecord> recordList = records.toList();
		assertEquals(122, recordList.size());
	}

	@Test
	void stopOnHttpError() throws IOException {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruUrl.START_RECORD, "1"))
								.withQueryStringParameter(Parameter.param(SruUrl.MAXIMUM_RECORDS, "50")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(404)
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		SruUrl mockUrl = SruUrlBuilder.create("http://localhost:9999/sru").query(fakeQuery).build();
		Stream<MarcRecord> records = new SruClient().getAllRecords(mockUrl);
		List<MarcRecord> recordList = records.toList();
		assertEquals(0, recordList.size());
	}

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
