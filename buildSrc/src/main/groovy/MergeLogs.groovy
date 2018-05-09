import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.yaml.snakeyaml.*

import java.text.SimpleDateFormat

class MergeLogs extends DefaultTask {

    @InputFile
    final RegularFileProperty praatFile = newInputFile()

    @InputFile
    final RegularFileProperty tobiiFile = newInputFile()

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void convert() {
        def data = new groovy.json.JsonSlurper().parse(tobiiFile.get().asFile)
        def opts = new DumperOptions()
        opts.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        def yaml = new Yaml(opts)
        def dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        dateFormat.timeZone = TimeZone.getTimeZone('Europe/Berlin')
        def sceneData = []
        new Yaml().load(praatFile.get().asFile.newReader()).each { scene ->
            def result = data.findAll {
                def timestamp = dateFormat.parse(it.date)
                timestamp >= scene.start && timestamp < scene.end
            }
            def sceneMap = [
                    start      : scene.start,
                    end        : scene.end,
                    windowStart: scene.window.start as double,
                    windowEnd  : scene.window.end as double,
                    gaze       : []
            ]
            result.each {
                sceneMap.gaze << [
                        timeStamp: dateFormat.parse(it.date),
                        gazeType : it.value.gaze_type,
                        gazeDur  : it.value.gaze_duration as double,
                        position : [xPos: it.value.xPos as int,
                                    yPos: it.value.yPos as int]
                ]
            }
            sceneData << sceneMap
        }
        yaml.dump(sceneData, destFile.get().asFile.newWriter())
    }
}
