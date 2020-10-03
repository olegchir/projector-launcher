import Electron.BrowserWindow
import Electron.BrowserWindowConstructorOptions
import Electron.app

class ElectronApp(val url: String) {
    open fun start() {
        app.whenReady().then {
            val mainWindow = BrowserWindow(object : BrowserWindowConstructorOptions {})
            mainWindow.loadURL(url)
        }
    }
}
