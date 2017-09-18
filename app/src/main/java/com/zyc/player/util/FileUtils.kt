package com.zyc.player.util

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.FilenameFilter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.ArrayList
import java.util.Collections

/**
 * <pre>
 * author: cc
 * blog  : http://cc.com
 * time  : 2016/8/11
 * desc  : 文件相关工具类
</pre> *
 */
class FileUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {
        val KB = 1024

        /**
         * 根据文件路径获取文件
         *
         * @param filePath 文件路径
         * @return 文件
         */
        fun getFileByPath(filePath: String): File? {
            return if (StringUtils.isSpace(filePath)) null else File(filePath)
        }

        /**
         * 判断文件是否存在
         *
         * @param filePath 文件路径
         * @return `true`: 存在<br></br>`false`: 不存在
         */
        fun isFileExists(filePath: String): Boolean {
            return isFileExists(getFileByPath(filePath))
        }

        /**
         * 判断文件是否存在
         *
         * @param file 文件
         * @return `true`: 存在<br></br>`false`: 不存在
         */
        fun isFileExists(file: File?): Boolean {
            return file != null && file.exists()
        }

        /**
         * 判断是否是目录
         *
         * @param dirPath 目录路径
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isDir(dirPath: String): Boolean {
            return isDir(getFileByPath(dirPath))
        }

        /**
         * 判断是否是目录
         *
         * @param file 文件
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isDir(file: File?): Boolean {
            return isFileExists(file) && file!!.isDirectory
        }

        /**
         * 判断是否是文件
         *
         * @param filePath 文件路径
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isFile(filePath: String): Boolean {
            return isFile(getFileByPath(filePath))
        }

        /**
         * 判断是否是文件
         *
         * @param file 文件
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isFile(file: File?): Boolean {
            return isFileExists(file) && file!!.isFile
        }

        /**
         * 判断目录是否存在，不存在则判断是否创建成功
         *
         * @param dirPath 文件路径
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsDir(dirPath: String): Boolean {
            return createOrExistsDir(getFileByPath(dirPath))
        }

        /**
         * 判断目录是否存在，不存在则判断是否创建成功
         *
         * @param file 文件
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsDir(file: File?): Boolean {
            // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        }

        /**
         * 判断文件是否存在，不存在则判断是否创建成功
         *
         * @param filePath 文件路径
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsFile(filePath: String): Boolean {
            return createOrExistsFile(getFileByPath(filePath))
        }

        /**
         * 判断文件是否存在，不存在则判断是否创建成功
         *
         * @param file 文件
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsFile(file: File?): Boolean {
            if (file == null) return false
            // 如果存在，是文件则返回true，是目录则返回false
            if (file.exists()) return file.isFile
            if (!createOrExistsDir(file.parentFile)) return false
            try {
                return file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * 判断文件是否存在，存在则在创建之前删除
         *
         * @param filePath 文件路径
         * @return `true`: 创建成功<br></br>`false`: 创建失败
         */
        fun createFileByDeleteOldFile(filePath: String): Boolean {
            return createFileByDeleteOldFile(getFileByPath(filePath))
        }

        /**
         * 判断文件是否存在，存在则在创建之前删除
         *
         * @param file 文件
         * @return `true`: 创建成功<br></br>`false`: 创建失败
         */
        fun createFileByDeleteOldFile(file: File?): Boolean {
            if (file == null) return false
            // 文件存在并且删除失败返回false
            if (file.exists() && file.isFile && !file.delete()) return false
            // 创建目录失败返回false
            if (!createOrExistsDir(file.parentFile)) return false
            try {
                return file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }
        /**
         * 删除目录
         *
         * @param dirPath 目录路径
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteDir(dirPath: String): Boolean {
            return deleteDir(getFileByPath(dirPath))
        }

        /**
         * 删除目录
         *
         * @param dir 目录
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteDir(dir: File?): Boolean {
            if (dir == null) return false
            // 目录不存在返回true
            if (!dir.exists()) return true
            // 不是目录返回false
            if (!dir.isDirectory) return false
            // 现在文件存在且是文件夹
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (file.isFile) {
                        if (!deleteFile(file)) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
            return dir.delete()
        }

        /**
         * 删除文件
         *
         * @param srcFilePath 文件路径
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFile(srcFilePath: String): Boolean {
            return deleteFile(getFileByPath(srcFilePath))
        }

        /**
         * 删除文件
         *
         * @param file 文件
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFile(file: File?): Boolean {
            return file != null && (!file.exists() || file.isFile && file.delete())
        }

        /**
         * 删除目录下的所有文件
         *
         * @param dirPath 目录路径
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFilesInDir(dirPath: String): Boolean {
            return deleteFilesInDir(getFileByPath(dirPath))
        }

        /**
         * 删除目录下的所有文件
         *
         * @param dir 目录
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFilesInDir(dir: File?): Boolean {
            if (dir == null) return false
            // 目录不存在返回true
            if (!dir.exists()) return true
            // 不是目录返回false
            if (!dir.isDirectory) return false
            // 现在文件存在且是文件夹
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (file.isFile) {
                        if (!deleteFile(file)) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
            return true
        }

        /**
         * 获取目录下所有文件
         *
         * @param dirPath     目录路径
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        fun listFilesInDir(dirPath: String, isRecursive: Boolean): List<File>? {
            return listFilesInDir(getFileByPath(dirPath), isRecursive)
        }

        /**
         * 获取目录下所有文件
         *
         * @param dir         目录
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        fun listFilesInDir(dir: File?, isRecursive: Boolean): List<File>? {
            if (isRecursive) return listFilesInDir(dir)
            if (dir == null || !isDir(dir)) return null
            val list = ArrayList<File>()
            Collections.addAll(list, *dir.listFiles())
            return list
        }

        /**
         * 获取目录下所有文件包括子目录
         *
         * @param dirPath 目录路径
         * @return 文件链表
         */
        fun listFilesInDir(dirPath: String): List<File>? {
            return listFilesInDir(getFileByPath(dirPath))
        }

        /**
         * 获取目录下所有文件包括子目录
         *
         * @param dir 目录
         * @return 文件链表
         */
        fun listFilesInDir(dir: File?): List<File>? {
            if (dir == null || !isDir(dir)) return null
            val list = ArrayList<File>()
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    list.add(file)
                    if (file.isDirectory) {
                        list.addAll(listFilesInDir(file)!!)
                    }
                }
            }
            return list
        }

        /**
         * 获取目录下所有后缀名为suffix的文件
         *
         * 大小写忽略
         *
         * @param dirPath     目录路径
         * @param suffix      后缀名
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(dirPath: String, suffix: String, isRecursive: Boolean): List<File>? {
            return listFilesInDirWithFilter(getFileByPath(dirPath), suffix, isRecursive)
        }

        /**
         * 获取目录下所有后缀名为suffix的文件
         *
         * 大小写忽略
         *
         * @param dir         目录
         * @param suffix      后缀名
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(dir: File?, suffix: String, isRecursive: Boolean): List<File>? {
            if (isRecursive) return listFilesInDirWithFilter(dir, suffix)
            if (dir == null || !isDir(dir)) return null
            val list = ArrayList<File>()
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (file.name.toUpperCase().endsWith(suffix.toUpperCase())) {
                        list.add(file)
                    }
                }
            }
            return list
        }

        /**
         * 获取目录下所有后缀名为suffix的文件包括子目录
         *
         * 大小写忽略
         *
         * @param dirPath 目录路径
         * @param suffix  后缀名
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(dirPath: String, suffix: String): List<File>? {
            return listFilesInDirWithFilter(getFileByPath(dirPath), suffix)
        }

        /**
         * 获取目录下所有后缀名为suffix的文件包括子目录
         *
         * 大小写忽略
         *
         * @param dir    目录
         * @param suffix 后缀名
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(dir: File?, suffix: String): List<File>? {
            if (dir == null || !isDir(dir)) return null
            val list = ArrayList<File>()
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (file.name.toUpperCase().endsWith(suffix.toUpperCase())) {
                        list.add(file)
                    }
                    if (file.isDirectory) {
                        list.addAll(listFilesInDirWithFilter(file, suffix)!!)
                    }
                }
            }
            return list
        }

        /**
         * 获取目录下所有符合filter的文件
         *
         * @param dirPath     目录路径
         * @param filter      过滤器
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(dirPath: String, filter: FilenameFilter, isRecursive: Boolean): List<File>? {
            return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
        }

        /**
         * 获取目录下所有符合filter的文件
         *
         * @param dir         目录
         * @param filter      过滤器
         * @param isRecursive 是否递归进子目录
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(dir: File?, filter: FilenameFilter, isRecursive: Boolean): List<File>? {
            if (isRecursive) return listFilesInDirWithFilter(dir, filter)
            if (dir == null || !isDir(dir)) return null
            val list = ArrayList<File>()
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (filter.accept(file.parentFile, file.name)) {
                        list.add(file)
                    }
                }
            }
            return list
        }

        /**
         * 获取目录下所有符合filter的文件包括子目录
         *
         * @param dirPath 目录路径
         * @param filter  过滤器
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(dirPath: String, filter: FilenameFilter): List<File>? {
            return listFilesInDirWithFilter(getFileByPath(dirPath), filter)
        }

        /**
         * 获取目录下所有符合filter的文件包括子目录
         *
         * @param dir    目录
         * @param filter 过滤器
         * @return 文件链表
         */
        fun listFilesInDirWithFilter(dir: File?, filter: FilenameFilter): List<File>? {
            if (dir == null || !isDir(dir)) return null
            val list = ArrayList<File>()
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (filter.accept(file.parentFile, file.name)) {
                        list.add(file)
                    }
                    if (file.isDirectory) {
                        list.addAll(listFilesInDirWithFilter(file, filter)!!)
                    }
                }
            }
            return list
        }

        /**
         * 获取目录下指定文件名的文件包括子目录
         *
         * 大小写忽略
         *
         * @param dirPath  目录路径
         * @param fileName 文件名
         * @return 文件链表
         */
        fun searchFileInDir(dirPath: String, fileName: String): List<File>? {
            return searchFileInDir(getFileByPath(dirPath), fileName)
        }

        /**
         * 获取目录下指定文件名的文件包括子目录
         *
         * 大小写忽略
         *
         * @param dir      目录
         * @param fileName 文件名
         * @return 文件链表
         */
        fun searchFileInDir(dir: File?, fileName: String): List<File>? {
            if (dir == null || !isDir(dir)) return null
            val list = ArrayList<File>()
            val files = dir.listFiles()
            if (files != null && files.size != 0) {
                for (file in files) {
                    if (file.name.toUpperCase() == fileName.toUpperCase()) {
                        list.add(file)
                    }
                    if (file.isDirectory) {
                        list.addAll(searchFileInDir(file, fileName)!!)
                    }
                }
            }
            return list
        }

        /**
         * 将字符串写入文件
         *
         * @param filePath 文件路径
         * @param content  写入内容
         * @param append   是否追加在文件末
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromString(filePath: String, content: String, append: Boolean): Boolean {
            return writeFileFromString(getFileByPath(filePath), content, append)
        }

        /**
         * 将字符串写入文件
         *
         * @param file    文件
         * @param content 写入内容
         * @param append  是否追加在文件末
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromString(file: File?, content: String?, append: Boolean): Boolean {
            if (file == null || content == null) return false
            if (!createOrExistsFile(file)) return false
            var bw: BufferedWriter? = null
            try {
                bw = BufferedWriter(FileWriter(file, append))
                bw.write(content)
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                CloseUtils.closeIO(bw!!)
            }
        }

        /**
         * 指定编码按行读取文件到List
         *
         * @param filePath    文件路径
         * @param charsetName 编码格式
         * @return 文件行链表
         */
        fun readFile2List(filePath: String, charsetName: String): List<String>? {
            return readFile2List(getFileByPath(filePath), charsetName)
        }

        /**
         * 指定编码按行读取文件到List
         *
         * @param file        文件
         * @param charsetName 编码格式
         * @return 文件行链表
         */
        fun readFile2List(file: File?, charsetName: String): List<String>? {
            return readFile2List(file, 0, 0x7FFFFFFF, charsetName)
        }

        /**
         * 指定编码按行读取文件到List
         *
         * @param filePath    文件路径
         * @param st          需要读取的开始行数
         * @param end         需要读取的结束行数
         * @param charsetName 编码格式
         * @return 包含制定行的list
         */
        fun readFile2List(filePath: String, st: Int, end: Int, charsetName: String): List<String>? {
            return readFile2List(getFileByPath(filePath), st, end, charsetName)
        }

        /**
         * 指定编码按行读取文件到List
         *
         * @param file        文件
         * @param st          需要读取的开始行数
         * @param end         需要读取的结束行数
         * @param charsetName 编码格式
         * @return 包含从start行到end行的list
         */
        fun readFile2List(file: File?, st: Int, end: Int, charsetName: String): List<String>? {
            if (file == null) return null
            if (st > end) return null
            var reader: BufferedReader? = null
            try {
                var line: String
                var curLine = 1
                val list = ArrayList<String>()
                if (StringUtils.isSpace(charsetName)) {
                    reader = BufferedReader(FileReader(file))
                } else {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
                }

                line = reader.readLine()
                while (line != null) {
                    if (curLine > end) break
                    if (st <= curLine && curLine <= end) list.add(line)
                    ++curLine
                    line = reader.readLine()
                }
                return list
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(reader!!)
            }
        }

        /**
         * 指定编码按行读取文件到字符串中
         *
         * @param filePath    文件路径
         * @param charsetName 编码格式
         * @return 字符串
         */
        fun readFile2String(filePath: String, charsetName: String): String? {
            return readFile2String(getFileByPath(filePath), charsetName)
        }

        /**
         * 指定编码按行读取文件到字符串中
         *
         * @param file        文件
         * @param charsetName 编码格式
         * @return 字符串
         */
        fun readFile2String(file: File?, charsetName: String): String? {
            if (file == null) return null
            var reader: BufferedReader? = null
            try {
                val sb = StringBuilder()
                if (StringUtils.isSpace(charsetName)) {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file)))
                } else {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
                }
                var line: String = reader.readLine()
                while ((line ) != null) {
                    sb.append(line).append("\r\n")// windows系统换行为\r\n，Linux为\n
                    line = reader.readLine()
                }
                // 要去除最后的换行符
                return sb.delete(sb.length - 2, sb.length).toString()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                CloseUtils.closeIO(reader!!)
            }
        }

        /**
         * 简单获取文件编码格式
         *
         * @param filePath 文件路径
         * @return 文件编码
         */
        fun getFileCharsetSimple(filePath: String): String {
            return getFileCharsetSimple(getFileByPath(filePath))
        }

        /**
         * 简单获取文件编码格式
         *
         * @param file 文件
         * @return 文件编码
         */
        fun getFileCharsetSimple(file: File?): String {
            var p = 0
            var `is`: InputStream? = null
            try {
                `is` = BufferedInputStream(FileInputStream(file!!))
                p = (`is`.read() shl 8) + `is`.read()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                CloseUtils.closeIO(`is`!!)
            }
            when (p) {
                0xefbb -> return "UTF-8"
                0xfffe -> return "Unicode"
                0xfeff -> return "UTF-16BE"
                else -> return "GBK"
            }
        }


        /**
         * 获取全路径中的最长目录
         *
         * @param file 文件
         * @return filePath最长目录
         */
        fun getDirName(file: File?): String? {
            return if (file == null) null else getDirName(file.path)
        }

        /**
         * 获取全路径中的最长目录
         *
         * @param filePath 文件路径
         * @return filePath最长目录
         */
        fun getDirName(filePath: String): String? {
            if (StringUtils.isSpace(filePath)) return filePath
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
        }

        /**
         * 获取全路径中的文件名
         *
         * @param file 文件
         * @return 文件名
         */
        fun getFileName(file: File?): String? {
            return if (file == null) null else getFileName(file.path)
        }

        /**
         * 获取全路径中的文件名
         *
         * @param filePath 文件路径
         * @return 文件名
         */
        fun getFileName(filePath: String): String? {
            if (StringUtils.isSpace(filePath)) return filePath
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
        }

        /**
         * 获取全路径中的不带拓展名的文件名
         *
         * @param file 文件
         * @return 不带拓展名的文件名
         */
        fun getFileNameNoExtension(file: File?): String? {
            return if (file == null) null else getFileNameNoExtension(file.path)
        }

        /**
         * 获取全路径中的不带拓展名的文件名
         *
         * @param filePath 文件路径
         * @return 不带拓展名的文件名
         */
        fun getFileNameNoExtension(filePath: String): String? {
            if (StringUtils.isSpace(filePath)) return filePath
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            if (lastSep == -1) {
                return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
            }
            return if (lastPoi == -1 || lastSep > lastPoi) {
                filePath.substring(lastSep + 1)
            } else filePath.substring(lastSep + 1, lastPoi)
        }

        /**
         * 获取全路径中的文件拓展名
         *
         * @param file 文件
         * @return 文件拓展名
         */
        fun getFileExtension(file: File?): String? {
            return if (file == null) null else getFileExtension(file.path)
        }

        /**
         * 获取全路径中的文件拓展名
         *
         * @param filePath 文件路径
         * @return 文件拓展名
         */
        fun getFileExtension(filePath: String): String? {
            if (StringUtils.isSpace(filePath)) return filePath
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
        }
    }
}