# Madhura's File Packer - Unpacker

A Java desktop utility that packs multiple files from a folder into a single
archive file, and unpacks that archive back into the original files.

Built with plain Java + Swing (no external dependencies).

## Features
- Pack all files in a chosen folder into a single archive file
- Unpack an archive back into the original files
- Simple Swing GUI with folder browsing and a live log of pack/unpack activity
- Handles filenames containing spaces and non-ASCII characters (emoji,
  Devanagari script, etc.)
- Also runnable from the console (each class has its own `main()`)
- Zero external dependencies — just the JDK

## Project structure
```
src/filepacker/
├── Main.java              # Entry point — launches the GUI
├── FilePackerFrame.java   # Swing GUI (folder picker, pack/unpack buttons, log area)
├── Packer.java            # Packing logic (also runnable standalone from console)
└── Unpacker.java          # Unpacking logic (also runnable standalone from console)
```

## Requirements
- JDK 17 or later
- Check with: `java -version` and `javac -version`

## Build & run

1. Clone the repository:
   ```
   git clone https://github.com/MadhuraPande18/Madhura-s_File-Packer-Unpacker.git
   cd Madhura-s_File-Packer-Unpacker/src
   ```

2. Compile:
   ```
   javac filepacker/*.java
   ```
   (Windows: `javac filepacker\*.java`)

3. Run the GUI:
   ```
   java filepacker.main
   ```
   Click **Browse** to pick a folder to pack, name your output archive, and
   click **Pack**. To reverse the process, enter the archive's name and click
   **Unpack**.

   Alternatively, run `Packer` or `Unpacker` directly from the console:
   ```
   java filepacker.Packer
   java filepacker.Unpacker
   ```

## Archive format

Each file is stored as a fixed **100-byte header** followed by the file's raw
bytes:

```
[100 bytes] "filename|filesize" padded with spaces
[N bytes]   file content (N = filesize from header)
```
repeated for each file in the folder.

`|` is used as the delimiter (rather than a space) because it's an illegal
character in Windows filenames, so it can never collide with an actual
filename — unlike a space, which commonly appears in real filenames. Header
padding is measured in UTF-8 bytes rather than Java characters, since
non-ASCII characters (e.g. emoji, Devanagari script) can occupy more than one
byte each.

## Known limitations
- Subfolders inside the target folder are skipped, not recursed into
- Filenames whose UTF-8 byte length exceeds ~90 bytes (leaving no room for
  the size and delimiter within the 100-byte header) are skipped
- Packing/unpacking runs on the GUI's main thread, so the window can appear
  unresponsive during large operations

## License
MIT — see [LICENSE](LICENSE).
