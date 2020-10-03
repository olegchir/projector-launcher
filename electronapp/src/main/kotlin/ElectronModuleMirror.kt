@file:JsModule("electron")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS",
        "OVERRIDING_FINAL_MEMBER",
        "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
        "CONFLICTING_OVERLOADS",
        "PACKAGE_OR_CLASSIFIER_REDECLARATION",
        "REDECLARATION",
        "NOTHING_TO_OVERRIDE",
        "UNRESOLVED_REFERENCE")
package Electron

external var NodeEventEmitter: Any

external interface App : EventEmitter {}
external var app: App

external open class BrowserWindow(options: Any) : NodeEventEmitter {}
