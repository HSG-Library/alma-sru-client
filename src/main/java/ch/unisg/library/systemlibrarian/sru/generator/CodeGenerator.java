package ch.unisg.library.systemlibrarian.sru.generator;

import ch.unisg.library.systemlibrarian.sru.query.Clause;
import ch.unisg.library.systemlibrarian.sru.query.Relation;
import ch.unisg.library.systemlibrarian.sru.query.SruIndex;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CodeGenerator {

	private final Path outputPath;
	private final List<String> usedClassNames;

	public CodeGenerator(final Path outputPath) {
		this.outputPath = outputPath;
		this.usedClassNames = new LinkedList<>();
	}

	public TypeSpec generateIndexClass(final SruIndexMeta indexMeta) {

		final FieldSpec logField = FieldSpec.builder(Logger.class, "LOG")
				.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("$T.$L($T.$L().$L())", LoggerFactory.class, "getLogger", MethodHandles.class, "lookup", "lookupClass")
				.build();

		final FieldSpec titleField = createStaticStringField("TITLE", indexMeta.getTitle());
		final FieldSpec nameField = createStaticStringField("NAME", indexMeta.getName());
		final FieldSpec setField = createStaticStringField("SET", indexMeta.getSet());

		final FieldSpec sortField = createStaticBooleanField("SORT", indexMeta.isSort());
		final FieldSpec emptyTermField = createStaticBooleanField("EMPTY_TERM", indexMeta.isEmptyTerm());

		final FieldSpec relationsField = FieldSpec.builder(ParameterizedTypeName.get(List.class, Relation.class), "RELATIONS")
				.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("$T.of($L)", List.class,
						indexMeta.getRelations().stream()
								.map(r -> ClassName.get("", Relation.class.getSimpleName()) + "." + r.name()).collect(Collectors.joining(", ")))
				.build();

		final MethodSpec isRelationSupportedMethod = MethodSpec.methodBuilder("isRelationSupported")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(ParameterSpec.builder(Relation.class, "relation", Modifier.FINAL).build())
				.addAnnotation(Override.class)
				.returns(TypeName.BOOLEAN)
				.addStatement("return $N.$L($N)", relationsField.name, "contains", "relation")
				.build();

		final AnnotationSpec generated = getGeneratedAnnotation();

		TypeSpec.Builder indexTypeBuilder = TypeSpec.classBuilder(toClassName(indexMeta.getName()));
		indexTypeBuilder
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addSuperinterface(TypeName.get(SruIndex.class))
				.addAnnotation(generated)
				.addField(logField)
				.addField(titleField)
				.addField(nameField)
				.addField(setField)
				.addField(sortField)
				.addField(emptyTermField)
				.addField(relationsField)
				.addMethod(createGetter("getTitle", titleField))
				.addMethod(createGetter("getName", nameField))
				.addMethod(createGetter("getSet", setField))
				.addMethod(createGetter("isSortable", sortField))
				.addMethod(isRelationSupportedMethod);
		indexMeta.getRelations().forEach(relation -> indexTypeBuilder.addMethod(createRelationMethod(relation, emptyTermField, logField, titleField)));
		TypeSpec indexType = indexTypeBuilder.build();
		JavaFile javaFile = JavaFile.builder("ch.unisg.library.systemlibrarian.sru.query.index", indexType)
				.build();
		try {
			javaFile.writeTo(outputPath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return indexType;
	}

	public void generateIndexList(final List<TypeSpec> indexTypes) {
		TypeSpec.Builder indexListTypeBuilder = TypeSpec.classBuilder("Idx")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addAnnotation(getGeneratedAnnotation());
		indexTypes.forEach(indexType -> indexListTypeBuilder.addMethod(createIndexListGetter(indexType)));
		TypeSpec indexListType = indexListTypeBuilder.build();
		JavaFile javaFile = JavaFile.builder("ch.unisg.library.systemlibrarian.sru.query.index", indexListType)
				.build();
		try {
			javaFile.writeTo(outputPath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private AnnotationSpec getGeneratedAnnotation() {
		return AnnotationSpec.builder(Generated.class)
				.addMember("value", "$S", this.getClass().getName())
				.addMember("date", "$S", LocalDateTime.now().toString())
				.build();
	}

	private FieldSpec createStaticStringField(final String name, final String value) {
		return FieldSpec.builder(String.class, name)
				.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("$S", value)
				.build();
	}

	private FieldSpec createStaticBooleanField(final String name, final boolean value) {
		return FieldSpec.builder(TypeName.BOOLEAN, name)
				.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("$L", value)
				.build();
	}

	private MethodSpec createGetter(final String methodName, final FieldSpec field) {
		return MethodSpec.methodBuilder(methodName)
				.addModifiers(Modifier.PUBLIC)
				.returns(field.type)
				.addAnnotation(Override.class)
				.addStatement("return $N", field.name)
				.build();
	}

	private MethodSpec createRelationMethod(final Relation relation, final FieldSpec emptyField, final FieldSpec logField, final FieldSpec titleField) {
		return MethodSpec.methodBuilder(toMethodName(relation))
				.addModifiers(Modifier.PUBLIC)
				.addParameter(ParameterSpec.builder(String.class, "value", Modifier.FINAL).build())
				.returns(Clause.class)
				.beginControlFlow("if(!$N && $T.$L($N))", emptyField.name, StringUtils.class, "isBlank", "value")
				.addStatement("$N.$L($S, $N)", logField.name, "warn", "The index '{}' does not support empty terms.", titleField.name)
				.endControlFlow()
				.addStatement("return new $T($L, $N, $T.$L)", Clause.class, "this", "value", ClassName.get("", Relation.class.getSimpleName()), relation.name())
				.build();
	}

	private MethodSpec createIndexListGetter(final TypeSpec indexType) {
		return MethodSpec.methodBuilder(StringUtils.uncapitalize(indexType.name))
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addStatement("return new $T()", ClassName.bestGuess(indexType.name))
				.returns(ClassName.bestGuess(indexType.name))
				.build();
	}

	private String toClassName(final String indexName) {
		Pattern pattern = Pattern.compile("([a-z])_([a-z])");
		Matcher matcher = pattern.matcher(indexName);
		String output = matcher.replaceAll(result -> result.group(1) + result.group(2).toUpperCase());
		output = StringUtils.removeStart(output, "_");
		output = StringUtils.removeEnd(output, "_");
		output = StringUtils.capitalize(output);
		String className = output;
		int index = 0;
		while (usedClassNames.contains(className)) {
			className = output + index;
			index += 1;
		}
		usedClassNames.add(className);
		return className;
	}

	private String toMethodName(final Relation relation) {
		Pattern pattern = Pattern.compile("([a-z])_([a-z])");
		Matcher matcher = pattern.matcher(relation.name().toLowerCase());
		return matcher.replaceAll(result -> result.group(1) + result.group(2).toUpperCase());
	}
}
