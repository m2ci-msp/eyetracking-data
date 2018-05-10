import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class AssembleData extends DefaultTask {

    @InputFiles
    FileCollection srcFiles = project.files()

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void assemble() {
        destFile.get().asFile.withWriter('UTF-8') { writer ->
            writer.println '# Phonetic Segmentation Eyetracking Data\n'
            writer.println '# see https://git.io/eyeseg-data for details\n'
            writer.println '# license: Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International\n'
            srcFiles.each { srcFile ->
                writer << srcFile.text
            }
        }
    }
}
