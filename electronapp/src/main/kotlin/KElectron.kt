@file:JsModule("electron")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS",
        "OVERRIDING_FINAL_MEMBER",
        "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
        "CONFLICTING_OVERLOADS",
//        "PACKAGE_OR_CLASSIFIER_REDECLARATION",
//        "REDECLARATION",
//        "NOTHING_TO_OVERRIDE",
//        "UNRESOLVED_REFERENCE"
)
package Electron

import kotlin.js.Promise

external interface App : Any {
    fun whenReady(): Promise<Unit>
}
external var app: App


external open class BrowserWindow(options: BrowserWindowConstructorOptions = definedExternally) : Any {
    open fun loadURL(url: String, options: LoadURLOptions = definedExternally): Promise<Unit>
}

external interface BrowserWindowConstructorOptions {
    var width: Number?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface LoadURLOptions {
    var httpReferrer: dynamic /* String? | Referrer? */
        get() = definedExternally
        set(value) = definedExternally
    var userAgent: String?
        get() = definedExternally
        set(value) = definedExternally
    var extraHeaders: String?
        get() = definedExternally
        set(value) = definedExternally
    var postData: dynamic /* Array<UploadRawData>? | Array<UploadFile>? | Array<UploadBlob>? */
        get() = definedExternally
        set(value) = definedExternally
    var baseURLForDataURL: String?
        get() = definedExternally
        set(value) = definedExternally
}
