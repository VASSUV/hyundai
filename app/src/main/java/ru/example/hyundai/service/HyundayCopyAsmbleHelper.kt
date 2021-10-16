package ru.example.hyundai.service

import android.content.Context
import android.content.res.AssetManager
import java.io.*

class HyundayCopyAsmbleHelper {

    fun run(service: HyundaiService) {
        HyundaiService.Shared.ASMBLE_COPIED.saveBoolean(false)
        if(!HyundaiService.Shared.ASMBLE_COPIED.getBoolean()) {
            val path = getAppPath(service)
            copyAssetFolder(service.assets, "asmble", "$path/asmble")
            HyundaiService.Shared.ASMBLE_COPIED.saveBoolean(true)
        }
    }

    fun getAppPath(context: Context): String {
        val pm = context.packageManager
        val pkgInfo = pm.getPackageInfo(context.packageName, 0)
        val appInfo = pkgInfo!!.applicationInfo
        return appInfo.dataDir?.toString() ?: ""
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while ((`in`.read(buffer).also { read = it }) != -1) {
            out.write(buffer, 0, read)
        }
    }

    private fun copyAsset(assetManager: AssetManager, fromAssetPath: String, toPath: String) {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = assetManager.open(fromAssetPath)
            File(toPath).createNewFile()
            out = FileOutputStream(toPath)
            copyFile(`in`!!, out!!)
            `in`!!.close()
            `in` = null
            out!!.flush()
            out!!.close()
            out = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun copyAssetFolder(assetManager: AssetManager, fromAssetPath: String, toPath: String) {
        try {
            val files = assetManager.list(fromAssetPath)!!
            File(toPath).mkdirs()
            for (file in files)
                if (file.contains("."))
                    copyAsset(assetManager, "$fromAssetPath/$file", "$toPath/$file")
                else
                    copyAssetFolder(assetManager, "$fromAssetPath/$file", "$toPath/$file")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}