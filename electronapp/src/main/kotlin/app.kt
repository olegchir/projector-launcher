external fun require(module: String): dynamic
external val process: dynamic
external val __dirname: dynamic

external class URL(a: String)

fun main(args: Array<String>) {
    val argv = commandLineArguments();
    val url = argv.last();
    if (testUrl(url)) {
        val eapp = ElectronApp(url);
        eapp.start();
    } else {
        console.log("Invalid URL")
        process.exit(0);
    }
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



