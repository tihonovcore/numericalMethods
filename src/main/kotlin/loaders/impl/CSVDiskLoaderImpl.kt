package loaders.impl

import loaders.DiskLoader
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.io.FileUtils
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path


class CSVDiskLoaderImpl : DiskLoader {

    override fun saveOnDisk(
        destDir: Path,
        destName: String,
        records: Map<String, List<String>>
    ) {
        val dest = destDir.resolve("$destName.csv")
        FileUtils.forceMkdirParent(dest.toFile())

        val out = Files.newBufferedWriter(dest, Charset.defaultCharset())

        val output = Array<MutableList<String>>(records.iterator().next().value.size) { ArrayList() }
        for (entry in records.values) {
            entry.forEachIndexed { index, s -> output[index].add(s) }
        }

        CSVPrinter(
            out, CSVFormat.DEFAULT
        ).use { printer ->
            printer.printRecord(records.keys)
            output.forEach(printer::printRecord)
        }

        println("Records for table $destName were saved to ${dest.fileName} successfully")
    }
}