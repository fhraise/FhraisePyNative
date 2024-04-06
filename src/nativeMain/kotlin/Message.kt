import kotlinx.serialization.Serializable

@Serializable
internal data class Message(val action: Action, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as Message

        if (action != other.action) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
