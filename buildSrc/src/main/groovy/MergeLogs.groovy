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
            def gazeEvents = data.findAll {
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
            gazeEvents.each { gazeEvent ->
                def signalTime = ''
                if (gazeEvent.value.xPos >= project.praatMargins.left &&
                        gazeEvent.value.xPos < project.praatMargins.right) {
                    signalTime = scene.window.start +
                            (gazeEvent.value.xPos - project.praatMargins.left) *
                            (scene.window.end - scene.window.start) /
                            project.praatMargins.width as float
                }
                def gazeMap = [
                        vp        : project.name - 'vp',
                        timeStamp : dateFormat.parse(gazeEvent.date),
                        signalTime: signalTime,
                        gazeType  : gazeEvent.value.gaze_type,
                        gazeDur   : gazeEvent.value.gaze_duration as double,
                        gazeRegion: gazeEvent.value.region,
                        position  : [xPos: gazeEvent.value.xPos as int,
                                     yPos: gazeEvent.value.yPos as int]
                ]
                if (gazeEvent.value.sub_region) {
                    gazeMap.subRegion = gazeEvent.value.sub_region
                }
                sceneMap.gaze << gazeMap
            }
            sceneData << sceneMap
        }
        yaml.dump(sceneData, destFile.get().asFile.newWriter())
    }
}
