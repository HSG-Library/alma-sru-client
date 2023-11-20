# Development Notes

## How to release
### Via GitHub actions
- Release workflow: [.github/workflows/release.yml](.github/workflows/release.yml)
- Release is triggered when a tag which matches a semver format is created
```shell
git tag -a 0.0.0 -m "0.0.0"
git push --tags
```
- Maven runs automatically and the release will be uploaded to GitHub

### Manually
- Set the correct version, this creates a backup of the pom file, which can be discarded (if everything is ok)
```shell
mvn versions:set -DnewVersion=<version>
```
or run the following to remove the backup poms automatically
```shell
mvn versions:set -DnewVersion=<version> && mvn versions:commit
```

- Run Maven with goal `package` and profile `release`
```shell
mvn -Prelease package
```
- The resulting package can be found in the Maven target directory

## How to update dependencies
- Display updates, but only for dependencies where the versions are managed in the pom-properties section
```shell
mvn versions:display-property-updates
```
- Actually update the versions managed in the pom-properties section, this creates a backup of the pom file, which can be discarded (if everything is ok)
```shell
mvn versions:update-properties
```
or run the following to remove the backup poms automatically
```shell
mvn versions:update-properties && mvn versions:commit
```
