import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.process.JavaForkOptions
import org.gradle.workers.*
import org.yaml.snakeyaml.Yaml

import javax.inject.Inject

class GeneratePraatScenes extends DefaultTask {

    final WorkerExecutor workerExecutor

    @InputFile
    final RegularFileProperty scenesFile = newInputFile()

    @InputFile
    final RegularFileProperty audioFile = newInputFile()

    @OutputDirectory
    final DirectoryProperty destDir = newOutputDirectory()

    @Inject
    GeneratePraatScenes(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor
    }

    @TaskAction
    void generate() {
        def scriptFile = project.file("$temporaryDir/script.praat")
        def spectrogramFile = project.file("$temporaryDir/sound.Spectrogram")
        scriptFile.withWriter { script ->
            script.println "Read from file... ${audioFile.get().asFile}"
            script.println "To Spectrogram... 0.005 5000 0.002 20 Gaussian"
            script.println "Write to binary file... $spectrogramFile"
        }
        project.exec {
            commandLine 'praat', '--no-pref-files', '--no-plugins', '--run', scriptFile
        }
        // find which praat
        def stdout = new ByteArrayOutputStream()
        project.exec {
            commandLine 'which', 'praat'
            standardOutput = stdout
            errorOutput = stdout
        }
        def praatBinary = project.file(stdout.toString().trim())
        new Yaml().load(scenesFile.get().asFile.newReader()).eachWithIndex { scene, s ->
            workerExecutor.submit(PraatSceneGenerator.class) { WorkerConfiguration config ->
                config.params praatBinary, audioFile.get().asFile, spectrogramFile, scene.window.start, scene.window.end, destDir.file(String.format('scene_%04d.png', s + 1)).get().asFile
            }
        }
        workerExecutor.await()
    }

}

class PraatSceneGenerator implements Runnable {

    File praatBinary
    File soundFile
    File spectrogramFile
    double start
    double end
    File pngFile

    @Inject
    PraatSceneGenerator(File praatBinary, File soundFile, File spectrogramFile, double start, double end, File pngFile) {
        this.praatBinary = praatBinary
        this.soundFile = soundFile
        this.spectrogramFile = spectrogramFile
        this.start = start
        this.end = end
        this.pngFile = pngFile
    }

    @Override
    void run() {
        def scriptFile = File.createTempFile('script', '.praat')
        scriptFile.withWriter { script ->
            script.println 'Helvetica'
            script.println 'Erase all'
            script.println 'Font size... 3'
            script.println 'Line width... 0.3'
            script.println 'Select inner viewport... 0.3560 6.0499 0.41 1.54'
            script.println "Read from file... $soundFile"
            script.println "Draw... $start $end 0 0 no Curve"
            script.println "Draw inner box"
            script.println "One mark left... 0.0 yes yes yes"
            script.println 'Select inner viewport... 0.3566 6.0499 1.54 2.6765'
            script.println "Read from file... $spectrogramFile"
            script.println "Paint... $start $end 0 0 100 yes 50 6 0 yes"
            script.println 'Select outer viewport... 0 6.4 0 4.0'
            script.println "Save as 300-dpi PNG file... $pngFile"
        }
        def commandLine = [praatBinary, '--no-pref-files', '--no-plugins', '--run', scriptFile]
        println commandLine.join(' ')
        commandLine.execute().waitFor()
    }
}
