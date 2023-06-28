package ch.unisg.library.systemlibrarian.sru.url;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SruUrlTest {

    @Test
    public void testSruUrlMaximumRecords() {
        SruUrl sruUrl1 = new SruUrl("https://base.example.com/sru/", Map.of(
                SruUrl.VERSION, SruUrlBuilder.Version.v1_2.getValue(),
                SruUrl.MAXIMUM_RECORDS, "10",
                SruUrl.START_RECORD, "1"
        ));
        // make sure SruUrl is immutable, withMaximumRecords should return a new SruUrl
        SruUrl sruUrl2 = sruUrl1.withMaximumRecords(20);
        assertNotEquals(sruUrl1, sruUrl2);
        TreeSet<String> queryParams = new TreeSet<>(Arrays.asList(sruUrl1.getUrl().getQuery().split("&")));
        TreeSet<String> expectedQuerParams = new TreeSet<>(Arrays.asList(sruUrl2.getUrl().getQuery().split("&")));
        assertNotEquals(expectedQuerParams, queryParams);
        // make sure the resulting Url stays is the same when using the same values
        SruUrl sruUrl3 = sruUrl2.withMaximumRecords(10);
        assertNotEquals(sruUrl2, sruUrl3);
        queryParams = new TreeSet<>(Arrays.asList(sruUrl3.getUrl().getQuery().split("&")));
        expectedQuerParams = new TreeSet<>(Arrays.asList(sruUrl1.getUrl().getQuery().split("&")));
        assertEquals(expectedQuerParams, queryParams);
        assertEquals(StringUtils.substringBefore(sruUrl2.getUrl().toString(), "?"), StringUtils.substringBefore(sruUrl3.getUrl().toString(), "?"));
    }

    @Test
    public void testSruUrlStartRecord() {
        SruUrl sruUrl1 = new SruUrl("https://base.example.com/sru/", Map.of(
                SruUrl.VERSION, SruUrlBuilder.Version.v1_2.getValue(),
                SruUrl.MAXIMUM_RECORDS, "10",
                SruUrl.START_RECORD, "1"
        ));
        // make sure SruUrl is immutable, withMaximumRecords should return a new SruUrl
        SruUrl sruUrl2 = sruUrl1.withStartRecord(15);
        assertNotEquals(sruUrl1, sruUrl2);
        TreeSet<String> queryParams = new TreeSet<>(Arrays.asList(sruUrl1.getUrl().getQuery().split("&")));
        TreeSet<String> expectedQuerParams = new TreeSet<>(Arrays.asList(sruUrl2.getUrl().getQuery().split("&")));
        assertNotEquals(expectedQuerParams, queryParams);

        // make sure the resulting Url stays is the same when using the same values
        SruUrl sruUrl3 = sruUrl2.withStartRecord(1);
        assertNotEquals(sruUrl2, sruUrl3);
        queryParams = new TreeSet<>(Arrays.asList(sruUrl3.getUrl().getQuery().split("&")));
        expectedQuerParams = new TreeSet<>(Arrays.asList(sruUrl1.getUrl().getQuery().split("&")));
        assertEquals(expectedQuerParams, queryParams);
        assertEquals(StringUtils.substringBefore(sruUrl2.getUrl().toString(), "?"), StringUtils.substringBefore(sruUrl3.getUrl().toString(), "?"));
    }
}
