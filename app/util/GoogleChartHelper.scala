package util

object GoogleChartHelper {

  val default_width = 350
  val default_height = 150
  val url_template = "https://chart.googleapis.com/chart?cht=p3&chd=t:%s&chs=%sx%s&chl=%s"

  def getChartUrl(counts: Seq[(Long, Int, String)], width: Int = default_width, height: Int = default_height) = {
    url_template format(
        (counts map { c => c._2 }) mkString ",",
        width,
        height,
        (counts map { c => c._3 }) mkString "|"
      )
  }

  def getChartUrlImageBinding(counts: Seq[(Long, Int, String)], width: Int = default_width, height: Int = default_height) = {
    CardApiHelper.getImageBinding(getChartUrl(counts, width, height), width, height)
  }
}