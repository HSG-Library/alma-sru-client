package ch.unisg.library.systemlibrarian.sru.url;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SruUrl {

    public static final String VERSION = "version";
    public static final String OPERATION = "operation";
    public static final String QUERY = "query";
    public static final String START_RECORD = "startRecord";
    public static final String MAXIMUM_RECORDS = "maximumRecords";
    public static final String RECORD_SCHEMA = "recordSchema";

    private final URI url;
    private final String base;
    private final Map<String, String> parameters;

    SruUrl(final String base, final Map<String, String> parameters) {
        this.base = base;
        this.parameters = new HashMap<>(parameters);
        this.url = URI.create(base + "?" + joinParameters(parameters));
    }

    public SruUrl withStartRecord(final int startRecord) {
        parameters.put(START_RECORD, String.valueOf(startRecord));
        return new SruUrl(base, parameters);
    }

    public SruUrl withMaximumRecords(final int maximumRecords) {
        parameters.put(MAXIMUM_RECORDS, String.valueOf(maximumRecords));
        return new SruUrl(base, parameters);
    }

    public URI getUrl() {
        return url;
    }

    private String joinParameters(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SruUrl sruUrl = (SruUrl) o;

        return url.equals(sruUrl.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return this.url.toString();
    }
}
