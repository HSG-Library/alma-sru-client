package ch.unisg.library.systemlibrarian.scripts;

import ch.unisg.library.systemlibrarian.file.ExcelInputHelper;
import ch.unisg.library.systemlibrarian.file.ExcelOutputHelper;
import ch.unisg.library.systemlibrarian.sru.client.SruClientBuilder;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.query.SruQueryBuilder;
import ch.unisg.library.systemlibrarian.sru.query.index.Idx;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;

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
		final SruQuery query = SruQueryBuilder.create(Idx.mmsId().equalTo(mmsId)).build();
		return SruClientBuilder.create(getBaseUrl())
				.query(query)
				.getSingleRecord();
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
