import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class MuxStreams extends DefaultTask {

    @InputFile
    final RegularFileProperty gazeVideoFile = newInputFile()

    @InputFile
    final RegularFileProperty screenCaptureFile = newInputFile()

    @InputFile
    final RegularFileProperty audioFile = newInputFile()

    @InputFile
    final RegularFileProperty subtitleFile = newInputFile()

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void mux() {
        project.exec {
            commandLine 'ffmpeg', '-loglevel', 'panic', '-y',
                    '-i', gazeVideoFile.get().asFile,
                    '-i', screenCaptureFile.get().asFile,
                    '-i', audioFile.get().asFile,
                    '-i', subtitleFile.get().asFile, '-disposition:s:0', 'default',
                    '-codec', 'copy',
                    '-map', '0:0',
                    '-map', '1:0',
                    '-map', '2:0',
                    '-map', '3:0',
                    destFile.get().asFile
        }
    }
}
