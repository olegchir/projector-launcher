import Electron.*
external fun require(module: String): dynamic
external val process: dynamic
external val __dirname: dynamic

external class URL(a: String)

fun main(args: Array<String>) {
    val argv = commandLineArguments();
    val url = argv.last();
    console.log("URL: $url")
    val eapp = ElectronApp(url);
    eapp.start();
}

fun commandLineArguments(): Array<String> {
    return process.argv as Array<String>;
}

fun testUrl(url: String): Boolean {
    var result: Boolean = true;
    try {
        val newUrl = URL(url)
    } catch (e: Throwable) {
        result = false;
    }
    return result;
}



