import kotlinx.cinterop.*
import kotlin.experimental.ExperimentalNativeApi

@ExperimentalNativeApi
@ExperimentalForeignApi
internal inline fun <R> runCatching(
    type: CPointer<CArrayPointerVar<ByteVar>>?,
    message: CPointer<CArrayPointerVar<ByteVar>>?,
    stacktrace: CPointer<CArrayPointerVar<CArrayPointerVar<ByteVar>>>?,
    stacktraceSize: CPointer<IntVar>?,
    block: () -> R
) = runCatching(block).onFailure { e ->
    if (type != null && type.rawValue != nativeNullPtr) {
        type.pointed.value = (e::class.qualifiedName ?: "Unknown exception").cstrPtr
    }

    if (message != null) {
        message.pointed.value = (e.message ?: "No message").cstrPtr
    }

    if (stacktrace != null && stacktraceSize != null) {
        val stacktraceList = mutableListOf<ByteVar>()
        e.getStackTrace().forEach {
            stacktraceList.add(it.cstrPtr.pointed)
        }
        val stacktraceArray = nativeHeap.allocArrayOfPointersTo(stacktraceList)
        stacktrace.pointed.value = stacktraceArray
        stacktraceSize.pointed.value = stacktraceList.size
    }
}
