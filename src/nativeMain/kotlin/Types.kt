import kotlinx.cinterop.*

@ExperimentalForeignApi
internal val String.cstrPtr: CPointer<ByteVar>
    get() {
        val cstr = cstr
        val ptr = nativeHeap.allocArray<ByteVar>(cstr.size)
        cstr.place(ptr)
        return ptr
    }
