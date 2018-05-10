import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import com.xlson.groovycsv.CsvParser
import groovy.json.JsonBuilder

import java.text.SimpleDateFormat

class ConvertTobiiLog extends DefaultTask {

    @InputFile
    final RegularFileProperty srcFile = newInputFile()

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void convert() {
        def tsvReader = new FileReader(srcFile.get().asFile)
        def data = CsvParser.parseCsv(['separator': '\t'], tsvReader)
        def fixationWithData = []
        def prevXPosition = ""
        def prevYPosition = ""
        def srcDateFormat = new SimpleDateFormat('dd.MM.yyyy HH:mm:ss.SSS')
        srcDateFormat.timeZone = TimeZone.getTimeZone('Europe/Berlin')
        def destDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        destDateFormat.timeZone = TimeZone.getTimeZone('Europe/Berlin')
        data.each { row ->
            def xPosition = row.'FixationPointX (MCSpx)'
            def yPosition = row.'FixationPointY (MCSpx)'
            if (xPosition != prevXPosition || yPosition != prevYPosition) {
                if (xPosition) {
                    def date = srcDateFormat.parse("$row.RecordingDate $row.LocalTimeStamp")
                    def gazeEvent = row.'GazeEventType'
                    def gazeDuration = row.'GazeEventDuration'
                    def region
                    def subregion = ''
                    switch (xPosition as int) {
                        case { it >= project.praatMargins.left && it < project.praatMargins.right }:
                            switch (yPosition as int) {
                                case {
                                    it >= project.praatMargins.top &&
                                            it < project.praatMargins.spectrogramTop
                                }:
                                    region = 'waveform'
                                    break
                                case {
                                    it > project.praatMargins.spectrogramTop &&
                                            it <= project.praatMargins.spectrogramUpperBandBottom
                                }:
                                    region = 'spectrogram'
                                    subregion = 'top'
                                    break
                                case {
                                    it > project.praatMargins.spectrogramUpperBandBottom &&
                                            it <= project.praatMargins.spectrogramMiddleBandBottom
                                }:
                                    region = 'spectrogram'
                                    subregion = 'middle'
                                    break
                                case {
                                    it > project.praatMargins.spectrogramMiddleBandBottom &&
                                            it <= project.praatMargins.spectrogramBottom
                                }:
                                    region = 'spectrogram'
                                    subregion = 'lower'
                                    break
                                case {
                                    it > project.praatMargins.spectrogramBottom &&
                                            it <= project.praatMargins.bottom
                                }:
                                    region = 'annotation'
                                    break
                                default:
                                    region = 'other'
                                    break
                            }
                            break
                        default:
                            region = 'other'
                            break
                    }
                    fixationWithData << [
                            date : destDateFormat.format(date),
                            value: [
                                    gaze_type    : gazeEvent,
                                    gaze_duration: gazeDuration as int,
                                    xPos         : xPosition as int,
                                    yPos         : yPosition as int,
                                    region       : region,
                                    sub_region   : subregion
                            ]]

                    prevXPosition = xPosition
                    prevYPosition = yPosition
                }
            }
        }
        destFile.get().asFile.text = new JsonBuilder(fixationWithData).toPrettyString()
    }
}
