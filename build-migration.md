### What the current Maven build covers
- Compiles Java sources with Java 8 target and produces a single `efa-0.0.1-SNAPSHOT.jar`.
- Uses your existing `META-INF/MANIFEST.MF` via `maven-jar-plugin`.
- Declares most third‑party libraries from Maven Central (two still system‑scoped).

This is a good start for “compile the app,” but it does not replicate what `tools/makeDist.sh` assembles as a distributable.

### What is missing compared to `makeDist.sh`
`makeDist.sh` performs a full distribution assembly, not just compilation. The Maven build is missing these functional pieces:

1) Distribution layout and packaging
- MakeDist creates a tree `makedist/` with subfolders and then archives it to `dist/efa<version>.zip` and `dist/efa<version>.tar`.
- Maven currently does not create any ZIP/TAR with the expected layout.

2) Bundling runtime dependencies as external JARs
- MakeDist copies plugin/runtime jars into `program/plugins/` next to `efa.jar`.
- Maven declares dependencies but does not produce a folder of runtime jars. The built JAR does not include dependencies (no shade/assembly).

3) Application resources
- MakeDist copies lots of non‑code resources into the distribution:
    - `cfg/*.cfg` → `cfg/`
    - Help/online help: copies `help/**` and then builds `efahelp.jar`
    - Docs: `help/main/*.gif|*.png|*.html` → `doc/`
    - Root scripts and launchers from `root/*` → distribution root; makes `.sh` executable; removes `efaDRV.*`
    - Selected resources under `de/**` (images, css, xml, xsl, properties)
- Maven currently does not copy any of these directories into an assembled distro, nor does it package a separate `efahelp.jar`.

4) Generated artifacts/steps
- `tools.SecFileCreator` is executed to produce `efa.sec` using the freshly built classes and classpath.
- Maven does not run this step; no `efa.sec` is produced.

5) Exact versions of non‑Central or special jars
- MakeDist includes jars from `plugins/*`:
    - `edtftpj.jar`, `jsch-0.1.55.jar`, `jh.jar`, `javax.mail.jar`, `activation.jar`, `flatlaf-*.jar`, `json-*.jar`, `commons-codec.jar`, `signpost-core.jar`, PDF stack (batik/fop/commons) in some contexts.
- Maven maps most to Central but two are still `system` scoped: `edtftpj.jar`, `jsuntimes.jar`. There’s no automated way (yet) to resolve them from a repository.

6) Versioned distribution naming
- MakeDist names archives `efa<version>.zip` and allows an optional `versionid` copy to `versions/<versionid>/`.
- Maven currently uses `0.0.1-SNAPSHOT` and does not mirror this naming/optional copy.

7) Source backup zip
- MakeDist zips the entire working tree into `Backup/efasrc_<timestamp>.zip` before building.
- Maven does not produce this backup.

8) Post‑build cleanup and normalization
- MakeDist removes temporary files, strips `.class` from staging, touches files, etc. Not strictly required with Maven but results in a tidy distro tree.

### A practical way forward with Maven
You can close the gap incrementally with a few standard plugins. Here’s a blueprint.

1) [DONE]  Create a distribution assembly (ZIP/TAR) with the right layout
- Use `maven-assembly-plugin` with a descriptor that mirrors `makedist/` structure. Example descriptor `src/assembly/efa.xml`:
```xml
<assembly xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.3 https://maven.apache.org/xsd/assembly-1.1.3.xsd">
  <id>dist</id>
  <formats>
    <format>zip</format>
    <format>tar</format>
  </formats>
  <includeBaseDirectory>false</includeBaseDirectory>

  <!-- program/efa.jar -->
  <files>
    <file>
      <source>${project.build.directory}/${project.artifactId}-${project.version}.jar</source>
      <outputDirectory>program</outputDirectory>
      <destName>efa.jar</destName>
    </file>
  </files>

  <!-- program/plugins: all runtime deps as loose jars -->
  <dependencySets>
    <dependencySet>
      <outputDirectory>program/plugins</outputDirectory>
      <unpack>false</unpack>
      <scope>runtime</scope>
      <useProjectArtifact>false</useProjectArtifact>
      <useTransitiveDependencies>true</useTransitiveDependencies>
    </dependencySet>
  </dependencySets>

  <!-- cfg, doc, help, root scripts, and selected resources -->
  <fileSets>
    <fileSet>
      <directory>${project.basedir}/cfg</directory>
      <outputDirectory>cfg</outputDirectory>
      <includes><include>*.cfg</include></includes>
    </fileSet>
    <fileSet>
      <directory>${project.basedir}/help/main</directory>
      <outputDirectory>doc</outputDirectory>
      <includes>
        <include>*.gif</include>
        <include>*.png</include>
        <include>*.html</include>
      </includes>
    </fileSet>
    <fileSet>
      <directory>${project.basedir}/help</directory>
      <outputDirectory>program/help</outputDirectory>
    </fileSet>
    <fileSet>
      <directory>${project.basedir}/root</directory>
      <outputDirectory>.</outputDirectory>
      <fileMode>0755</fileMode>
    </fileSet>
  </fileSets>
</assembly>
```
- Then wire the plugin:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-assembly-plugin</artifactId>
  <version>3.7.1</version>
  <configuration>
    <descriptors>
      <descriptor>src/assembly/efa.xml</descriptor>
    </descriptors>
    <finalName>efa${project.version}</finalName>
  </configuration>
  <executions>
    <execution>
      <id>make-dist</id>
      <phase>package</phase>
      <goals><goal>single</goal></goals>
    </execution>
  </executions>
</plugin>
```
- Result: `target/efa${project.version}.zip` and `.tar` containing `program/`, `program/plugins/`, `cfg/`, `doc/`, `help/`, root scripts, etc.

2) Generate `efahelp.jar` (optional, to match MakeDist)
- If you still require a separate `efahelp.jar` in `program/`, add a secondary `maven-jar-plugin` execution that jars `help/**`:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-jar-plugin</artifactId>
  <executions>
    <execution>
      <id>help-jar</id>
      <phase>prepare-package</phase>
      <goals><goal>jar</goal></goals>
      <configuration>
        <classesDirectory>${project.basedir}/help</classesDirectory>
        <finalName>efahelp</finalName>
        <archive><manifest><addDefaultImplementationEntries>false</addDefaultImplementationEntries></manifest></archive>
      </configuration>
    </execution>
  </executions>
</plugin>
```
- Then include `target/efahelp.jar` into the assembly (another `<file>` entry with `outputDirectory>program</outputDirectory>`).

3) [DONE] Run `tools.SecFileCreator` during the build
- Use `exec-maven-plugin` after the main jar is built so the classpath is available:
```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.5.0</version>
  <executions>
    <execution>
      <id>generate-efa-sec</id>
      <phase>prepare-package</phase>
      <goals><goal>java</goal></goals>
      <configuration>
        <mainClass>tools.SecFileCreator</mainClass>
        <classpathScope>runtime</classpathScope>
        <arguments>
          <argument>${project.build.directory}</argument>
          <argument>${project.build.directory}/${project.artifactId}-${project.version}.jar</argument>
        </arguments>
      </configuration>
    </execution>
  </executions>
</plugin>
```
- Then add a `<file>` in the assembly to place the generated `efa.sec` into `program/`.

4) Replace `system` dependencies
- `edtftpj`: switch to Central: `com.enterprisedt:edtftpj` at a compatible version.
- `jsuntimes.jar`: either
    - import sources as a module, or
    - publish it to an internal repo (GitHub Packages) and depend on it normally, or
    - replace it with a maintained library.
- Avoid `system` scope in CI; it complicates caching and portability.

5) Optional: bundle dependencies vs. external folder
- Current MakeDist keeps dependencies as separate jars in `program/plugins`. That is compatible with your launch scripts. If you prefer a single runnable JAR, use `maven-shade-plugin` instead of copying dependencies; update launch scripts accordingly.

6) Versioning and naming
- Use Maven’s `project.version` to produce `efa${project.version}.zip`.
- To mimic the extra `versionid` copy, you can implement a profile or a small `maven-antrun-plugin`/`build-helper-maven-plugin` step that duplicates the zip into `versions/${versionid}/` when `-Dversionid=...` is provided.

7) Source backup (optional)
- If you still want a source zip: `maven-source-plugin` already produces `-sources.jar`. If you want a raw tree zip, add another `assembly` with a simple fileset of `${project.basedir}` filtered to exclude `target/` and VCS meta.

### Suggested GitHub Actions CI pipeline
Below is a minimal workflow that builds on Linux with Temurin JDK 8, produces the distribution ZIP/TAR, and uploads them as artifacts. Adjust names to your repo.

```yaml
name: CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  workflow_dispatch: {}

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 8
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '8'
          cache: 'maven'

      - name: Build with Maven
        run: mvn -B -DskipTests package

      - name: Upload distribution artifacts
        uses: actions/upload-artifact@v4
        with:
          name: efa-dist
          path: |
            target/efa*.zip
            target/efa*.tar

  # Optional: create a GitHub Release when a tag is pushed like v2.4.1_19
  release:
    if: startsWith(github.ref, 'refs/tags/')
    runs-on: ubuntu-latest
    needs: build
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '8'
          cache: 'maven'
      - run: mvn -B -DskipTests package
      - name: Upload to GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          files: |
            target/efa*.zip
            target/efa*.tar
```

If you keep any `system`-scoped jars for now, ensure the paths exist in the repo so CI can read them. Longer term, replace them or publish them to a repository (e.g., GitHub Packages) and reference them normally.

### Recommended next steps
- Decide whether you want a folder-based runtime layout (plugins next to app) or a shaded fat jar. Match your launch scripts accordingly.
- Add an `assembly` descriptor and the `exec-maven-plugin` step for `SecFileCreator` so the Maven build becomes the single source of truth for releases.
- Replace `system` dependencies. If `jsuntimes` is custom, consider importing its sources into your repo as a module.
- Once the Maven build outputs match `makeDist.sh`, retire the script and let CI publish signed artifacts or GitHub Releases based on tags.

If you want, I can prepare the exact `pom.xml` snippets and an `efa.xml` assembly descriptor tailored to your current tree so `mvn package` reproduces the `makeDist.sh` result byte-for-byte.

### Auto-update via Getdown (GitHub Releases)
This project now supports auto-updates using the Getdown launcher while keeping the existing ZIP distribution for first-time installs.

How it works
- The ZIP now prefers launching via Getdown if `program/getdown.jar` and `program/getdown.txt` are present. If not, it falls back to the legacy script behavior.
- Getdown reads `program/getdown.txt` and looks for updates at:
  `https://github.com/<owner>/<repo>/releases/latest/download/`
- Stable assets uploaded on each release:
  - `efa.jar` (the shaded application jar)
  - `getdown.txt` (launcher configuration)
  - `digest.txt` (hashes file generated by Getdown Digester)
  - `version.txt` (plain `${project.version}`)
- The app directory used by Getdown is `program/` (inside the ZIP and local install). Asset names are flat to match GitHub Releases.

Launcher configuration (`getdown.txt`)
```
appbase = https://github.com/<owner>/<repo>/releases/latest/download/
allow_offline = true
ui.name = efa
main = de.nmichael.efa.base.Main
jvmarg = -Xmx192m
jvmarg = -XX:NewSize=32m
jvmarg = -XX:MaxNewSize=32m
code = efa.jar
```

Build and release pipeline
- Local `mvn package` now also prepares all Getdown artifacts so you can test updates without CI:
  - Produces `target/update-site/{efa.jar,getdown.txt,version.txt,digest.txt}`
  - Copies the Getdown launcher to `target/program/getdown.jar`
  - Copies `getdown.txt` into `target/program/getdown.txt`
  - The assembled ZIP includes `program/getdown.jar` and `program/getdown.txt`
- The GitHub Release workflow only builds and uploads the Maven-produced artifacts; it no longer recreates Getdown files in CI.

Run scripts changes (non-breaking)
- `root/runefa.sh` now tries: `exec java -jar program/getdown.jar program "$@"` if Getdown files exist; otherwise executes the original logic.
- `root/runefa.bat` now tries: `START /B javaw -jar program\getdown.jar program %*` if Getdown files exist; otherwise runs original logic.
- Default JVM args are duplicated in `getdown.txt` for consistent cross-platform behavior.

Verifying locally
1. Build: `./mvnw -B -ntp package`
2. Unzip `target/efa<version>.zip`, cd to its root.
3. If `program/getdown.jar` and `program/getdown.txt` are not present yet (only after CI release), you can add them manually:
   - Download `getdown-1.8.6.jar` and copy to `program/getdown.jar`.
   - Create `program/getdown.txt` using the config above, but set `appbase` to a local server.
4. Serve an update-site from another folder:
   - Create folder `site/` with files: `efa.jar`, `getdown.txt`, `version.txt` (matching your current version), then run Digester to create `digest.txt`.
   - Start server: `cd site && python3 -m http.server 8000`
   - Point `appbase = http://127.0.0.1:8000/` in your local `program/getdown.txt`.
5. Start the app via `root/runefa.sh` or `runefa.bat`; you should see the Getdown window briefly.
6. To simulate an update: modify the code or bump version, rebuild jar, replace `site/efa.jar`, run Digester again, refresh app.

Security notes
- This setup relies on HTTPS from GitHub. If you later need stronger integrity or a private backend, consider enabling Getdown signing and signed digests.

Optional channels (stub)
- Future channels (e.g., `beta`) can be introduced by changing `appbase` to a different release path or asset set. The CI can be parameterized to publish channel-specific assets with the same filenames under separate releases or tags. For now, only `latest` is used.
