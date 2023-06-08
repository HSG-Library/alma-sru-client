package ch.unisg.library.systemlibrarian.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

class ExcelInputDataHelperTest {

	@Test
	public void testLoadXlsxSimple() {
		URL inputFile = getClass().getClassLoader().getResource("xlsx/input-00.xlsx");
		assert inputFile != null;
		final File xlsxFile = new File(inputFile.getFile());
		ExcelInputDataHelper excelInputDataHelper = new ExcelInputDataHelper(xlsxFile);
		List<String> columnA = excelInputDataHelper.loadColumn("A");
		Assertions.assertEquals(40, columnA.size());
		Assertions.assertEquals("1", columnA.get(0));
		Assertions.assertEquals("40", columnA.get(39));
	}

	@Test
	public void testLoadXlsxNumbersOnly() {
		URL inputFile = getClass().getClassLoader().getResource("xlsx/input-00.xlsx");
		assert inputFile != null;
		final File xlsxFile = new File(inputFile.getFile());
		ExcelInputDataHelper excelInputDataHelper = new ExcelInputDataHelper(xlsxFile);
		List<String> columnA = excelInputDataHelper.loadNumbersColumn("C");
		Assertions.assertEquals(36, columnA.size());
		Assertions.assertEquals("1", columnA.get(0));
		Assertions.assertEquals("40", columnA.get(35));
	}

	@Test
	public void testLoadXlsxCustomFilter() {
		URL inputFile = getClass().getClassLoader().getResource("xlsx/input-00.xlsx");
		assert inputFile != null;
		final File xlsxFile = new File(inputFile.getFile());
		ExcelInputDataHelper excelInputDataHelper = new ExcelInputDataHelper(xlsxFile);
		List<String> columnA = excelInputDataHelper.loadColumnWithFilter("C", (s) -> s.equals("Hello"));
		Assertions.assertEquals(4, columnA.size());
		Assertions.assertTrue(columnA.stream().allMatch((s) -> s.equals("Hello")));
	}

	@Test
	public void testLoadXlsxMoreData() {
		URL inputFile = getClass().getClassLoader().getResource("xlsx/input-01.xlsx");
		assert inputFile != null;
		final File xlsxFile = new File(inputFile.getFile());
		ExcelInputDataHelper excelInputDataHelper = new ExcelInputDataHelper(xlsxFile);
		List<String> columnA = excelInputDataHelper.loadNumbersColumn("A");
		Assertions.assertEquals(1129, columnA.size());
		Assertions.assertEquals("991301810105506", columnA.get(columnA.size() - 1));
	}

	@Test
	public void testLoadXlsxEvenMoreData() {
		URL inputFile = getClass().getClassLoader().getResource("xlsx/input-03.xlsx");
		assert inputFile != null;
		final File xlsxFile = new File(inputFile.getFile());
		ExcelInputDataHelper excelInputDataHelper = new ExcelInputDataHelper(xlsxFile);
		List<String> columnA = excelInputDataHelper.loadNumbersColumn("A");
		Assertions.assertEquals(4713, columnA.size());
		Assertions.assertEquals("991170744126005501", columnA.get(columnA.size() - 1));
	}
}
