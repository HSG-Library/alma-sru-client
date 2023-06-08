package ch.unisg.library.systemlibrarian.file;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellAddress;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Predicate;

public class ExcelInputDataHelper {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final File xlsxFile;

	public ExcelInputDataHelper(final File xlsxFile) {
		this.xlsxFile = xlsxFile;
	}

	public List<String> loadColumn(final String column) {
		return loadColumnWithFilter(column, (text) -> true);
	}

	public List<String> loadNumbersColumn(final String column) {
		return loadColumnWithFilter(column, (text) -> text.matches("[0-9]+"));
	}

	public List<String> loadColumnWithFilter(final String column, final Predicate<String> textFilter) {
		LOG.info("Loading data from file '{}'", xlsxFile);
		try (ReadableWorkbook wb = new ReadableWorkbook(xlsxFile)) {
			Sheet firstSheet = wb.getFirstSheet();
			return firstSheet.openStream()
					.map(row -> row.getCell(new CellAddress(column + row.getRowNum())))
					.map(Cell::getText)
					.filter(textFilter)
					.toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
