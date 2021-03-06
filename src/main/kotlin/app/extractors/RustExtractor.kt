// Copyright 2018 Sourcerer Inc. All Rights Reserved.
// Author: Alexander Surkov (alex@sourcerer.io)

package app.extractors

import app.model.CommitStats
import app.model.DiffFile

class RustExtractor : ExtractorInterface {
    companion object {
        const val LANGUAGE_NAME = Lang.Rust
        val evaluator by lazy {
            ExtractorInterface.getLibraryClassifier(LANGUAGE_NAME)
        }
        val importRegex = Regex("""^extern crate \w+;$""")
        val commentRegex = Regex("(//.+$)|(/[*].*?[*]/)")
        val extractImportRegex = Regex("""^extern crate (\w+);$""")
    }

    override fun extract(files: List<DiffFile>): List<CommitStats> {
        files.map { file -> file.language = LANGUAGE_NAME }
        return super.extract(files)
    }

    override fun extractImports(fileContent: List<String>): List<String> {
        val imports = mutableSetOf<String>()

        fileContent.forEach {
            val res = extractImportRegex.find(it)
            if (res != null) {
                val lineLib = res.groupValues[1]
                imports.add(lineLib)
            }
        }

        return imports.toList()
    }

    override fun tokenize(line: String): List<String> {
        var newLine = importRegex.replace(line, "")
        newLine = commentRegex.replace(newLine, "")
        return super.tokenize(newLine)
    }

    override fun getLineLibraries(line: String,
                                  fileLibraries: List<String>): List<String> {

        return super.getLineLibraries(line, fileLibraries, evaluator,
            LANGUAGE_NAME)
    }
}
