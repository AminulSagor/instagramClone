package com.somadhan.instagramclone.Post

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.somadhan.instagramclone.HomeActivity
import com.somadhan.instagramclone.Models.Reel
import com.somadhan.instagramclone.Models.User
import com.somadhan.instagramclone.databinding.ActivityReelsBinding
import com.somadhan.instagramclone.utils.REEL
import com.somadhan.instagramclone.utils.REEL_FOLDER
import com.somadhan.instagramclone.utils.USER_NODE
import com.somadhan.instagramclone.utils.uploadVideo

class ReelsActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }

    private lateinit var videoUrl: String
    lateinit var progressDialog: ProgressDialog
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {

            uploadVideo(it, REEL_FOLDER,progressDialog) { url ->

                if (url != null) {

                    videoUrl = url
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        progressDialog= ProgressDialog(this)

        binding.selectReel.setOnClickListener { launcher.launch("video/*") }
        binding.cancelButton.setOnClickListener {
            startActivity(
                Intent(
                    this@ReelsActivity,
                    HomeActivity::class.java
                )
            )
            finish()
        }

        binding.postButton.setOnClickListener {

            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                var user:User =it.toObject<User>()!!
                val reel: Reel = Reel(videoUrl!!,binding.caption.editText?.text.toString(),user.image!!)

                Firebase.firestore.collection(REEL).document().set(reel).addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+REEL).document().set(reel).addOnSuccessListener{
                        startActivity(Intent(this@ReelsActivity,HomeActivity::class.java))
                        finish()
                    }

                }
            }
        }



    }
}