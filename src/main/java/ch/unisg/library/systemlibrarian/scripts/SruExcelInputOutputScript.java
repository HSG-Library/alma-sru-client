package ch.unisg.library.systemlibrarian.scripts;

import ch.unisg.library.systemlibrarian.file.ExcelInputHelper;
import ch.unisg.library.systemlibrarian.file.ExcelOutputHelper;
import ch.unisg.library.systemlibrarian.sru.SruClient;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import ch.unisg.library.systemlibrarian.sru.url.SruUrl;
import ch.unisg.library.systemlibrarian.sru.url.SruUrlBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SruExcelInputOutputScript {

	SruExcelInputOutputScript input(final String path, final String column);

	File getInput();

	SruExcelInputOutputScript output(final String path);

	File getOutput();

	String getBaseUrl();

	Map<String, List<String>> getResults(List<String> mmsIds);

	void processFiles();

	default Optional<MarcRecord> getRecord(final String mmsId) {
		final String query = "mms_id=" + mmsId;
		final SruUrl sruUrl = SruUrlBuilder.create(getBaseUrl())
				.query(new SruQuery(query))
				.build();
		SruClient sru = new SruClient();
		return sru.getSingleRecord(sruUrl);
	}

	default List<String> getMmsIdsFromExcel(final String column) {
		return new ExcelInputHelper(getInput()).loadNumbersColumn(column);
	}

	default void writeToExcel(List<List<String>> rows) {
		new ExcelOutputHelper(getOutput()).writeToExcel(rows);
	}

	default void writeToExcel(Map<String, List<String>> rows) {
		new ExcelOutputHelper(getOutput()).writeToExcel(rows);
	}
}
