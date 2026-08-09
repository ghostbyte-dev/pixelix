package com.daniebeler.pfpixelix.domain.repository.serializers

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.TextNode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object HtmlAsTextSerializer : KSerializer<String> {
    override val descriptor = PrimitiveSerialDescriptor("com.daniebeler.HtmlAsTextSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: String) = encoder.encodeString(value)
    override fun deserialize(decoder: Decoder): String {
        val html = decoder.decodeString()

        val withoutDoubleBreaks = html.replace("<br />\n", "\n")
        val document = Ksoup.parse(withoutDoubleBreaks)
        document.outputSettings(Document.OutputSettings().prettyPrint(false))

        document.select("br").forEach { it.replaceWith(TextNode("\n")) }

        val text = document.wholeText()

        return text
            .lines()
            .joinToString("\n") { it.trimStart() }
            .trim()
    }
}

