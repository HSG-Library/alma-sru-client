package ch.unisg.library.systemlibrarian.helper;

import ch.unisg.library.systemlibrarian.TestDataHelper;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class XPathHelperTest {

	@Test
	public void testQuery() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		NodeList fields035 = xPathHelper.query(getTestData(), "//record/datafield[@tag='035']");
		assertEquals(3, fields035.getLength());
	}

	@Test
	public void testQueryNotExisting() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		NodeList notExisting = xPathHelper.query(getTestData(), "//record/notExisting");
		assertEquals(0, notExisting.getLength());
	}

	@Test
	public void testQuerySingle() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		Optional<Node> leader = xPathHelper.querySingle(getTestData(), "//record/leader");
		assertTrue(leader.isPresent());
		assertEquals("00910nam a2200217uc 4500", leader.get().getTextContent());
	}

	@Test
	public void testQuerySingle2() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		Optional<Node> leader = xPathHelper.querySingle(getTestData(), "//record/datafield[@tag='035']/subfield[@code='a']");
		assertTrue(leader.isPresent());
		assertEquals("(swissbib)005197015-41slsp_network", leader.get().getTextContent());
	}

	@Test
	public void testQuerySingleNotExisting() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		Optional<Node> notExisting = xPathHelper.querySingle(getTestData(), "//record/notExisting");
		assertTrue(notExisting.isEmpty());
	}

	@Test
	public void testQueryTextSingleResult() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		Optional<String> leader = xPathHelper.queryText(getTestData(), "//record/leader");
		assertTrue(leader.isPresent());
		assertEquals("00910nam a2200217uc 4500", leader.get());
	}

	@Test
	public void testQueryTextMultiple() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		Optional<String> leader = xPathHelper.queryText(getTestData(), "//record/datafield[@tag='035']/subfield[@code='a']");
		assertTrue(leader.isPresent());
		assertEquals("(swissbib)005197015-41slsp_network", leader.get());
	}

	@Test
	public void testQueryTextNoResult() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		Optional<String> leader = xPathHelper.queryText(getTestData(), "//record/notExisting");
		assertTrue(leader.isEmpty());
	}

	@Test
	public void testQueryExists() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		boolean leader = xPathHelper.queryExists(getTestData(), "//record/leader");
		assertTrue(leader);
	}

	@Test
	public void testQueryExistsNotExisting() throws IOException {
		XPathHelper xPathHelper = new XPathHelper();
		boolean doesNotExist = xPathHelper.queryExists(getTestData(), "//record/notExisting");
		assertFalse(doesNotExist);
	}

	private Document getTestData() throws IOException {
		TestDataHelper testDataHelper = new TestDataHelper();
		String xmlString = testDataHelper.getResponseFromFile("records-1.xml");
		return testDataHelper.getDocumentFromXmlString(xmlString);
	}
}
