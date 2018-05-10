# Phonetic Segmentation Eyetracking Data

Data collected for experiments with eyetracking during phonetic segmentation tasks.

See the [releases] to download the data.

## Data

The data consists of a [YAML] file containing a list of static scenes viewed by each subject ("vp"), with associated gaze events and relevant UI details.
Example excerpt:

```yaml
- start: 2014-12-10T08:24:16.033Z
  end: 2014-12-10T08:24:18.033Z
  windowStart: 0.0
  windowEnd: 3.3231078705735193
  gaze:
  - vp: '02'
    timeStamp: 2014-12-10T08:24:16.416Z
    signalTime: 1.2743769
    gazeType: Fixation
    gazeDur: 425.0
    gazeRegion: spectrogram
    position:
      xPos: 761
      yPos: 579
    subRegion: middle
  - vp: '02'
    timeStamp: 2014-12-10T08:24:16.858Z
    signalTime: 1.3288541
    gazeType: Fixation
    gazeDur: 175.0
    gazeRegion: spectrogram
    position:
      xPos: 789
      yPos: 563
    subRegion: top
```

## Video data

For each subject, we provide a video in [Matroska] format (which can be played in [VLC] and other free software), including
- screen capture of the session (video stream #1)
- audio played back during the segmentation task
- rendered acoustic waveform and spectrogram for each scene, and gaze fixations (video stream #2)
- subtitles indicating start and end time of each scene in the segmented recording

The second video stream has been *reconstructed* from the segmented [audio], structured scene information, and gaze data (to be released).

## Background

For details, see the [paper] presented at LREC 2018:
```bibtex
@inproceedings{Khan2018LREC,
  year = {2018},
  month = may,
  booktitle = {11th Language Resources and Evaluation Conference (LREC)},
  address = {Miyazaki, Japan},
  author = {Khan, Arif and Steiner, Ingmar and Sugano, Yusuke and Bulling, Andreas and Macdonald, Ross},
  date = {2018-05-11},
  eprint = {1712.04798},
  eprinttype = {arxiv},
  pages = {4277-4281},
  title = {A Multimodal Corpus of Expert Gaze and Behavior during Phonetic Segmentation Tasks},
  url = {http://www.lrec-conf.org/proceedings/lrec2018/summaries/676.html}
}
```

[YAML]: http://yaml.org/
[releases]: https://github.com/m2ci-msp/eyetracking-data/releases
[Matroska]: https://www.matroska.org/
[VLC]: https://www.videolan.org/vlc/
[audio]: src/experiment/northwind_rm.flac
[paper]: http://www.lrec-conf.org/proceedings/lrec2018/summaries/676.html
