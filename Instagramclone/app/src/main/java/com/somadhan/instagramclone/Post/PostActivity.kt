package com.somadhan.instagramclone.Post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.somadhan.instagramclone.HomeActivity
import com.somadhan.instagramclone.Models.Post
import com.somadhan.instagramclone.Models.User
import com.somadhan.instagramclone.databinding.ActivityPostBinding
import com.somadhan.instagramclone.utils.POST
import com.somadhan.instagramclone.utils.POST_FOLDER
import com.somadhan.instagramclone.utils.USER_NODE
import com.somadhan.instagramclone.utils.uploadImage


class PostActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {

            uploadImage(it, POST_FOLDER) { url ->

                if (url != null) {
                    Log.d("PostActivity", "Uploaded Image URL: $url")
                    binding.selectImage.setImageURI(uri)
                    imageUrl = url
                } else {

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.selectImage.setOnClickListener { launcher.launch("image/*") }
        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }
        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document().get()
                .addOnSuccessListener {

                    var user = it.toObject<User>()!!
                    val post: Post = Post(
                        imageUrl!!,
                        caption = binding.caption.editText?.text.toString(),
                        uid = Firebase.auth.currentUser!!.uid,
                        time = System.currentTimeMillis().toString()
                    )

                    Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                            .set(post).addOnSuccessListener {
                                startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                                finish()
                            }

                    }
                }
        }
    }
}