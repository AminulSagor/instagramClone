package com.somadhan.instagramclone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.somadhan.instagramclone.Models.User
import com.somadhan.instagramclone.databinding.ActivityLogInBinding
import com.somadhan.instagramclone.databinding.ActivitySignUpBinding

class LogInActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLogInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.loginUpBtn.setOnClickListener {
            if (binding.Email.editText?.text.toString().isEmpty()
                || binding.Password.editText?.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this@LogInActivity,
                    "Please fill the information.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var user = User(
                    binding.Email.editText?.text.toString(),
                    binding.Password.editText?.text.toString()
                )
                Firebase.auth.signInWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this@LogInActivity, HomeActivity::class.java))
                            finish()
                        }else
                        {
                            Toast.makeText(this@LogInActivity, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}