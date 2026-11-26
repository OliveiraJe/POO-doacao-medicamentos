plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.54.1.0")
}

javafx {
    version = "21.0.2"
    modules = listOf("javafx.controls")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "com.goytacazes.poodoacao.App"
}

/**
 * Gera um JAR único executável (classes do projeto + SQLite + JavaFX, incluindo
 * as bibliotecas nativas da plataforma atual).
 *
 * Uso: ./gradlew fatJar  ->  app/build/libs/POODoacao-all.jar
 * Execução: java -jar app/build/libs/POODoacao-all.jar
 */
tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Cria um JAR executável com todas as dependências embutidas."
    archiveBaseName = "POODoacao"
    archiveClassifier = "all"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Main-Class" to "com.goytacazes.poodoacao.Launcher",
            "Implementation-Title" to "Doacao de Medicamentos",
            "Implementation-Version" to project.version.toString()
        )
    }

    from(sourceSets.main.get().output)

    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    }) {
        exclude(
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA",
            "module-info.class",
            "META-INF/versions/*/module-info.class"
        )
    }
}

/**
 * Copia o JAR executável para <raiz do projeto>/build/, que é o diretório de
 * saída "de vitrine" do projeto.
 */
tasks.register<Copy>("distJar") {
    group = "build"
    description = "Copia o JAR executável para build/ na raiz do projeto."
    from(tasks.named("fatJar"))
    into(rootProject.layout.buildDirectory)
}
