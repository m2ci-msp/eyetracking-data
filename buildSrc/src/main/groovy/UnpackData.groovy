import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class UnpackData extends DefaultTask {

    @OutputDirectory
    File destDir

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
                            '-map', '0:0', '-codec', 'copy', "$destDir/screencapture.mp4",
                            '-map', '0:1', '-codec', 'copy', "$destDir/audio.flac",
                            '-loglevel', 'panic', '-y'
                }
                mkvFileDetails.exclude()
            }
        }
    }
}
