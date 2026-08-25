# Madhura File Packer - Unpacker

A small Java desktop utility that packs multiple files (or an entire folder,
recursively) into a single archive file, and unpacks that archive back into
the original files — preserving folder structure.

Built with plain Java + AWT (no external dependencies).

## Features

- Pack any number of files, or an entire folder tree, into one `.mvp` archive
- Preserves relative folder structure on pack/unpack
- Simple AWT GUI with a live progress log
- Path-traversal safety check on unpack (won't write outside the chosen output folder)
- Zero external dependencies — just the JDK

## Project structure

```
src/filepacker/
├── Main.java              # Entry point — launches the GUI
├── FilePackerFrame.java   # AWT GUI (Pack / Unpack screens)
├── Packer.java            # Packing logic
├── Unpacker.java          # Unpacking logic
└── ProgressListener.java  # Callback interface for progress messages
```

## Requirements

- JDK 11 or later

## Build & run

```bash
cd src
javac filepacker/*.java
java filepacker.Main
```

This opens the GUI. Use **Pack Files** to select a folder to archive, and
**Unpack Package** to restore an `.mvp` archive to a chosen output folder.

> Note: AWT's `FileDialog` can't pick a folder directly on most platforms,
> so the folder pickers work by having you select *any file inside* the
> folder you want — its parent folder is then used.

## Archive format (`.mvp`)

A simple custom binary format:

```
[int]    number of entries
For each entry:
    [int]    length of relative path (UTF-8 bytes)
    [bytes]  relative path (forward-slash separated, portable across OSes)
    [long]   length of file content
    [bytes]  file content
```

## License

MIT — see [LICENSE](LICENSE).
