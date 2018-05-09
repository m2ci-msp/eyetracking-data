import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import com.xlson.groovycsv.CsvParser

class FindOffsets extends DefaultTask {

    @InputFile
    final RegularFileProperty praatlog = newInputFile()

    @InputFile
    final RegularFileProperty tobiilog = newInputFile()

    @TaskAction
    void findOffsets() {
        def offSet = praatStart(praatlog.get().asFile).time - tobiiStart(tobiilog.get().asFile).time
        project.file("gradle.properties").text = "offset=$offSet"
        println offSet
    }

    Date praatStart(File praatlog) {
        def dateFormat = "EEE MMM dd HH:mm:ss yyyy"
        def praatStartingPoint = (praatlog.readLines().get(2) - 'Date: ').trim()
        return Date.parse(dateFormat, praatStartingPoint)
    }

    Date tobiiStart(File tobiilog) {
        def tsvReader = new FileReader(tobiilog)
        def data = CsvParser.parseCsv(['separator': '\t'], tsvReader)
        def recDate = data[0].RecordingDate
        def localTimeStamp = data[0].LocalTimeStamp
        return Date.parse("dd.MM.yyyy HH:mm:ss.SSS", "$recDate $localTimeStamp")
    }
}
