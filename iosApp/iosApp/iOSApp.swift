import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        MainInitKt.doInitApp()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}