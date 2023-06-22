package ch.unisg.library.systemlibrarian.sru.query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SruQueryBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final List<Clause> clauses;
	private SruIndex sortBy;
	private Sort sortDirection;

	private SruQueryBuilder() {
		this.clauses = new ArrayList<>();
	}

	public static SruQueryBuilder create(final Clause clause) {
		SruQueryBuilder sruQueryBuilder = new SruQueryBuilder();
		sruQueryBuilder.clauses.add(clause);
		return sruQueryBuilder;
	}

	public SruQueryBuilder or(final Clause clause) {
		this.clauses.add(BoolOpWrapper.OR.wrap(clause));
		return this;
	}

	public SruQueryBuilder and(final Clause clause) {
		this.clauses.add(BoolOpWrapper.AND.wrap(clause));
		return this;
	}

	public SruQueryBuilder sort(final SruIndex index, final Sort direction) {
		if (!index.isSortable()) {
			LOG.warn("Index '{}' is not sortable, ignore sort specification.", index.getTitle());
			return this;
		}
		this.sortBy = index;
		this.sortDirection = direction;
		return this;
	}

	public SruQuery build() {
		final String queryString = clauses.stream()
				.map(Clause::string)
				.map(clauseString -> URLEncoder.encode(clauseString, StandardCharsets.UTF_8))
				.collect(Collectors.joining());
		return new SruQuery(queryString + getSortSpec());
	}

	private String getSortSpec() {
		if (sortBy != null && sortDirection != null) {
			return " sortBy " + sortBy.getName() + "/" + sortDirection.getValue();
		}
		return StringUtils.EMPTY;
	}
}
