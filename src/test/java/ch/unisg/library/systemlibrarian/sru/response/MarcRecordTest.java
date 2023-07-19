package ch.unisg.library.systemlibrarian.sru.response;

import ch.unisg.library.systemlibrarian.TestDataHelper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarcRecordTest {

	@Test
	public void testFindDataField() throws IOException {
		MarcRecord record = getTestData();

		List<DataField> dataFieldsNoIndicators = record.findDataFields("999");
		assertEquals(4, dataFieldsNoIndicators.size());

		List<DataField> dataFieldsInd1 = record.findDataFields("999", "1", null);
		assertEquals(1, dataFieldsInd1.size());
		assertEquals("test-999-1-a", dataFieldsInd1.get(0).findSubfield("a").orElseThrow().getText());

		List<DataField> dataFieldsInd2 = record.findDataFields("999", null, "2");
		assertEquals(1, dataFieldsInd2.size());
		assertEquals("test-999-2-a", dataFieldsInd2.get(0).findSubfield("a").orElseThrow().getText());

		List<DataField> dataFieldsInd12 = record.findDataFields("999", "1", "2");
		assertEquals(1, dataFieldsInd12.size());
		assertEquals("test-999-1-2-a", dataFieldsInd12.get(0).findSubfield("a").orElseThrow().getText());

		List<DataField> dataFieldsInd1Space = record.findDataFields("999", "1", " ");
		assertEquals(1, dataFieldsInd1Space.size());
		assertEquals("test-999-1-a", dataFieldsInd1Space.get(0).findSubfield("a").orElseThrow().getText());

		List<DataField> dataFieldsInd1Empty = record.findDataFields("999", "1", "");
		assertEquals(1, dataFieldsInd1Empty.size());
		assertEquals("test-999-1-a", dataFieldsInd1Empty.get(0).findSubfield("a").orElseThrow().getText());

		List<DataField> dataFieldsInd1Wrong = record.findDataFields("999", "wrong", null);
		assertEquals(0, dataFieldsInd1Wrong.size());

		List<DataField> dataFieldsExplicitNoInd = record.findDataFields("999", null, null);
		assertEquals(1, dataFieldsExplicitNoInd.size());
		assertEquals("test-999-a", dataFieldsExplicitNoInd.get(0).findSubfield("a").orElseThrow().getText());
	}

	@Test
	public void testFindSubField() throws IOException {
		MarcRecord record = getTestData();

		List<SubField> subFields = record.findSubFields("999", "a");
		assertEquals(4, subFields.size());

		List<SubField> subField9991a = record.findSubFields("999", "1", null, "a");
		assertEquals(1, subField9991a.size());
		assertEquals("test-999-1-a", subField9991a.get(0).getText());

		List<SubField> subField9992a = record.findSubFields("999", null, "2", "a");
		assertEquals(1, subField9992a.size());
		assertEquals("test-999-2-a", subField9992a.get(0).getText());

		List<SubField> subField99912a = record.findSubFields("999", "1", "2", "a");
		assertEquals(1, subField99912a.size());
		assertEquals("test-999-1-2-a", subField99912a.get(0).getText());

		List<SubField> subField9991SpaceA = record.findSubFields("999", "1", " ", "a");
		assertEquals(1, subField9991SpaceA.size());
		assertEquals("test-999-1-a", subField9991SpaceA.get(0).getText());

		List<SubField> subField9991EmptyA = record.findSubFields("999", "1", "", "a");
		assertEquals(1, subField9991EmptyA.size());
		assertEquals("test-999-1-a", subField9991EmptyA.get(0).getText());

		List<SubField> subField999WrongA = record.findSubFields("999", "wrong", null, "a");
		assertEquals(0, subField999WrongA.size());

		List<SubField> subField999ExplicitNoIndA = record.findSubFields("999", "", null, "a");
		assertEquals(1, subField999ExplicitNoIndA.size());
		assertEquals("test-999-a", subField999ExplicitNoIndA.get(0).getText());
	}

	@Test
	public void testGetLeader() throws IOException {
		MarcRecord record = getTestData();
		Optional<Leader> leader = record.getLeader();
		assertTrue(leader.isPresent());
		assertEquals("2", leader.get().getSubfieldCount());
		assertEquals("n", leader.get().getRecordStatus());
		assertEquals("a", leader.get().getTypeOfRecord());
	}

	private MarcRecord getTestData() throws IOException {
		TestDataHelper testDataHelper = new TestDataHelper();
		String xmlString = testDataHelper.getResponseFromFile("records-1-modified-for-query-test.xml");
		Optional<MarcRecord> firstRecord = testDataHelper.extractRecords(testDataHelper.getDocumentFromXmlString(xmlString)).findFirst();
		assertTrue(firstRecord.isPresent());
		return firstRecord.get();
	}
}
