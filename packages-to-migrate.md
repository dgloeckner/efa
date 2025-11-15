### Summary
I inspected the `plugins/` directory and mapped each bundled JAR to the most likely Maven Central coordinates (or noted when none are known). Where legacy/renamed Jakarta artifacts exist, I’ve included both legacy and current options.

### Mapping: plugins JAR → Maven coordinates and notes
- plugins/flatlaf/flatlaf-3.6.jar
    - Maven: `com.formdev:flatlaf:3.6`
    - Status: Available on Maven Central (actively maintained).

- plugins/ftp/edtftpj.jar
    - Maven: `com.enterprisedt:edtftpj` (versions 2.x exist on Central)
    - Status: Available on Maven Central. Exact version must match the binary you have; if unknown, consider updating to a current 2.x.

- plugins/ftp/jsch-0.1.55.jar
    - Maven: `com.jcraft:jsch:0.1.55`
    - Status: Available on Maven Central. Note that JSCH 0.1.x is legacy; community-maintained forks (e.g., `com.github.mwiede:jsch`) exist with updates.

- plugins/help/jh.jar (JavaHelp runtime)
    - Maven: `javax.help:javahelp:2.0.05` (also seen as `org.jdesktop:javahelp:2.0.05`)
    - Status: Available on Maven Central but long unmaintained. Suitable if you still need JavaHelp at runtime.

- plugins/json/json-20250517.jar
    - Maven: `org.json:json:20250517` (JSON-Java uses date-based versions)
    - Status: Available on Maven Central. Replace custom-dated JAR with the matching Central version to standardize.

- plugins/jsuntimes/jsuntimes.jar
    - Maven: No well-known Central artifact with this exact name. Likely a project-local or third-party JAR not published to Maven Central.
    - Notes: If it’s custom code, consider turning it into a module or publish it to an internal repository (or replace with a maintained library like `com.luckycatlabs:SunriseSunsetCalculator`).

- plugins/mail/javax.mail.jar (JavaMail)
    - Legacy Maven: `com.sun.mail:javax.mail:1.6.2` (last javax.* line)
    - Jakarta (current API): `jakarta.mail:jakarta.mail-api:2.x` (API) plus an implementation if needed
    - Status: Available on Maven Central. Choose legacy vs Jakarta depending on your package usage (`javax.mail.*` vs `jakarta.mail.*`).

- plugins/mail/activation.jar (JAF/Activation)
    - Legacy Maven: `javax.activation:activation:1.1.1` (very old)
    - Newer (javax): `com.sun.activation:javax.activation:1.2.0`
    - Jakarta API: `jakarta.activation:jakarta.activation-api:1.2.2` (API) + implementation if required
    - Status: Available on Maven Central. Match to your `javax.*` vs `jakarta.*` usage.

- plugins/pdf/avalon-framework.jar
    - Maven: `avalon-framework:avalon-framework:4.1.5` (sometimes `org.apache.avalon:avalon-framework:4.1.5`)
    - Status: Available but EOL/legacy; required by old FOP/Batik stacks.

- plugins/pdf/batik-all.jar
    - Maven: `org.apache.xmlgraphics:batik-all:1.17` (latest line; older 1.14–1.16 also common)
    - Status: Available on Maven Central.

- plugins/pdf/commons-io.jar
    - Maven: `commons-io:commons-io` (e.g., `2.16.1` or `2.17.0`)
    - Status: Available on Maven Central.

- plugins/pdf/commons-logging.jar
    - Maven: `commons-logging:commons-logging` (e.g., `1.3.4`)
    - Status: Available on Maven Central.

- plugins/pdf/fop.jar (Apache FOP)
    - Maven: `org.apache.xmlgraphics:fop` (e.g., `2.9` current stable line)
    - Status: Available on Maven Central. FOP pulls `xmlgraphics-commons` and Batik dependencies transitively when declared via Maven/Gradle.

- plugins/pdf/xmlgraphics-commons.jar
    - Maven: `org.apache.xmlgraphics:xmlgraphics-commons` (e.g., `2.9`)
    - Status: Available on Maven Central.

- plugins/weather/commons-codec.jar
    - Maven: `commons-codec:commons-codec` (e.g., `1.17.1`)
    - Status: Available on Maven Central.

- plugins/weather/signpost-core.jar
    - Maven: `oauth.signpost:signpost-core:1.2.1.2`
    - Status: Available on Maven Central but legacy. If HTTP client stack modernizes, consider alternative OAuth libs.

### Extra notes and recommendations
- Version alignment: The exact versions in `plugins/` are mostly unknown (jars are versionless except a few). If you plan to migrate to Maven/Gradle, use the exact legacy versions first to avoid behavioral changes; then upgrade one library at a time, running regression checks (especially for the FOP/Batik/Commons stack).
- javax → jakarta transitions: If your code uses `javax.mail.*` / `javax.activation.*`, stick to the last `javax.*` artifacts. Moving to `jakarta.*` requires code/package updates and possibly newer Java baselines.
- JavaHelp (`jh.jar`): still obtainable from Central but long unmaintained. Since you removed help indexing from the build, this jar is only for runtime display; keep it only if those features are still needed.
- `jsuntimes.jar`: If this is a custom or third-party non-Maven JAR, consider:
    - Extracting source and incorporating it as a module; or
    - Publishing it to an internal Maven repository; or
    - Replacing with a maintained library.

If you want, I can produce a `pom.xml`/Gradle snippet that declares these dependencies with conservative (legacy) versions matching your current stack, or propose a modernized set with current versions and notes on likely code changes.