package ch.unisg.library.systemlibrarian.scripts;

import ch.unisg.library.systemlibrarian.file.XmlOutputHelper;
import ch.unisg.library.systemlibrarian.sru.SruClient;
import ch.unisg.library.systemlibrarian.sru.SruUrlBuilder;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.query.SruQueryBuilder;
import ch.unisg.library.systemlibrarian.sru.query.index.Idx;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FindSomething {
	private static final String BASE = "https://slsp-hsg.alma.exlibrisgroup.com/view/sru/41SLSP_HSG";

	public static void main(String[] args) {
		new FindSomething().run();
	}

	public void run() {
		SruQuery sruQuery = SruQueryBuilder.create(Idx.title().containsPhrase("zombie"))
				.and(Idx.mmsMaterialType().equalTo("BK"))
				.build();

		SruUrlBuilder sruUrlBuilder = new SruUrlBuilder(BASE)
				.query(sruQuery);

		SruClient sruClient = new SruClient();
		Stream<MarcRecord> allRecordsForSingleFile = sruClient.getAllRecords(sruUrlBuilder);
		Stream<MarcRecord> allRecordsForIndividualFiles = sruClient.getAllRecords(sruUrlBuilder);

		//allRecords.forEach(System.out::println);

		XmlOutputHelper xmlOutputHelper = new XmlOutputHelper();
		final File outputCombined = new File("/Users/jonas/tmp/records.xml");
		xmlOutputHelper.saveSingleFile(allRecordsForSingleFile, outputCombined);
		final Path outputSingle = new File("/Users/jonas/tmp/single").toPath();
		xmlOutputHelper.saveEachRecord(allRecordsForIndividualFiles, outputSingle);
	}
}
