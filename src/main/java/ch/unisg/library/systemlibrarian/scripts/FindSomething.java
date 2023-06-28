package ch.unisg.library.systemlibrarian.scripts;

import ch.unisg.library.systemlibrarian.file.XmlOutputHelper;
import ch.unisg.library.systemlibrarian.sru.SruClient;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.query.SruQueryBuilder;
import ch.unisg.library.systemlibrarian.sru.query.index.Idx;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import ch.unisg.library.systemlibrarian.sru.url.SruUrl;
import ch.unisg.library.systemlibrarian.sru.url.SruUrlBuilder;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FindSomething {
	private static final String BASE = "https://slsp-hsg.alma.exlibrisgroup.com/view/sru/41SLSP_HSG";

	public static void main(String[] args) {
		new FindSomething().run();
	}

	public void run() {
		final SruQuery sruQuery = SruQueryBuilder.create(Idx.title().containsPhrase("zombie"))
				.and(Idx.mmsMaterialType().equalTo("BK"))
				.build();

		final SruUrl sruUrl = SruUrlBuilder.create(BASE)
				.query(sruQuery)
				.build();

		SruClient sruClient = new SruClient();
		Stream<MarcRecord> allRecordsForSingleFile = sruClient.getAllRecords(sruUrl);
		Stream<MarcRecord> allRecordsForIndividualFiles = sruClient.getAllRecords(sruUrl);

		//allRecords.forEach(System.out::println);

		XmlOutputHelper xmlOutputHelper = new XmlOutputHelper();
		final File outputCombined = new File("/Users/jonas/tmp/records.xml");
		xmlOutputHelper.saveSingleFile(allRecordsForSingleFile, outputCombined);
		final Path outputSingle = new File("/Users/jonas/tmp/single").toPath();
		xmlOutputHelper.saveEachRecord(allRecordsForIndividualFiles, outputSingle);
	}
}
