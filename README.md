![build](https://github.com/HSG-Library/alma-sru-client/actions/workflows/build.yml/badge.svg)
[![](https://jitpack.io/v/HSG-Library/alma-sru-client.svg)](https://jitpack.io/#HSG-Library/alma-sru-client)
# Alma SRU client
The Alma SRU Client is a Java library that simplifies working with the Alma SRU interface.

## Main features:
- Generates classes for all searchable indexes found via the explain operation
- Provides a query builder for constructing queries using the generated index classes
- Provides a client builder, which builds a valid SRU URL and performs an HTTP GET request
- Wraps the response (when in `marcxml` format), so the result can be queried
- Provides helpers for file-based input and output
- Offers a fluent and Java Stream-ready interface

## Examples

### Build a query

```java
SruQuery query = SruQueryBuilder
    .create(Idx.inventoryNumber().containsPhrase("MN"))
    .and(Idx.inventoryDate().greaterThan("2020-10-12"))
    .and(Idx.inventoryDate().lessThan("2020-10-14"))
    .or(Idx.mmsId().equalTo("9953760105506"))
    .or(Idx.title().contains("rain"))
    .sort(Idx.title(),Sort.ASCENDING)
    .build();
```

### Submit a query with the SRU client

```java
Stream<MarcRecord> records = SruClientBuilder
    .create("https://your-domain.alma.exlibrisgroup.com/view/sru/YOUR-INST")
    .query(query)
    .maximumRecords(12)
    .getRecords();
```

### Get an explain response with the SRU client

```java
Optional<Document> explainResponse = SruClientBuilder
    .create("https://your-domain.alma.exlibrisgroup.com/view/sru/YOUR-INST")
    .explain()
    .getXmlResponse();
```

### Get subfields `800$$w` from response

```java
Optional<MarcRecord> singleRecord = SruClientBuilder
    .create("https://your-domain.alma.exlibrisgroup.com/view/sru/YOUR-INST")
    .query(query)
    .getSingleRecord();

List<Subfield> fields800w = singleRecord
    .map(record -> record.findSubfields("800","w"))
    .orElse(List.of());
```

### Get all records and write them to an XML file

```java
SruQuery sruQuery = SruQueryBuilder
    .create(Idx.mainPubDate().lessThan("1800"))
    .and(Idx.mainPubDate().greaterThan("1790"))
    .and(Idx.mmsMaterialType().equalTo("BK"))
    .build();

Stream<MarcRecord> allRecords = SruClientBuilder
    .create("https://your-domain.alma.exlibrisgroup.com/view/sru/YOUR-INST")
    .query(sruQuery)
    .getAllRecords();

XmlOutputHelper xmlOutputHelper = new XmlOutputHelper();
File tempFile = Files.createTempFile("alma-sru", ".xml").toFile();
xmlOutputHelper.saveSingleFile(allRecords, tempFile);
```

### Submit query and write MMSID, title and author to an Excel file

```java
SruQuery query = SruQueryBuilder
    .create(Idx.title().containsWords("tree"))
    .build();

Stream<MarcRecord> allRecords = SruClientBuilder
    .create("https://your-domain.alma.exlibrisgroup.com/view/sru/YOUR-INST")
    .query(query)
    .maximumRecords(45)
    .getRecords();

List<List<String>> rows = allRecords.map(record -> {
  final String mmsId = record.getControlField("001")
      .map(ControlField::getText)
      .orElse("no-mmsid");
  final String title = record.findDataFields("245")
      .stream()
      .map(DataField::getSubfields)
      .flatMap(List::stream)
      .map(SubField::getText)
      .collect(Collectors.joining(" "));
  final String authors = record.findSubFields("100", "a")
      .stream()
      .map(SubField::getText)
      .collect(Collectors.joining(", "));
  return List.of(mmsId, title, authors);
}).toList();

File outputFile = Files.createTempFile("alma-sru", ".xlsx").toFile();
ExcelOutputHelper excelOutputHelper = new ExcelOutputHelper(outputFile);
excelOutputHelper.writeToExcel(rows);
```


## How to use in your project

Include the `alma-sru-client` library in your Maven or Gradle project using [JitPack](https://jitpack.io/).

### Maven

Add the following to the `pom.xml` file:

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

and with the according release tag:

```xml
<dependency>
	<groupId>com.github.HSG-Library</groupId>
	<artifactId>alma-sru-client</artifactId>
	<version>[latest-tag]</version>
</dependency>
```

### Without dependency management
Download the latest JAR file from the [GitHub releases page](https://github.com/HSG-Library/alma-sru-client/releases
).

## How to generate the SRU-index classes

If you build with Maven, add the following to your `pom.xml`:

```xml

<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-compiler-plugin</artifactId>
			<version>3.11.0</version>
			<configuration>
				<source>17</source>
				<target>17</target>
				<generatedSourcesDirectory>${project.build.directory}/generated-sources</generatedSourcesDirectory>
				<compilerArgs>
					<arg>-Ajava.io.tmpdir=${project.build.directory}/generated-sources</arg>
				</compilerArgs>
			</configuration>
			<executions>
				<execution>
					<id>compile-source-generator</id>
					<phase>generate-sources</phase>
					<goals>
						<goal>compile</goal>
					</goals>
					<configuration>
						<includes>
							<include>ch/unisg/library/systemlibrarian/sru/generator/*.java</include>
						</includes>
					</configuration>
				</execution>
			</executions>
		</plugin>
		<plugin>
			<groupId>org.codehaus.mojo</groupId>
			<artifactId>exec-maven-plugin</artifactId>
			<version>3.1.0</version>
			<executions>
				<execution>
					<id>generate</id>
					<phase>generate-sources</phase>
					<goals>
						<goal>java</goal>
					</goals>
				</execution>
			</executions>
			<configuration>
				<mainClass>ch.unisg.library.systemlibrarian.sru.generator.SruIndexGenerator</mainClass>
				<arguments>
					<!--sru base url  -->
					<argument>https://your-domain.alma.exlibrisgroup.com/view/sru/YOUR-INST</argument>
					<!--output directory -->
					<argument>target/generated-sources/</argument>
				</arguments>
			</configuration>
		</plugin>
	</plugins>
</build>
```
Or without Maven, manually call the generator by running the following code:
```java 
import ch.unisg.library.systemlibrarian.sru.generator.SruIndexGenerator;
import java.nio.file.Path;

public class RunGenerator {
	public static void main(String[] args) {
		final String baseUrl = "https://your-domain.alma.exlibrisgroup.com/view/sru/YOUR-INST";
		final Path outputDir = Path.of("target/generated-sources/");
		final SruIndexGenerator sruIndexGenerator = new SruIndexGenerator();
		sruIndexGenerator.generate(baseUrl, outputDir);
	}
}
```
