# jdcli

`jdcli` is a lightweight command line interface tool to extract and decompile java classes in .jar files by using
[jd-core](https://github.com/java-decompiler/jd-core), the library behind [JD-GUI](https://java-decompiler.github.io/).

This tool also supports extracting jar files with case-insensitive class name conflicts (which commonly occur in
obfuscated jar files) on case-insensitive filesystems. It also does not stop when it fails to decompile a singular
file, allowing complicated jar files crafted using different tools to still be analyzed.

Usage:

```
usage: jdcli <command> [arguments...]

valid commands:
  jdcli dj|decompile-jar <jar file> [output directory] [--verbose] [--skip-cleanup] - Decompile a jar file into a directory
  jdcli dcs|decompile-classes <directory> [--verbose] [--delete-class-files] - Decompile classes in a directory
  jdcli dc|decompile-class <base directory> <file> - Decompile a .class file
  jdcli cleanup <directory> - removes all .class files in a directory
  jdcli h|help - Show this menu
  jdcli v|version - Show version information

options:
  -v --verbose | Log additional debug information
```

Example: `jdcli decompile-jar ./test.jar ./test.classes/`

This can also be used with APK files by using a tool like [dex2jar](https://github.com/pxb1988/dex2jar) to convert it to
a .jar file.

### Installation

Installation requires Java/OpenJDK version >= 16. To install, download the latest jar file from the
[releases page](https://github.com/brxxn/jdcli/releases).

If you use Linux or macOS, you may want to add
`alias jdcli="java -jar [jdcli jar location]"` to your `~/.bashrc` or `~/.zshrc` so that you can invoke jdcli using just
`jdcli`. Once you complete that and restart your terminal, you should be able to use `jdcli`. If you can't do this, you
can always use `java -jar [jdcli jar location]` instead.

### Commands

* `jdcli dj|decompile-jar <jar file> [output directory] [--verbose] [--skip-cleanup]`
  * Decompiles a .jar file into .java files
  * Arguments:
    * `<jar file>`: the .jar file to extract from
    * `[output directory]`: the directory to put the extracted files in (default: `[jar path].classes/`)
  * Options:
    * `--verbose`: adds heavy logging output (warning: it will spam your terminal), useful for debugging.
    * `--skip-cleanup`: skips removing .class files (this will use more storage per decompile)
* `jdcli dcs|decompile-classes <directory> [--verbose] [--delete-class-files]`
  * Recursively decompiles all .class files into .java files
  * Arguments:
    * `<directory>`: the target directory in which classes should be decompiled
  * Options:
    * `--verbose`: adds heavy logging output (warning: it will spam your terminal), useful for debugging.
    * `--delete-class-files`: deletes all .class files after decompiling is complete
* `jdcli dc|decompile-class <base directory> <file>`
  * Decompiles a specified .class file and shows any errors that occur
  * Arguments:
    * `<base directory>`: the directory that should be used as the base for the class (so it can identify packages)
    * `<file>`: the targeted .class file to decompile
* `jdcli cleanup <directory>`
  * Recursively removes all .class files in a directory
  * Arguments:
    * `<directory>`: the directory in which class files should be deleted
* `jdcli h|help`
  * Show the help menu
* `jdcli v|version`
  * Shows the version and a link to the releases page

### Building

This project should be able to be imported into IntelliJ IDEA and have all dependencies installed using `mvn install`.
To build, use `mvn package`. Note that the target version for jdcli is Java/OpenJDK 16.