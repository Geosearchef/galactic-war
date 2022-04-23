import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLDivElement

fun main() {
    window.onload = {
        (document.getElementById("loading-indicator") as HTMLDivElement).style.display = "none"

        console.log("Loading Galactic War Frontend...")
    }
}