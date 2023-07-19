package ch.unisg.library.systemlibrarian.sru.response;

import org.apache.commons.lang3.Validate;

/**
 * See: <a href="https://www.loc.gov/marc/bibliographic/bdleader.html">Marc 21: Leader</a>
 */
public class Leader {

	public static final int LEADER_LENGTH = 24;
	private final String leader;

	public Leader(final String leader) {
		Validate.inclusiveBetween(LEADER_LENGTH, LEADER_LENGTH, leader.length(), "Leader must have exactly 24 characters");
		this.leader = leader;
	}

	/**
	 * 00-04 - Record length
	 *
	 * @return Record length
	 */
	public String getRecordLength() {
		return leader.substring(0, 5);
	}

	/**
	 * 05 - Record status
	 * <p>
	 * a - Increase in encoding level
	 * c - Corrected or revised
	 * d - Deleted
	 * n - New
	 * p - Increase in encoding level from prepublication
	 *
	 * @return Record status
	 */
	public String getRecordStatus() {
		return leader.substring(5, 6);
	}

	/**
	 * 06 - Type of record
	 * <p>
	 * a - Language material
	 * c - Notated music
	 * d - Manuscript notated music
	 * e - Cartographic material
	 * f - Manuscript cartographic material
	 * g - Projected medium
	 * i - Nonmusical sound recording
	 * j - Musical sound recording
	 * k - Two-dimensional nonprojectable graphic
	 * m - Computer file
	 * o - Kit
	 * p - Mixed materials
	 * r - Three-dimensional artifact or naturally occurring object
	 * t - Manuscript language material
	 *
	 * @return Type of record
	 */
	public String getTypeOfRecord() {
		return leader.substring(6, 7);
	}

	/**
	 * 07 - Bibliographic level
	 * <p>
	 * a - Monographic component part
	 * b - Serial component part
	 * c - Collection
	 * d - Subunit
	 * i - Integrating resource
	 * m - Monograph/Item
	 * s - Serial
	 *
	 * @return bibliographic level
	 */
	public String getBibliographicLevel() {
		return leader.substring(7, 8);
	}

	/**
	 * 09 - Character coding scheme
	 * <p>
	 * # - MARC-8
	 * a - UCS/Unicode
	 *
	 * @return Character coding scheme
	 */
	public String getCharacterCodingScheme() {
		return leader.substring(8, 9);
	}

	/**
	 * 10 - Indicator count
	 * <p>
	 * 2 - Number of character positions used for indicators
	 *
	 * @return Indicator count
	 */
	public String getIndicatorCount() {
		return leader.substring(9, 10);
	}

	/**
	 * 11 - Subfield code count
	 * <p>
	 * 2 - Number of character positions used for a subfield code
	 *
	 * @return Subfield code count
	 */
	public String getSubfieldCount() {
		return leader.substring(10, 11);
	}

	/**
	 * 12-16 - Base address of data
	 * <p>
	 * [number] - Length of Leader and Directory
	 *
	 * @return Base address of data
	 */
	public String getBaseAddressOfData() {
		return leader.substring(11, 16);
	}

	/**
	 * 17 - Encoding level
	 * <p>
	 * # - Full level
	 * 1 - Full level, material not examined
	 * 2 - Less-than-full level, material not examined
	 * 3 - Abbreviated level
	 * 4 - Core level
	 * 5 - Partial (preliminary) level
	 * 7 - Minimal level
	 * 8 - Prepublication level
	 * u - Unknown
	 * z - Not applicable
	 *
	 * @return Encoding level
	 */
	public String getEncodingLevel() {
		return leader.substring(16, 17);
	}

	/**
	 * 18 - Descriptive cataloging form
	 * <p>
	 * # - Non-ISBD
	 * a - AACR 2
	 * c - ISBD punctuation omitted
	 * i - ISBD punctuation included
	 * n - Non-ISBD punctuation omitted
	 * u - Unknown
	 *
	 * @return Descriptive cataloging form
	 */
	public String getDescriptiveCatalogingForm() {
		return leader.substring(17, 18);
	}

	/**
	 * 19 - Multipart resource record level
	 * <p>
	 * # - Not specified or not applicable
	 * a - Set
	 * b - Part with independent title
	 * c - Part with dependent title
	 *
	 * @return Multipart resource record level
	 */
	public String getMultipartResourceRecordLevel() {
		return leader.substring(18, 19);
	}


	/**
	 * 20 - Length of the length-of-field portion
	 * <p>
	 * 4 - Number of characters in the length-of-field portion of a Directory entry
	 *
	 * @return Length of the length-of-field portion
	 */
	public String getLengthOfTheLengthOfFieldPortion() {
		return leader.substring(19, 20);
	}

	/**
	 * 21 - Length of the starting-character-position portion
	 * <p>
	 * 5 - Number of characters in the starting-character-position portion of a Directory entry
	 *
	 * @return Length of the starting-character-position portion
	 */
	public String getLengthOfTheStartingCharacterPositionPortion() {
		return leader.substring(20, 21);
	}

	/**
	 * 22 - Length of the implementation-defined portion
	 * <p>
	 * 0 - Number of characters in the implementation-defined portion of a Directory entry
	 *
	 * @return Length of the implementation-defined portion
	 */
	public String getLengthOfTheImplementationDefinedPortion() {
		return leader.substring(21, 22);
	}

	/**
	 * 23 - Undefined
	 * <p>
	 * 0 - Undefined
	 *
	 * @return Undefined
	 */
	public String getUndefined() {
		return leader.substring(22, 23);
	}
}
