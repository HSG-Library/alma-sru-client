package ch.unisg.library.systemlibrarian.sru.query;

import ch.unisg.library.systemlibrarian.sru.query.index.Idx;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		assertEquals("inventoryNumber+%3D+%22MN%22+AND+inventoryDate+%3E+%222020-10-12%22+AND+inventoryDate+%3C+%222020-10-14%22+OR+mms_id+%3D%3D+%229953760105506%22", queryString);
	}

	@Test
	public void testQueryBuildLoop() {
		Stream<String> systemNumbers = Stream.of("1", "2", "3", "4", "5");

		SruQueryBuilder sruQueryBuilder = SruQueryBuilder.create();
		systemNumbers.forEach(number -> sruQueryBuilder.or(Idx.otherSystemNumber().equalTo(number)));
		final SruQuery query = sruQueryBuilder.build();
		final String queryString = query.string();

		assertEquals("other_system_number+%3D%3D+%221%22+OR+other_system_number+%3D%3D+%222%22+OR+other_system_number+%3D%3D+%223%22+OR+other_system_number+%3D%3D+%224%22+OR+other_system_number+%3D%3D+%225%22", queryString);
	}
}
