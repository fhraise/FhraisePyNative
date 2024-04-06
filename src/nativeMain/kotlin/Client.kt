import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.cbor.*
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.ExperimentalNativeApi

class Client(private val host: String, private val port: UShort) {
    private val scope = MainScope()
    private val channel = Channel<Message>(3)

    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient {
        install(Resources)

        install(ContentNegotiation) {
            cbor()
        }

        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Cbor)
        }
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
    fun connect(
        throwableType: CPointer<CArrayPointerVar<ByteVar>>?,
        throwableMessage: CPointer<CArrayPointerVar<ByteVar>>?,
        throwableStacktrace: CPointer<CArrayPointerVar<CArrayPointerVar<ByteVar>>>?,
        throwableStacktraceSize: CPointer<IntVar>?
    ) = runBlocking {
        suspendCoroutine {
            scope.launch(Dispatchers.IO) {
                it.resume(runCatching(throwableType, throwableMessage, throwableStacktrace, throwableStacktraceSize) {
                    client.webSocket(host = host, port = port.toInt(), path = WS_PATH) {
                        while (true) {
                            channel.send(receiveDeserialized<Message>())
                        }
                    }
                }.isSuccess)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun receive(
        action: CPointer<CArrayPointerVar<ByteVar>>,
        data: CPointer<CArrayPointerVar<ByteVar>>,
        size: CPointer<IntVar>,
        getResult: CPointer<CFunction<() -> CPointer<*>>>
    ) {
        val message = runBlocking { channel.receive() }
        action.pointed.value = message.action.name.cstrPtr
        data.pointed.value = nativeHeap.allocArrayOf(message.data)
        size.pointed.value = message.data.size

        val result = getResult().asStableRef<Result>().get()

        if (result.action != message.action) {

        }
    }
}
