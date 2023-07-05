package ch.unisg.library.systemlibrarian.sru.client;

import ch.unisg.library.systemlibrarian.TestDataHelper;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.response.ControlField;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.configuration.Configuration;
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

class SruClientTest {

	private static ClientAndServer mockServer;

	@BeforeAll
	public static void beforeAll() {
		mockServer = ClientAndServer.startClientAndServer(
				Configuration.configuration()
						.disableSystemOut(true),
				9999
		);
	}

	@AfterAll
	public static void afterAll() {
		mockServer.stop();
	}

	@Test
	void getSingleRecordFromSingleResponse() throws IOException {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruQueryClient.MAXIMUM_RECORDS, "1")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-1.xml"))
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		Optional<MarcRecord> singleRecord = SruClientBuilder.create("http://localhost:9999/sru")
				.query(fakeQuery)
				.getSingleRecord();
		assertTrue(singleRecord.isPresent());
		assertEquals("Schutzorganisation der privaten Aktiengesellschaften", singleRecord.get().findSubFields("264", "b").get(0).getText());
	}

	@Test
	void getRecords() throws IOException {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruQueryClient.START_RECORD, "1"))
								.withQueryStringParameter(Parameter.param(SruQueryClient.MAXIMUM_RECORDS, "12")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-12.xml"))
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		Stream<MarcRecord> records = SruClientBuilder.create("http://localhost:9999/sru")
				.query(fakeQuery)
				.maximumRecords(12)
				.getRecords();
		List<MarcRecord> recordList = records.toList();
		assertEquals(12, recordList.size());
	}

	@Test
	void getAllRecords() throws IOException {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruQueryClient.START_RECORD, "1"))
								.withQueryStringParameter(Parameter.param(SruQueryClient.MAXIMUM_RECORDS, "50")),
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
								.withQueryStringParameter(Parameter.param(SruQueryClient.START_RECORD, "51"))
								.withQueryStringParameter(Parameter.param(SruQueryClient.MAXIMUM_RECORDS, "50")),
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
								.withQueryStringParameter(Parameter.param(SruQueryClient.START_RECORD, "101"))
								.withQueryStringParameter(Parameter.param(SruQueryClient.MAXIMUM_RECORDS, "50")),
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
								.withQueryStringParameter(Parameter.param(SruQueryClient.START_RECORD, "151"))
								.withQueryStringParameter(Parameter.param(SruQueryClient.MAXIMUM_RECORDS, "50")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-151-to-end-of-122.xml"))
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		Stream<MarcRecord> records = SruClientBuilder.create("http://localhost:9999/sru")
				.query(fakeQuery)
				.getAllRecords();
		List<MarcRecord> recordList = records.toList();
		assertEquals(122, recordList.size());
	}

	@Test
	void stopOnHttpError() {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruQueryClient.START_RECORD, String.valueOf(SruQueryClient.DEFAULT_START_RECORD)))
								.withQueryStringParameter(Parameter.param(SruQueryClient.MAXIMUM_RECORDS, String.valueOf(SruQueryClient.DEFAULT_MAXIMUM_RECORDS))),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(404)
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		Stream<MarcRecord> records = SruClientBuilder.create("http://localhost:9999/sru")
				.query(fakeQuery)
				.getAllRecords();
		List<MarcRecord> recordList = records.toList();
		assertEquals(0, recordList.size());
	}

	@Test
	void getRecordsFromResponseTest() throws IOException {
		mockServer.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/sru")
								.withQueryStringParameter(Parameter.param(SruQueryClient.START_RECORD, "1"))
								.withQueryStringParameter(Parameter.param(SruQueryClient.MAXIMUM_RECORDS, "12")),
						Times.exactly(1))
				.respond(
						HttpResponse.response()
								.withStatusCode(200)
								.withBody(new TestDataHelper().getResponseFromFile("records-12.xml"))
				);
		SruQuery fakeQuery = new SruQuery("nothing");
		Stream<MarcRecord> records = SruClientBuilder.create("http://localhost:9999/sru")
				.query(fakeQuery)
				.maximumRecords(12)
				.getRecords();
		List<MarcRecord> recordList = records.toList();
		assertEquals(12, recordList.size());
		MarcRecord record5 = recordList.get(4);
		Optional<ControlField> controlfield001 = record5.getControlField("001");
		assertTrue(controlfield001.isPresent());
		final String text001 = controlfield001.get().getText();
		assertEquals("991008097069705501", text001);
	}
}
