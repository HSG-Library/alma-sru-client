![build](https://github.com/HSG-Library/alma-sru-client/actions/workflows/build.yml/badge.svg)
[![](https://jitpack.io/v/HSG-Library/alma-sru-client.svg)](https://jitpack.io/#HSG-Library/alma-sru-client)
# Alma SRU client
This is a Java library which makes the work with the Alma SRU interface easier.

The Alma SRU client has the following features:

- It generates classes for all searchable indexes, found via explain operation
- It provides a query builder, which allows to fluently build queries using the generated index classes
- It provides a client, which builds a valid SRU URL and performs an HTTP GET request
- It wraps the response (when in `marcxml` format), so the result can be queried
- It provides helper for file based input and output.

## Examples

### Build a query

```java
SruQuery query=SruQueryBuilder
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
Stream<MarcRecord> records=SruClientBuilder.create("https://alma.domain.com/view/sru/institution code")
		.query(query)
		.maximumRecords(12)
		.getRecords();
```

### Get an explain response with the SRU client

```java
Optional<Document> explainResponse=SruClientBuilder.create("https://alma.domain.com/view/sru/institution code")
		.explain()
		.getXmlResponse();
```

### Get subfields `800$$w` from response

```java
Optional<MarcRecord> singleRecord=SruClientBuilder.create("https://alma.domain.com/view/sru/institution code")
		.query(query)
		.getSingleRecord();

		List<Subfield> fields800w=singleRecord
		.map(record->record.findSubfields("800","w"))
		.orElse(List.of());
```

## How to use in your project

Include the `alma-sru-client` into your Maven or Gradle project via https://jitpack.io/.

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

### Gradle

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

and with the according release tag:

```groovy
dependencies {
    implementation 'com.github.HSG-Library:alma-sru-client:<latest-tag>'
}
```

## How to generate the index classes

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


