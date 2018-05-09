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
                    fixationWithData << [
                            date : destDateFormat.format(date),
                            value: [
                                    gaze_type    : gazeEvent,
                                    gaze_duration: gazeDuration,
                                    xPos         : xPosition,
                                    yPos         : yPosition
                            ]]
                    prevXPosition = xPosition
                    prevYPosition = yPosition
                }
            }
        }
        destFile.get().asFile.text = new JsonBuilder(fixationWithData).toPrettyString()
    }
}
