package com.daniebeler.pfpixelix

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class TranslationParameterTest {

    private val formatParamRegex = Regex("""%(?:\d+\$)?[-+0-9#]*[a-zA-Z]""")
    private val stringItemRegex = Regex("""<string name="([^"]+)">([\s\S]*?)</string>""")

    @Test
    fun verifyTranslationFormatParametersMatchBaseLanguage() {
        val composeResourcesDir = File("src/commonMain/composeResources")
        val baseFile = File(composeResourcesDir, "values/strings.xml")

        assertTrue(
            baseFile.exists(),
            "Base string resource file not found at ${baseFile.path}"
        )

        // Map of string key -> list of format parameters in order (e.g., "welcome_message" -> ["%1$s", "%2$d"])
        val baseParameters = extractFormatParameters(baseFile.readText())

        // Find all translated string files (values-fi, values-de, etc.)
        val translationFiles = composeResourcesDir.walkTopDown()
            .filter { it.isFile && it.name.endsWith(".xml") && it.parentFile.name.startsWith("values-") }
            .toList()

        val parameterMismatches = mutableListOf<String>()

        for (translationFile in translationFiles) {
            val locale = translationFile.parentFile.name
            val translatedParameters = extractFormatParameters(translationFile.readText())

            for ((key, expectedParams) in baseParameters) {
                if (expectedParams.isEmpty()) continue

                val actualParams = translatedParameters[key] ?: continue // Skip if string isn't translated yet

                if (expectedParams != actualParams) {
                    parameterMismatches.add(
                        "[$locale] String '$key' parameter mismatch:\n" +
                                "  Expected (base): $expectedParams\n" +
                                "  Actual ($locale): $actualParams"
                    )
                }
            }
        }

        assertTrue(
            parameterMismatches.isEmpty(),
            "Found format parameter mismatches in translations that will cause String.format() crashes:\n\n" +
                    parameterMismatches.joinToString("\n\n")
        )
    }

    private fun extractFormatParameters(xmlContent: String): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()

        for (match in stringItemRegex.findAll(xmlContent)) {
            val key = match.groupValues[1]
            val value = match.groupValues[2]

            val params = formatParamRegex.findAll(value)
                .map { it.value }
                .toList()

            result[key] = params
        }

        return result
    }
}