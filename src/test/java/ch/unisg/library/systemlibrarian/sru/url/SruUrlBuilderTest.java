package ch.unisg.library.systemlibrarian.sru.url;

import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.query.SruQueryBuilder;
import ch.unisg.library.systemlibrarian.sru.query.index.Idx;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SruUrlBuilderTest {

    @Test
    public void testBasicBuilder() {
        final String base = "https://base.example.com/something/sru/";
        final SruQuery query = SruQueryBuilder.create(Idx.mmsId().equalTo("0000")).build();

        final String expectedUrl01 = base + "?query=" + query.string() + "&version=1.2&recordSchema=marcxml&startRecord=1&operation=searchRetrieve&maximumRecords=50";
        SruUrl sruUrl01 = SruUrlBuilder.create(base)
                .query(query)
                .build();
        TreeSet<String> queryParams = new TreeSet<>(Arrays.asList(sruUrl01.getUrl().getQuery().split("&")));
        TreeSet<String> expectedQuerParams = new TreeSet<>(Arrays.asList(URI.create(expectedUrl01).getQuery().split("&")));
        assertEquals(expectedQuerParams, queryParams);
        assertEquals(StringUtils.substringBefore(expectedUrl01, "?"), StringUtils.substringBefore(sruUrl01.getUrl().toString(), "?"));
    }

    @Test
    public void testSetAllParams() {
        final String base = "https://base.example.com/something/sru/";
        final SruQuery query = SruQueryBuilder.create(Idx.mmsId().equalTo("0000")).build();

        final String expectedUrl01 = base + "?query=" + query.string() + "&version=1.2&recordSchema=isohold&startRecord=13&operation=explain&maximumRecords=100";
        SruUrl sruUrl01 = SruUrlBuilder.create(base)
                .maximumRecords(100)
                .startRecord(13)
                .version(SruUrlBuilder.Version.v1_2)
                .operation(SruUrlBuilder.Operation.EXPLAIN)
                .recordSchema(SruUrlBuilder.RecordSchema.ISOHOLD)
                .query(query)
                .build();
        TreeSet<String> queryParams = new TreeSet<>(Arrays.asList(sruUrl01.getUrl().getQuery().split("&")));
        TreeSet<String> expectedQuerParams = new TreeSet<>(Arrays.asList(URI.create(expectedUrl01).getQuery().split("&")));
        assertEquals(expectedQuerParams, queryParams);
        assertEquals(StringUtils.substringBefore(expectedUrl01, "?"), StringUtils.substringBefore(sruUrl01.getUrl().toString(), "?"));
    }
}
