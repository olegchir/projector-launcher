import Electron.*
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlin.js.json

//import kotlin.coroutines.CoroutineContext
//suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
//    then({ cont.resume(it) }, { cont.resumeWithException(it) })
//}

external fun encodeURI(data: String): String

class ElectronApp(val url: String) {
    var that = this

    var path = require("path")
    var node_url = require("url")
    var node_fs = require("fs")

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
                get() = object : WebPreferences {
                    override var nodeIntegration: Boolean?
                        get() = true
                        set(value) {}
                    override var webSecurity: Boolean?
                        get() = false
                        set(value) {}
                }
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

        this.mainWindow.webContents.on("did-fail-load", didFailLoadListener = { event: Event, errorCode: Number, errorDescription: String, validatedURL: String, isMainFrame: Boolean, frameProcessId: Number, frameRoutingId: Number ->
            GlobalScope.launch(block = {
                if (!that.initialized) {
                    if (!validatedURL.isNullOrBlank()) {
                        dialog.showMessageBoxSync(that.mainWindow, object : MessageBoxSyncOptions {
                            override var message: String
                                get() = "Can't load the URL"
                                set(value) {}
                            override var detail: String?
                                get() = validatedURL
                                set(value) {}
                            override var type: String?
                                get() = "error"
                                set(value) {}
                            override var title: String?
                                get() = "Invalid URL"
                                set(value) {}
                        })
                    }

                    console.log("Can't load the URL: $validatedURL")
                    that.mainWindow.loadFile("openurl.html").await()
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

    fun loadMessage(message: String) {
        GlobalScope.launch(block = {
            that.mainWindow.loadURL("about:blank").await()
            var html = "<body><h1>Invalid URL</h1><p>$message</p></body>"
            that.mainWindow.loadURL("data:text/html;charset=utf-8," + encodeURI(html)).await()
        })
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

    fun serveStaticFiles() {
        // https://stackoverflow.com/questions/38204774/serving-static-files-in-electron-react-app
        protocol.interceptFileProtocol("file", handler = { request, callback ->
            var url = request.url.substring(7)    /* all urls start with 'file://' */
            var normalUrl = if (node_fs.existsSync(url)) url else path.normalize("${__dirname}/${url}")
            callback(normalUrl)
            //console.log("Intercepted url was: $url")
            //console.log("Intercepted url transformed to: $normalUrl")
        })
    }

    fun registerApplicationLevelEvents() {
        app.whenReady().then {
            this.serveStaticFiles()
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
