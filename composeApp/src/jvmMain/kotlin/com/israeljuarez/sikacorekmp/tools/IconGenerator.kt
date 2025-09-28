package com.israeljuarez.sikacorekmp.tools

import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object IconGenerator {
    private fun writeIcoFromPng(png: ByteArray, outFile: File) {
        // ICO with a single PNG image entry (256x256 recommended). Width/Height 0 => 256 per spec
        val header = ByteArray(6)
        val bbHeader = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
        bbHeader.putShort(0) // reserved
        bbHeader.putShort(1) // type: 1=icon
        bbHeader.putShort(1) // count

        val entry = ByteArray(16)
        val bbEntry = ByteBuffer.wrap(entry).order(ByteOrder.LITTLE_ENDIAN)
        bbEntry.put(0) // width: 0 means 256
        bbEntry.put(0) // height: 0 means 256
        bbEntry.put(0) // color count
        bbEntry.put(0) // reserved
        bbEntry.putShort(1) // planes
        bbEntry.putShort(32) // bitCount
        bbEntry.putInt(png.size) // bytes in resource
        bbEntry.putInt(6 + 16) // image offset

        FileOutputStream(outFile).use { fos ->
            fos.write(header)
            fos.write(entry)
            fos.write(png)
        }
    }

    private fun writeIcnsWithPngChunk(png: ByteArray, outFile: File) {
        // Minimal ICNS with a single 'ic09' (512x512 PNG) chunk; many launchers accept it
        val type = byteArrayOf('i'.code.toByte(), 'c'.code.toByte(), '0'.code.toByte(), '9'.code.toByte())
        val chunkLen = 8 + png.size // 4 bytes type + 4 bytes length included + data
        val fileLen = 8 + chunkLen

        val bb = ByteBuffer.allocate(fileLen).order(ByteOrder.BIG_ENDIAN)
        // magic 'icns'
        bb.put('i'.code.toByte())
        bb.put('c'.code.toByte())
        bb.put('n'.code.toByte())
        bb.put('s'.code.toByte())
        bb.putInt(fileLen)
        // chunk
        bb.put(type)
        bb.putInt(chunkLen)
        bb.put(png)

        FileOutputStream(outFile).use { fos ->
            fos.write(bb.array())
        }
    }

    fun ensureDesktopIcons(outputDir: File) {
        if (!outputDir.exists()) outputDir.mkdirs()
        val ico = File(outputDir, "logo.ico")
        val icns = File(outputDir, "logo.icns")
        // Leer el PNG desde el repositorio (ruta relativa)
        val source = File("composeApp/src/commonMain/composeResources/drawable/logo.png")
        if (!source.exists()) return
        val pngBytes = source.readBytes()

        if (!ico.exists()) writeIcoFromPng(pngBytes, ico)
        if (!icns.exists()) writeIcnsWithPngChunk(pngBytes, icns)
    }
}
