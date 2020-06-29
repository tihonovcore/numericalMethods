package loaders

import java.nio.file.Path

interface DiskLoader {

    fun saveOnDisk(
        destDir: Path,
        destName: String,
        records: Map<String, List<String>>
    )
}