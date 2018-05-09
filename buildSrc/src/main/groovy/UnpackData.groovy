import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class UnpackData extends DefaultTask {

    @OutputDirectory
    final DirectoryProperty destDir = newOutputDirectory()

    @OutputFile
    final RegularFileProperty praatLogFile = newOutputFile()

    @OutputFile
    final RegularFileProperty tobiiLogFile = newOutputFile()

    @OutputFile
    final RegularFileProperty screenCaptureFile = newOutputFile()

    @OutputFile
    final RegularFileProperty audioFile = newOutputFile()

    @TaskAction
    void unpack() {
        project.copy {
            from project.configurations.data
            into destDir
            filesMatching '*.zip', { zipFileDetails ->
                project.copy {
                    from project.zipTree(zipFileDetails.file)
                    into destDir
                }
                zipFileDetails.exclude()
            }
            filesMatching '*.mkv', { mkvFileDetails ->
                project.exec {
                    commandLine 'ffmpeg', '-i', mkvFileDetails.file,
                            '-map', '0:0', '-codec', 'copy', screenCaptureFile.get().asFile,
                            '-map', '0:1', '-codec', 'copy', audioFile.get().asFile,
                            '-loglevel', 'panic', '-y'
                }
                mkvFileDetails.exclude()
            }
        }
    }
}
