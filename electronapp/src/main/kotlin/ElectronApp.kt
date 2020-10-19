import Electron.*
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

//import kotlin.coroutines.CoroutineContext
//suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
//    then({ cont.resume(it) }, { cont.resumeWithException(it) })
//}

external fun encodeURI(data: String): String

class ElectronApp(val url: String) {
    var that = this
    var app: App = Electron.app
    lateinit var mainWindow: BrowserWindow
    lateinit var mainWindowUrl: String
    lateinit var mainWindowPrevUrl: String
    lateinit var mainWindowNextUrl: String
    var initialized = false;

    fun navigateMainWindow(url: String) {
        GlobalScope.launch(block = {
            that.mainWindowNextUrl = url
            try {
                that.mainWindow.loadURL(url).await()
                that.mainWindowPrevUrl = that.mainWindowUrl
                that.mainWindowUrl = url
            } catch (e: dynamic) {

            }
        })
    }

    fun createWindow() {
        val workAreaSize = screen.getPrimaryDisplay().workAreaSize

        this.mainWindow = BrowserWindow(object : BrowserWindowConstructorOptions {
            override var width: Number?
                get() = workAreaSize.width
                set(value) {}
            override var height: Number?
                get() = workAreaSize.height
                set(value) {}
            override var webPreferences: WebPreferences?
                get() = jsObject { "nodeIntegration" to true }
                set(value) {}
        })

        this.mainWindow.webContents.on("did-navigate-in-page")
        { event: Event,
          url: String,
          isMainFrame: Boolean,
          frameProcessId: Number,
          frameRoutingId: Number ->

            initialized = true;
            if (isMainFrame) {
                Logger.debug("Navigation: go to $url")
            }
        }

        this.mainWindow.webContents.on("did-fail-load", genericListener = {
            GlobalScope.launch(block = {
                if (!that.initialized) {
                    console.log("Loading failed")
                    that.mainWindow.loadURL("about:blank").await()
                    var html = "<body><h1>Invalid URL</h1><p>We’re having trouble finding Projector. Try using different URL.</p></body>"
                    that.mainWindow.loadURL("data:text/html;charset=utf-8," + encodeURI(html)).await()
                    that.mainWindow.close()
                } else {
                    Logger.direct("Loading failed, fallback activated: ${that.mainWindowNextUrl} -> ${that.mainWindowUrl}");
                    that.navigateMainWindow(that.mainWindowUrl)
                }
            })
        })

        this.mainWindow.on("closed", genericListener = {
            this.app.quit()
        });

        this.navigateMainWindow(url);
    }

    fun registerGlobalShortcuts() {
        ElectronUtil.disableAllStandardShortcuts();

        ElectronUtil.registerGlobalShortcut(app, "Alt+F4") {
            this.quitApp();
        };

        ElectronUtil.registerGlobalShortcut(app, "Cmd+Q") {
            this.quitApp();
        };
    }

    fun registerApplicationLevelEvents() {
        app.whenReady().then {
            this.createWindow()
            this.registerGlobalShortcuts()
        }

        app.on("web-contents-created", listener = { e: Event, contents: WebContents ->
            contents.on("new-window", listener = { e: Event, url: String ->
                e.preventDefault();
                require("open")(url);
            })


            contents.on("will-navigate", navigationListener = { e: Event, url: String ->
                if (url !== contents.getURL()) {
                    e.preventDefault()
                    require("open")(url);
                }
            })
        })
    }

    fun quitApp() {
        app.exit(0)
    }

    open fun start() {
        registerApplicationLevelEvents()
    }
}
