package com.daniebeler.pfpixelix.domain.repository.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object TagNameSerializer : KSerializer<String> {
    override val descriptor =
        PrimitiveSerialDescriptor("com.daniebeler.TagNameSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: String) = encoder.encodeString("#$value")
    override fun deserialize(decoder: Decoder) = decoder.decodeString().removePrefix("#")
}