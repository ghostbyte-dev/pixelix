import com.daniebeler.pfpixelix.desktopApp
import io.github.kdroidfilter.nucleus.core.runtime.DeepLinkHandler

fun main(args: Array<String>) {
    DeepLinkHandler.register(args) { uri ->
        println("Received deep link: $uri")
        // Handle: myapp://open?file=document.txt
    }
    desktopApp(args)
}
