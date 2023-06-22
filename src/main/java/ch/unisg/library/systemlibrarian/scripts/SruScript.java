package ch.unisg.library.systemlibrarian.scripts;

import ch.unisg.library.systemlibrarian.file.ExcelInputDataHelper;
import ch.unisg.library.systemlibrarian.file.ExcelOutputDataHelper;
import ch.unisg.library.systemlibrarian.sru.SruClient;
import ch.unisg.library.systemlibrarian.sru.SruUrlBuilder;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.response.Record;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SruScript {

	SruScript input(final String path, final String column);

	File getInput();

	SruScript output(final String path);

	File getOutput();

	String getBaseUrl();

	Map<String, List<String>> getResults(List<String> mmsIds);

	void processFiles();

	default Optional<Record> getRecord(final String mmsId) {
		final String query = "mms_id=" + mmsId;
		final SruUrlBuilder urlBuilder = new SruUrlBuilder(getBaseUrl())
				.query(new SruQuery(query));
		SruClient sru = new SruClient();
		return sru.getSingleRecord(urlBuilder);
	}

	default List<String> getMmsIdsFromExcel(final String column) {
		return new ExcelInputDataHelper(getInput()).loadNumbersColumn(column);
	}

	default void writeToExcel(List<List<String>> rows) {
		new ExcelOutputDataHelper(getOutput()).writeToExcel(rows);
	}

	default void writeToExcel(Map<String, List<String>> rows) {
		new ExcelOutputDataHelper(getOutput()).writeToExcel(rows);
	}
}
