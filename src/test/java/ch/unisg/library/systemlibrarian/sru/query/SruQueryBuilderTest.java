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
				.or(Idx.title().containsWords("rain"))
				.sort(Idx.title(), Sort.ASCENDING)
				.build();
		final String queryString = query.string();
		final String expected = "alma.inventoryNumber%20%3D%20%22MN%22%20AND%20alma.inventoryDate%20%3E%20%222020-10-12%22%20AND%20alma.inventoryDate%20%3C%20%222020-10-14%22%20OR%20alma.mms_id%20%3D%3D%20%229953760105506%22%20OR%20alma.title%20all%20%22rain%22%20sortBy%20title%2Fsort.ascending";
		assertEquals(expected, queryString);
	}

	@Test
	public void testQueryBuildLoop() {
		Stream<String> systemNumbers = Stream.of("1", "2", "3", "4", "5");
		SruQueryBuilder sruQueryBuilder = SruQueryBuilder.create();
		systemNumbers.forEach(number -> sruQueryBuilder.or(Idx.otherSystemNumber().equalTo(number)));
		final SruQuery query = sruQueryBuilder.build();
		final String queryString = query.string();
		final String expected = "alma.other_system_number%20%3D%3D%20%221%22%20OR%20alma.other_system_number%20%3D%3D%20%222%22%20OR%20alma.other_system_number%20%3D%3D%20%223%22%20OR%20alma.other_system_number%20%3D%3D%20%224%22%20OR%20alma.other_system_number%20%3D%3D%20%225%22";
		assertEquals(expected, queryString);
	}
}
