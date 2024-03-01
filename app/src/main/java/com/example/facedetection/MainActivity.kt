package com.example.facedetection

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.facedetection.ui.theme.FaceDetectionTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val btnFaceDetection = findViewById<Button>(R.id.btnFaceDetect)

        btnFaceDetection.setOnClickListener {
            try {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                //if intent working properly or not
                if (cameraIntent.resolveActivity(packageManager) != null){
                    startActivityForResult(cameraIntent,123)
                }
                else{
                    Toast.makeText(this,"Opps something wrong",Toast.LENGTH_SHORT).show()
                }

            } catch (e:IOException){
                Log.d(TAG, "onCreate: " + e.message)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode==123 && resultCode== RESULT_OK && data != null){
                val extras = data?.extras
                val bitmap = extras?.get("data") as Bitmap

                faceDetect(bitmap)
            }
        }catch (e:IOException){
            Log.d(TAG, "onActivityResult: "+e.message)
        }
    }

    private fun faceDetect(bitmap: Bitmap) {
        try {
            //for high accuracy landmark and face classification
            val highAccuracyOptions = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

            val image = InputImage.fromBitmap(bitmap,0)

            val faceDetector = FaceDetection.getClient(highAccuracyOptions)

            //for process image
            val result = faceDetector.process(image)
                .addOnSuccessListener { faces->
                    //task completed successfully and face is detected successfully
                    var resultText = " "

                    for (face in faces){
                        var i = 1;
                        resultText = "Face number: $i\n"+
                                "Smile percentage: ${face.smilingProbability?.times(100)}\n"+
                                "Left eye open: ${face.leftEyeOpenProbability?.times(100)}\n"+
                                "Right eye open: ${face.rightEyeOpenProbability?.times(100)}"
                        i++
                    }
                    if (faces.isEmpty()){
                        Toast.makeText(this,"No face detected",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val resul = findViewById<TextView>(R.id.resultView)
                        resul.text = resultText
                    }
                }
                .addOnFailureListener{
                    //failed with exception
                    Toast.makeText(this,"Something wrong",Toast.LENGTH_SHORT).show()
                }
        }catch (e:IOException){
            Log.d(TAG, "faceDetect: "+e.message)
        }
    }
}

