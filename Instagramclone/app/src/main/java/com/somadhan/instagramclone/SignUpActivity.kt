package com.somadhan.instagramclone

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.somadhan.instagramclone.Models.User
import com.somadhan.instagramclone.databinding.ActivitySignUpBinding
import com.somadhan.instagramclone.utils.USER_NODE
import com.somadhan.instagramclone.utils.USER_PROFILE_FOLDER
import com.somadhan.instagramclone.utils.uploadImage
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var user: User
    private val launcher=registerForActivityResult(ActivityResultContracts.GetContent())
    {
        uri->
        uri?.let {

            uploadImage(uri, USER_PROFILE_FOLDER)
            {
                if (it!=null)
                {
                    user.image=it
                    binding.profileImage.setImageURI(uri)
                }
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val text="<font color=#FF000000>Already have an account</font> <font color=#1E88E5>LogIn?</font>"
        binding.login.setText(Html.fromHtml(text))
        user=User()
        if (intent.hasExtra("Mode"))
        {
            if (intent.getIntExtra("Mode",-1)==1)
            {

                binding.signUpBtn.text="Update Profile"
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                    user=it.toObject<User>()!!
                    if (!user.image.isNullOrEmpty()) {
                        Picasso.get().load(user.image).into(binding.profileImage)
                    }
                    binding.name.editText?.setText(user.name)
                    binding.email.editText?.setText(user.email)
                    binding.password.editText?.setText(user.password)
                }
            }
        }
        binding.signUpBtn.setOnClickListener {
            if (intent.hasExtra("Mode")) {
                if (intent.getIntExtra("Mode", -1) == 1) {

                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser!!.uid).set(user)
                        .addOnSuccessListener {

                            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                            finish()
                        }

                }
            }
            else{
            if (binding.name.editText?.text.toString()
                    .equals("") or binding.email.editText?.text.toString().equals("") or
                binding.password.editText?.text.toString().equals("")
            ) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please fill the required information",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editText?.text.toString(),
                    binding.password.editText?.text.toString()
                ).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        user.name = binding.name.editText?.text.toString()
                        user.email = binding.email.editText?.text.toString()
                        user.password = binding.password.editText?.text.toString()
                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {

                                startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                                finish()
                            }
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            result.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        }
        binding.plusImage.setOnClickListener {
            launcher.launch("image/*")
        }
        binding.login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,LogInActivity::class.java))
            finish()
        }

    }
}