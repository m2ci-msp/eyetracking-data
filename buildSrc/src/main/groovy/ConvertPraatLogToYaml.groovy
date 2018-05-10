import groovy.time.TimeCategory
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.yaml.snakeyaml.*

import java.text.SimpleDateFormat

class ConvertPraatLogToYaml extends DefaultTask {

    @InputFile
    final RegularFileProperty srcFile = newInputFile()

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void convert() {
        def opts = new DumperOptions()
        opts.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        def yaml = new Yaml(opts)
        def frames = []
        def frameStr = ''
        def offsetStr = project.findProperty('offset')
        def offset = offsetStr ? Integer.parseInt(offsetStr) : 0
        def dateFormat = new SimpleDateFormat('EEE MMM dd HH:mm:ss yyyy')
        dateFormat.timeZone = TimeZone.getTimeZone('Europe/Berlin')
        srcFile.get().asFile.eachLine { line ->
            switch (line) {
                case ~/^Editor type:.+/:
                    if (frameStr) {
                        def map = yaml.load(frameStr)
                        def date = dateFormat.parse(map.Date)
                        use(TimeCategory) {
                            date.time -= offset
                        }
                        frames << [window: [start: (map.'Window start' - 'seconds') as double,
                                            end  : (map.'Window end' - 'seconds') as double],
                                   date  : date]
                    }
                    frameStr = "$line\n"
                    break
                default:
                    frameStr += "$line\n"
                    break
            }
        }
        yaml.dump(frames, destFile.get().asFile.newWriter())
    }
}
