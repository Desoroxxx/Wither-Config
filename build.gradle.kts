import org.jetbrains.gradle.ext.settings
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.runConfigurations

plugins {
	id("com.gtnewhorizons.retrofuturagradle") version "1.4.0"
	id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.9"
	id("com.github.gmazzo.buildconfig") version "5.5.1"
	id("io.freefair.lombok") version "8.11"
}

group = "dev.redstudio"
version = "1.2.1" // Versioning must follow Ragnarök versioning convention: https://github.com/Red-Studio-Ragnarok/Commons/blob/main/Ragnar%C3%B6k%20Versioning%20Convention.md

val realName = "Wither Config"
val id = "witherconfig"
val plugin = "asm.WitherConfigPlugin"

minecraft {
	mcVersion = "1.12.2"
	username = "Desoroxxx"
	extraRunJvmArguments = listOf("-Dforge.logging.console.level=debug", "-Dfml.coreMods.load=${project.group}.${id}.${plugin}", "-Dmixin.hotSwap=true", "-Dmixin.checks.mixininterfaces=true", "-Dmixin.debug.export=true")
}

repositories {
	maven {
		name = "Cleanroom"
		url = uri("https://maven.cleanroommc.com")
	}

	maven {
		name = "SpongePowered"
		url = uri("https://repo.spongepowered.org/maven")
	}
}

dependencies {
	annotationProcessor("org.ow2.asm", "asm-debug-all", "5.2")
	annotationProcessor("com.google.guava", "guava", "32.1.2-jre")
	annotationProcessor("com.google.code.gson", "gson", "2.8.9")

	val mixinBooter: String = modUtils.enableMixins("zone.rong:mixinbooter:10.2", "mixins.${id}.refmap.json") as String
	api(mixinBooter) {
		isTransitive = false
	}
	annotationProcessor(mixinBooter) {
		isTransitive = false
	}
}

buildConfig {
	packageName("${project.group}.${id}")
	className("ProjectConstants")
	documentation.set("This class defines constants for ${realName}.\n<p>\nThey are automatically updated by Gradle")

	useJavaOutput()
	buildConfigField("String", "ID", provider { """"${id}"""" })
	buildConfigField("String", "NAME", provider { """"${realName}"""" })
	buildConfigField("String", "VERSION", provider { """"${project.version}"""" })
	buildConfigField("org.apache.logging.log4j.Logger", "LOGGER", "org.apache.logging.log4j.LogManager.getLogger(NAME)")
}


idea {
	module {
		inheritOutputDirs = true

		excludeDirs = setOf(
			file(".github"), file(".gradle"), file(".idea"), file("build"), file("gradle"), file("run")
		)
	}

	project {
		settings {
			jdkName = "1.8"
			languageLevel = IdeaLanguageLevel("JDK_1_8")

			runConfigurations {
				create("Client", Gradle::class.java) {
					taskNames = setOf("runClient")
				}
				create("Server", Gradle::class.java) {
					taskNames = setOf("runServer")
				}
				create("Obfuscated Client", Gradle::class.java) {
					taskNames = setOf("runObfClient")
				}
				create("Obfuscated Server", Gradle::class.java) {
					taskNames = setOf("runObfServer")
				}
				create("Vanilla Client", Gradle::class.java) {
					taskNames = setOf("runVanillaClient")
				}
				create("Vanilla Server", Gradle::class.java) {
					taskNames = setOf("runVanillaServer")
				}
			}
		}
	}
}

// Set the toolchain version to decouple the Java we run Gradle with from the Java used to compile and run the mod
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(8))
		vendor.set(JvmVendorSpec.ADOPTIUM)
	}
	withSourcesJar() // Generate sources jar when building and publishing
}

tasks.processResources.configure {
	inputs.property("version", project.version)
	inputs.property("id", id)

	filesMatching("mcmod.info") {
		expand(mapOf("version" to project.version, "id" to id))
	}
}

tasks.named<Jar>("jar") {
	manifest {
		attributes(
			"ModSide" to "BOTH",
			"FMLCorePlugin" to "${project.group}.${id}.${plugin}",
			"FMLCorePluginContainsFMLMod" to "true",
			"ForceLoadAsMod" to "true"
		)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	options.isFork = true
	options.forkOptions.jvmArgs = listOf("-Xmx4G")
}
