package com.cuvv.vratim

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

const val PAYLOAD_SIZE = 2

@Serializable
data class TransportPacket(
    val version: Int,

    @Serializable(with = ByteArrayAsIntListSerializer::class)
    val payload: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        other as TransportPacket
        return version == other.version && payload.contentEquals(other.payload)
    }

    override fun hashCode(): Int {
        var result = version
        result = 31 * result + payload.contentHashCode()
        return result
    }

    companion object {
        private const val PROTOCOL_VERSION = 1;

        fun fromEntity(entityId: Int, status: Int): TransportPacket {
            val payload = ByteArray(PAYLOAD_SIZE)

            payload[0] = entityId.toByte()
            payload[1] = status.toByte()

            return TransportPacket(
                version = PROTOCOL_VERSION,
                payload = payload
            )
        }
    }
}

object ByteArrayAsIntListSerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ByteArrayAsIntList") {
            element<Int>("0")
            element<Int>("1")
        }

    override fun serialize(encoder: Encoder, value: ByteArray) {
        val composite = encoder.beginCollection(descriptor, value.size)
        for (i in value.indices) {
            val u8 = value[i].toInt() and 0xFF
            composite.encodeIntElement(descriptor, i, u8)
        }

        composite.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        val composite = decoder.beginStructure(descriptor)
        val result = ByteArray(PAYLOAD_SIZE)

        while (true) {
            when (val index = composite.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break
                0, 1 -> {
                    val u8 = composite.decodeIntElement(descriptor, index)
                    result[index] = u8.toByte()
                }
                else -> composite.decodeIntElement(descriptor, index)
            }
        }

        composite.endStructure(descriptor)
        return result
    }
}
