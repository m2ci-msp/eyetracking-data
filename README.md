# eyetracking-data

Data collected for experiments with eyetracking during phonetic segmentation tasks.

## Video data

We have released a [5 min. preview] of the data.

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

[5 min. preview]: https://github.com/m2ci-msp/eyetracking-data/releases/tag/v1.0-preview
[Matroska]: https://www.matroska.org/
[VLC]: https://www.videolan.org/vlc/
[audio]: src/experiment/northwind_rm.flac
[paper]: http://www.lrec-conf.org/proceedings/lrec2018/summaries/676.html
