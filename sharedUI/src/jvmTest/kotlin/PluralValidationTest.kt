package com.daniebeler.pfpixelix

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class PluralValidationTest {

    @Test
    fun verifyAllPluralsContainOtherQuantity() {
        // Relative path from sharedUI module directory to composeResources
        val composeResourcesDir = File("src/commonMain/composeResources")

        // Recursively fetch all .xml files in values, values-fi, etc.
        val xmlFiles = composeResourcesDir.walkTopDown()
            .filter { it.isFile && it.name.endsWith(".xml") }
            .toList()

        assertTrue(xmlFiles.isNotEmpty(), "No string resource files found in composeResources!")

        val pluralsRegex = Regex("""<plurals name="([^"]+)">([\s\S]*?)</plurals>""")
        val otherQuantityRegex = Regex("""<item quantity="other">""")

        val missingOtherPlurals = mutableListOf<String>()

        for (file in xmlFiles) {
            val content = file.readText()
            val matches = pluralsRegex.findAll(content)

            for (match in matches) {
                val pluralName = match.groupValues[1]
                val pluralContent = match.groupValues[2]

                if (!otherQuantityRegex.containsMatchIn(pluralContent)) {
                    // Include relative path to make tracking down errors easy
                    val relativePath = file.path.substringAfter("composeResources/")
                    missingOtherPlurals.add("$relativePath -> <plurals name=\"$pluralName\">")
                }
            }
        }

        assertTrue(
            missingOtherPlurals.isEmpty(),
            "The following plurals are missing quantity='other' and will cause runtime crashes:\n" +
                    missingOtherPlurals.joinToString("\n")
        )
    }
}