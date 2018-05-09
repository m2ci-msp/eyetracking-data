import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.yaml.snakeyaml.Yaml

import java.time.*

class ConvertScenesToSrt extends DefaultTask {

    @InputFile
    final RegularFileProperty srcFile = newInputFile()

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void convert() {
        destFile.get().asFile.withWriter { srt ->
            def offset
            new Yaml().load(srcFile.get().asFile.newReader()).eachWithIndex { scene, s ->
                offset = offset ?: scene.start.toInstant()
                def start = formatInstantToTimestamp(scene.start.toInstant(), offset)
                def end = formatInstantToTimestamp(scene.end.toInstant(), offset)
                srt.println s + 1
                srt.println "$start --> $end"
                srt.println sprintf('{ start: %.6f, end: %.6f }\n', scene.window.start, scene.window.end)
            }
        }
    }

    String formatInstantToTimestamp(Instant instant, Instant offset) {
        def duration = Duration.between(offset, instant)
        def fields = [
                duration.toHours(),
                duration.toMinutes() - duration.toHours() * 60,
                duration.seconds - duration.toMinutes() * 60,
                duration.toMillis() - duration.seconds * 1000
        ]
        sprintf '%02d:%02d:%02d,%03d', fields
    }

}
