package ch.unisg.library.systemlibrarian.file;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcelOutputDataHelperTest {

	@Test
	public void testWriteExcelRows() throws IOException {
		final var rows = List.of(
				List.of("1", "2", "3", "4"),
				List.of("1", "2", "3", "4"),
				List.of("1", "2", "3", "4"),
				List.of("1", "2", "3", "4"),
				List.of("1", "2", "3", "4")
		);

		ExcelOutputDataHelper excelOutputDataHelper = new ExcelOutputDataHelper(File.createTempFile("sru-test-", ".xlsx"));
		final File xlsxFile = excelOutputDataHelper.writeToExcel(rows);

		ExcelInputDataHelper excelInputDataHelper = new ExcelInputDataHelper(xlsxFile);
		List<String> columnA = excelInputDataHelper.loadNumbersColumn("A");
		assertEquals(5, columnA.size());
		assertTrue(columnA.stream().allMatch(value -> value.equals("1")));

		List<String> columnD = excelInputDataHelper.loadNumbersColumn("D");
		assertEquals(5, columnD.size());
		assertTrue(columnD.stream().allMatch(value -> value.equals("4")));
	}

	@Test
	public void testWriteExcelRowsWithLead() throws IOException {
		final var rowsWithLead = Map.of(
				"1st", List.of("1", "2", "3", "4"),
				"2nd", List.of("1", "2", "3", "4"),
				"3rd", List.of("1", "2", "3", "4"),
				"4th", List.of("1", "2", "3", "4"),
				"5th", List.of("1", "2", "3", "4")
		);

		ExcelOutputDataHelper excelOutputDataHelper = new ExcelOutputDataHelper(File.createTempFile("sru-test-", ".xlsx"));
		final File xlsxFile = excelOutputDataHelper.writeToExcel(rowsWithLead);

		ExcelInputDataHelper excelInputDataHelper = new ExcelInputDataHelper(xlsxFile);
		List<String> columnA = excelInputDataHelper.loadColumn("A");
		assertEquals(5, columnA.size());
		assertTrue(columnA.containsAll(List.of("1st", "2nd", "3rd", "4th", "5th")));

		List<String> columnD = excelInputDataHelper.loadNumbersColumn("D");
		assertEquals(5, columnD.size());
		assertTrue(columnD.stream().allMatch(value -> value.equals("3")));
	}
}