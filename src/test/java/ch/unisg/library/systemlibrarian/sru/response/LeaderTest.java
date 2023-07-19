package ch.unisg.library.systemlibrarian.sru.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeaderTest {

	private static final String TEST_LEADER = "abcdefghijklmnopqrstuvwx"; //length: 24
	private Leader leader;

	@BeforeEach
	public void before() {
		this.leader = new Leader(TEST_LEADER);
	}

	@Test
	public void testRecordLength() {
		assertEquals("abcde", leader.getRecordLength());
	}

	@Test
	public void testRecordStatus() {
		assertEquals("f", leader.getRecordStatus());
	}

	@Test
	public void testTypeOfRecord() {
		assertEquals("g", leader.getTypeOfRecord());
	}

	@Test
	public void testBibliographicLevel() {
		assertEquals("h", leader.getBibliographicLevel());
	}

	@Test
	public void testCharacterCodingScheme() {
		assertEquals("i", leader.getCharacterCodingScheme());
	}

	@Test
	public void testIndicatorCount() {
		assertEquals("j", leader.getIndicatorCount());
	}

	@Test
	public void testSubfieldCodeCount() {
		assertEquals("k", leader.getSubfieldCount());
	}

	@Test
	public void testBaseAddressOfData() {
		assertEquals("lmnop", leader.getBaseAddressOfData());
	}

	@Test
	public void testEncodingLevel() {
		assertEquals("q", leader.getEncodingLevel());
	}

	@Test
	public void testDescriptiveCatalogingForm() {
		assertEquals("r", leader.getDescriptiveCatalogingForm());
	}

	@Test
	public void testMultipartResourceRecordLevel() {
		assertEquals("s", leader.getMultipartResourceRecordLevel());
	}

	@Test
	public void testLengthOfTheLengthOfFieldPortion() {
		assertEquals("t", leader.getLengthOfTheLengthOfFieldPortion());
	}

	@Test
	public void testLengthOfTheStartingCharacterPositionPortion() {
		assertEquals("u", leader.getLengthOfTheStartingCharacterPositionPortion());
	}

	@Test
	public void testLengthOfTheImplementationDefinedPortion() {
		assertEquals("v", leader.getLengthOfTheImplementationDefinedPortion());
	}

	@Test
	public void testUndefined() {
		assertEquals("w", leader.getUndefined());
	}

	@Test
	public void testAssertLeaderToLong() {
		assertThrows(IllegalArgumentException.class, () -> new Leader("abcdefghijklmnopqrstuvwxyz"));
	}

	@Test
	public void testAssertLeaderLengthToShort() {
		assertThrows(IllegalArgumentException.class, () -> new Leader("acb"));
	}

	@Test
	public void testAssertLeaderEmpty() {
		assertThrows(IllegalArgumentException.class, () -> new Leader(""));
	}
}
