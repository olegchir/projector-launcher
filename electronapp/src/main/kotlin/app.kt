import Electron.BrowserWindow
import Electron.BrowserWindowConstructorOptions
import Electron.app

external fun require(module: String): dynamic
external val process: dynamic
external val __dirname: dynamic

external class URL(a: String)

fun main(args: Array<String>) {
    console.log("Start");
    app.whenReady().then {
        val mainWindow = BrowserWindow(object : BrowserWindowConstructorOptions {})
        mainWindow.loadURL("https://github.com")
    }
}



