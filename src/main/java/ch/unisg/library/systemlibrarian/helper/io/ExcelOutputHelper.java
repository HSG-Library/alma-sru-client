package ch.unisg.library.systemlibrarian.helper.io;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelOutputHelper {

	private final File outputFile;

	public ExcelOutputHelper(final File outputFile) {
		this.outputFile = outputFile;
	}

	public File writeToExcel(final List<List<String>> rows) {
		try (FileOutputStream os = new FileOutputStream(outputFile); Workbook wb = new Workbook(os, "SRU", "0.1")) {
			final Worksheet worksheet = wb.newWorksheet("Sheet 1");
			for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
				List<String> row = rows.get(rowIndex);
				for (int colIndex = 0; colIndex < row.size(); colIndex++) {
					worksheet.value(rowIndex, colIndex, row.get(colIndex));
				}
			}
			worksheet.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this.outputFile;
	}

	public File writeToExcel(final Map<String, List<String>> rowsWithLeadColumn) {
		try (FileOutputStream os = new FileOutputStream(outputFile); Workbook wb = new Workbook(os, "SRU", "0.1")) {
			final Worksheet worksheet = wb.newWorksheet("Sheet 1");
			final var entries = rowsWithLeadColumn.entrySet().stream().collect(Collectors.toList());
			for (int rowIndex = 0; rowIndex < entries.size(); rowIndex++) {
				final var entry = entries.get(rowIndex);
				worksheet.value(rowIndex, 0, entry.getKey());
				worksheet.style(rowIndex, 0).bold().set();
				for (int colIndex = 0; colIndex < entry.getValue().size(); colIndex++) {
					final String value = entry.getValue().get(colIndex);
					worksheet.value(rowIndex, colIndex + 1, value);
				}
			}
			worksheet.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this.outputFile;
	}
}
