package ch.unisg.library.systemlibrarian.file;

import ch.unisg.library.systemlibrarian.helper.XmlHelper;
import ch.unisg.library.systemlibrarian.sru.response.Controlfield;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

public class XmlOutputHelper {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public void saveSingleFile(final Stream<MarcRecord> records, final File outputFile) {
		final XmlHelper xmlHelper = new XmlHelper();
		final Document allRecords = xmlHelper.combineToDocument("records", records.map(MarcRecord::getRecordNode));
		final String allRecordsXml = xmlHelper.nodeToString(allRecords);
		writeString(outputFile.toPath(), allRecordsXml);
	}

	public void saveEachRecord(final Stream<MarcRecord> records, final Path outputDirectory) {
		records.forEach(record -> {
			Optional<Controlfield> mmsId = record.getControlfield("001");
			final String fileName = mmsId
					.map(Controlfield::getText)
					.map(id -> String.join(getDatePrefix(), id + ".xml"))
					.orElseGet(() -> String.join(getDatePrefix(), RandomStringUtils.random(10), "-") + ".xml");
			final String recordXml = record.toString();
			final File outputFile = new File(outputDirectory.toString(), fileName);
			writeString(outputFile.toPath(), recordXml);
		});
	}

	private void writeString(final Path outputFilePath, final String outputString) {
		try {
			LOG.info("Write file '{}'", outputFilePath);
			if (!Files.exists(outputFilePath.getParent())) {
				Files.createDirectories(outputFilePath.getParent());
			}
			Files.writeString(outputFilePath, outputString, StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOG.error("Could not write output file '{}', skipping.", outputFilePath, e);
		}
	}

	private String getDatePrefix() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-ssSSS");
		return now.format(formatter);
	}
}
