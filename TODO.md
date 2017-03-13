# Features
* Annotates identities and bounding boxes 
* Built-in face detector
* Built-in face recognition
* Export to FDDB format

# Wish List
* Local face DB in protobufs for language interop
* Remote DB in cloud for annotation sharing
* Automatic or semiautomatic labeling
* Automatic or semiautomatic label regeneration 
* Diagnostic tool to sniff out bad labels
* Fixes
    + hanging delete / rename box on DB tab
* Annotation / metadata extensions
    + gender
    + smile
    + happy
    + sad
    + tired

# v0.4
- [ ] Cloud based annotation database
- [ ] Checklist
    - [ ] Archived code
    - [ ] Repeatable results
    - [ ] Documentation up-to-date
    - [ ] Peer review

# v0.3
- [ ] Protobuf implementation of FaceDB
    - [ ] Unit tests for existing DB
    - [ ] Refactor to use Java's externalize interface
    - [ ] Revise to use protobufs
- [ ] Evaluate stability and function of face Eigenvalue recognizer plugin
- [ ] Revise, remove or replace BetaFace detector plugin
- [ ] Revise, remove or replace original openCV face  detector plugin
- [ ] Checklist
    - [ ] Archived code
    - [ ] Repeatable results
    - [ ] Documentation up-to-date
    - [ ] Peer review

# v0.2
- [ ] Filter to subset data to export
- [ ] Changes to eliminate 
- [ ] Export FDDB format to file
- [ ] File chooser to default to root of data directory
- [ ] Directory choice to persist over invocations
- [ ] Checklist
    - [ ] Archived code
    - [ ] Documentation up-to-date
    - [ ] Repeatable results
    - [ ] Peer review
    - [ ] First results / report

# v0.1
- [x] Export annotation database in FDDB format
- [x] Reimplement OpenCV face detector to eliminate dependencies on Windows
- [x] Archive code on github
