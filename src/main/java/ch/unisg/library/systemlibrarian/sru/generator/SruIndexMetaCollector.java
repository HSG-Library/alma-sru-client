package ch.unisg.library.systemlibrarian.sru.generator;

import ch.unisg.library.systemlibrarian.helper.HttpXmlClientHelper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class SruIndexMetaCollector {
	private static final String EXPLAIN_URL = "https://slsp-hsg.alma.exlibrisgroup.com/view/sru/41SLSP_HSG?version=1.2&operation=explain";

	public List<SruIndexMeta> collectSruIndexMeta() {
		final Document explainDoc = getExplainDocument()
				.orElseThrow(() -> new IllegalStateException("Could not get SRU explain response"));

		final NodeList indexElements = explainDoc.getElementsByTagName("index");
		return IntStream.range(0, indexElements.getLength())
				.mapToObj(indexElements::item)
				.map(indexNode -> new SruIndexMeta.Factory().create(indexNode))
				.toList();
	}

	private Optional<Document> getExplainDocument() {
		HttpXmlClientHelper xmlClientHelper = new HttpXmlClientHelper();
		return xmlClientHelper.call(getUri());
	}

	private URI getUri() {
		try {
			return new URI(EXPLAIN_URL);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
