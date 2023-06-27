package ch.unisg.library.systemlibrarian;

import ch.unisg.library.systemlibrarian.helper.DomUtil;
import ch.unisg.library.systemlibrarian.sru.SruClient;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestDataHelper {

	public String getResponseFromFile(final String fileName) throws IOException {
		URL inputFile = getClass().getClassLoader().getResource("sru-responses/" + fileName);
		assert inputFile != null;
		final Path responseFile = new File(inputFile.getFile()).toPath();
		return Files.readString(responseFile, StandardCharsets.UTF_8);
	}

	public Stream<MarcRecord> getRecordStreamFromXml(final String xml) {
		final SruClient sruClient = new SruClient();
		return sruClient.extractRecords(DomUtil.getDocumentFromXmlString(xml));
	}
}
