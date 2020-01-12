package xh.destiny.core.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import java.io.*
import kotlin.Exception
import android.provider.MediaStore
import xh.destiny.core.R


class ImageUtil {
    companion object {

        fun watermarkText(context: Context, src: Bitmap, watermark: String) : Bitmap {
            val resultBitmap = Bitmap.createBitmap(src.width, src.height, src.config)
            val canvas = Canvas(resultBitmap)
            canvas.drawBitmap(src, 0f, 0f, null)
            val paint = Paint()
            paint.color = context.resources.getColor(R.color.color_watermark_text)
//            paint.alpha = 70  // hex:46
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = context.resources.getDimension(R.dimen.core_watermark_size)
            paint.isAntiAlias = true
            // 获取文本矩形
            val bounds = Rect()
            paint.getTextBounds(watermark, 0, watermark.length, bounds)
            canvas.drawText(watermark, src.width.toFloat() - bounds.width() - 20f, bounds.height() + 10f, paint)
            return resultBitmap
        }

        fun bitmapFrom(context: Activity, file: File?, callback: (bitmap: Bitmap) -> Unit) {
            Thread {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val img = BitmapFactory.decodeFile(file?.absolutePath, options)
//                val resizeBitmap = Bitmap.createBitmap(img, 0,0 , img.width, img.height)
                context.runOnUiThread {
                    callback(img)
                }
            }.start()
        }

        fun compressBytesToFile(bytes: ByteArray?,
                                desPath: String,
                                compressedName: String,
                                reqWidth: Int,
                                reqHeight: Int): File? {
            if (bytes == null) return null

            val compressedFile = File(desPath, compressedName)
            var output: FileOutputStream? = null
            var out: ByteArrayOutputStream? = null
            return try {
                output = FileOutputStream(compressedFile)
                out = ByteArrayOutputStream()
                val original = decodeSampledBitmapFromByteArray(bytes, reqWidth, reqHeight)
                original.compress(Bitmap.CompressFormat.JPEG, 50, out)
                output.write(out.toByteArray())
                output.close()

                compressedFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                output?.close()
                out?.close()
            }
        }

        /**
         * 压缩照片文件
         * desPath: 压缩后的路径
         * compressedName: 压缩后的文件名称
         */
        fun compressFileToFile(srcFile: File?,
                               desPath: String,
                               compressedName: String,
                               reqWidth: Int,
                               reqHeight: Int,
                               addWatermark: (src: Bitmap) -> Bitmap = {it}): File? {
            if (srcFile == null) return null

            val compressedFile = File(desPath, compressedName)
            var output: FileOutputStream? = null
            var out: ByteArrayOutputStream? = null
            return try {
                output = FileOutputStream(compressedFile)
                out = ByteArrayOutputStream()
                val original = decodeSampledBitmapFromFile(srcFile, reqWidth, reqHeight)
                val originalWithWatermark = addWatermark(original)
                originalWithWatermark.compress(Bitmap.CompressFormat.JPEG, 50, out)
                output.write(out.toByteArray())
                output.close()

                compressedFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                output?.close()
                out?.close()
            }
        }

        private fun decodeSampledBitmapFromResource(
            res: Resources,
            resId: Int,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeResource(res, resId, this)

                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false

                BitmapFactory.decodeResource(res, resId, this)
            }
        }

        private fun decodeSampledBitmapFromFile(
            f: File,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(f.absolutePath, this)

                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false

                val scaledBmp = BitmapFactory.decodeFile(f.absolutePath, this)

                // 对照片方向进行矫正
                val ei = ExifInterface(f.absolutePath)
                when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> return rotateImage(scaledBmp, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> return rotateImage(scaledBmp, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> return rotateImage(scaledBmp, 270)
                    else -> scaledBmp
                }
            }
        }

        private fun decodeSampledBitmapFromByteArray(
            bytes: ByteArray,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)

                // Calculate inSampleSize
                // 设置采样尺寸
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false

                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
            }
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        @Throws(IOException::class)
        fun rotateImageIfRequired(img: Bitmap, context: Context, selectedImage: Uri): Bitmap {
            if (selectedImage.scheme == "content") {
                val projection = arrayOf(MediaStore.Images.ImageColumns.ORIENTATION)
                val c = context.contentResolver.query(selectedImage, projection, null, null, null)
                if (c!!.moveToFirst()) {
                    val rotation = c.getInt(0)
                    c.close()
                    return rotateImage(img, rotation)
                }
                return img
            } else {
                val ei = ExifInterface(selectedImage.path)
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> return rotateImage(img, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> return rotateImage(img, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> return rotateImage(img, 270)
                    else -> return img
                }
            }
        }

        private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        }
    }
}