package ch.unisg.library.systemlibrarian.sru.query;

import ch.unisg.library.systemlibrarian.sru.query.index.Idx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SruQueryBuilderTest {

	@Test
	public void testQueryBuilder() {
		SruQuery query = SruQueryBuilder
				.create(Idx.inventoryNumber().containsPhrase("MN"))
				.and(Idx.inventoryDate().greaterThan("2020-10-12"))
				.and(Idx.inventoryDate().lessThan("2020-10-14"))
				.or(Idx.mmsId().equalTo("9953760105506"))
				.sort(Idx.mmsId(), Sort.ASCENDING)
				.build();

		final String queryString = query.string();
		Assertions.assertEquals("inventoryNumber+%3D+%22MN%22+AND+inventoryDate+%3E+%222020-10-12%22+AND+inventoryDate+%3C+%222020-10-14%22+OR+mms_id+%3D%3D+%229953760105506%22", queryString);
	}
}
