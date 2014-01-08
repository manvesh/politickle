
import com.typesafe.config.ConfigFactory
import play.api.{Configuration, GlobalSettings}
import play.api.mvc.WithFilters
import play.filters.csrf.CSRFFilter
import java.io.File
import play.api.Mode
import play.Logger

object Global extends WithFilters(CSRFFilter()) with GlobalSettings {
  override def onLoadConfig(config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode): Configuration = {
    val configFile = s"application.${mode.toString.toLowerCase}.conf"
    Logger.debug("Adding more config files!: " + configFile)
    val modeSpecificConfig = config ++ Configuration(ConfigFactory.load(configFile))
    super.onLoadConfig(modeSpecificConfig, path, classloader, mode)
  }
}
