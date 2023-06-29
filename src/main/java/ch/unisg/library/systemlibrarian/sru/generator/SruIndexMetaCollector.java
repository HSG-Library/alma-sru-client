package ch.unisg.library.systemlibrarian.sru.generator;

import ch.unisg.library.systemlibrarian.sru.client.SruClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class SruIndexMetaCollector {

	public List<SruIndexMeta> collectSruIndexMeta(final String sruBaseUrl) {
		final Document explainDoc = getExplainDocument(sruBaseUrl)
				.orElseThrow(() -> new IllegalStateException("Could not get SRU explain response"));

		final NodeList indexElements = explainDoc.getElementsByTagName("index");
		return IntStream.range(0, indexElements.getLength())
				.mapToObj(indexElements::item)
				.map(indexNode -> new SruIndexMeta.Factory().create(indexNode))
				.toList();
	}

	private Optional<Document> getExplainDocument(final String sruBaseUrl) {
		return SruClientBuilder.create(sruBaseUrl)
				.explain()
				.getXmlResponse();
	}
}
