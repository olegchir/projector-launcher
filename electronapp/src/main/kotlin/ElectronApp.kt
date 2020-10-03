import Electron.BrowserWindow
import Electron.BrowserWindowConstructorOptions
import Electron.App
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

//suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
//    then({ cont.resume(it) }, { cont.resumeWithException(it) })
//}

class ElectronApp(val url: String) {
    var app: App = Electron.app
    lateinit var mainWindow: BrowserWindow
    lateinit var mainWindowUrl: String
    lateinit var mainWindowPrevUrl: String
    lateinit var mainWindowNextUrl: String

    suspend fun navigateMainWindow(url: String) {
        this.mainWindowNextUrl = url
        this.mainWindow.loadURL(url).await()
        this.mainWindowPrevUrl = this.mainWindowUrl
        this.mainWindowUrl = url
    }

    suspend fun registerApplicationLevelEvents() {
        app.whenReady().then {
            val mainWindow = BrowserWindow(object : BrowserWindowConstructorOptions {})
            mainWindow.loadURL(url)
        }
    }

    open fun start() {
        GlobalScope.launch(block = {
            registerApplicationLevelEvents()
        });
    }
}
