package com.migu.android.network.util

import android.net.Uri
import cn.leancloud.LCObject
import cn.leancloud.LCUser
import com.migu.android.core.LinkYou
import com.migu.android.core.util.GsonUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object LeanCloudUtils {

    /**
     * 获取当前登录的对象
     * 如果SDK缓存没有，就使用本地的缓存
     */
    @Synchronized
    fun getLCUser(): LCUser? {
        return LCUser.getCurrentUser()
            ?: GsonUtils.fromJsonNormal<LCUser>(LinkYou.sdkLoginLCUserJson)
    }


    /**
     * 获取用户信息。
     *
     * @return 用户信息的 LCObject 对象，如果不存在则返回 null
     */
    @Synchronized
    fun getUserInfo(): LCObject? {
        return GsonUtils.fromJsonNormal<LCObject>(LinkYou.sdkUserInfoJson)
    }


    /**
     * 将文件转换为字节数组
     * @param file 要转换的文件
     * @return 转换后的字节数组
     */
    fun fileToByteArray(file: File): ByteArray {
        // 创建文件输入流
        val inputStream = FileInputStream(file)
        // 读取文件内容到字节数组
        val bytes = inputStream.readBytes()
        // 关闭输入流
        inputStream.close()
        return bytes
    }

    /**
     * 将 Uri 转换为文件
     * @param uri 要转换的 Uri
     * @return 转换后的文件
     */
    fun uriToFile(uri: Uri): File {
        // 根据当前时间生成一个临时文件名
        val file = File("${LinkYou.context.cacheDir}/${System.currentTimeMillis()}.png")
        // 定义数据缓冲区
        val dataBuffer = ByteArray(4096)
        var byteRead = 0
        // 打开输入流
        LinkYou.context.contentResolver.openInputStream(uri).use { inputStream ->
            // 打开输出流
            FileOutputStream(file).use { fileOutputStream ->
                // 循环读取输入流中的数据，并写入输出流
                while (inputStream?.read(dataBuffer).also {
                        if (it != null) {
                            byteRead = it
                        }
                    } != -1) {
                    fileOutputStream.write(dataBuffer, 0, byteRead)
                }
                // 刷新输出流
                fileOutputStream.flush()
                // 关闭输入流
                inputStream?.close()
            }
        }
        return file
    }

}