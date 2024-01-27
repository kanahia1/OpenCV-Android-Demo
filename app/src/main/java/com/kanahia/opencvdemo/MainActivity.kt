package com.kanahia.opencvdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var outputImage: ImageView
    private lateinit var inputImage: ImageView
    private lateinit var chooseImageButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        outputImage = findViewById(R.id.imageView)
        inputImage = findViewById(R.id.imageView2)
        chooseImageButton = findViewById(R.id.chooseImageButton)
        chooseImageButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            try {
                val outputBitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver, selectedImageUri
                )
                val inputBitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver, selectedImageUri
                )
                inputImage.setImageBitmap(inputBitmap)
                if(OpenCVLoader.initDebug()){
                    var inputStream = resources.openRawResource(R.raw.haarcascade_russian_plate_number)
                    try {
                        var file = File(getDir("cascade", MODE_PRIVATE),"haarcascade_russian_plate_number.xml")
                        var fileOutputStream = FileOutputStream(file)
                        var data = ByteArray(4096)

                        var bytesRead: Int
                        while (inputStream.read(data).also { bytesRead = it } != -1) {
                            fileOutputStream.write(data, 0, bytesRead)
                        }

                        var cascadeClassifier : CascadeClassifier?  = CascadeClassifier(file.absolutePath)
                        if (cascadeClassifier!!.empty()) cascadeClassifier = null

                        var rgb = Mat(outputBitmap.width, outputBitmap.height, CvType.CV_8UC1)
                        var gray = Mat(outputBitmap.width, outputBitmap.height, CvType.CV_8UC1)
                        var rects = MatOfRect()
                        Utils.bitmapToMat(outputBitmap,rgb)
                        Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);
                        cascadeClassifier!!.detectMultiScale(gray,rects,1.1,2)
                        for (rect in rects.toList()){
                            var submat = rgb.submat(rect)
                            Imgproc.blur(submat,submat, Size(10.0,10.0))
                        }

                        Utils.matToBitmap(rgb,outputBitmap)
                        inputStream.close()
                        fileOutputStream.close()
                        file.delete()
                    }catch (e : Exception){
                        Log.e("PRINT",e.toString())
                    }
                }
                outputImage!!.setImageBitmap(outputBitmap)
            } catch (e: Exception) {
                Log.e("PRINT",e.toString())
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
