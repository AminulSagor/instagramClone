package com.somadhan.instagramclone.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.somadhan.instagramclone.Models.User
import com.somadhan.instagramclone.R
import com.somadhan.instagramclone.SignUpActivity
import com.somadhan.instagramclone.adapters.viewPagerAdapter
import com.somadhan.instagramclone.databinding.FragmentProfileBinding
import com.somadhan.instagramclone.utils.USER_NODE
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: viewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.editProfile.setOnClickListener {
            val intent=Intent(activity,SignUpActivity::class.java)
            intent.putExtra("Mode",1)
            activity?.startActivity(intent)
            activity?.finish()
        }
        viewPagerAdapter= viewPagerAdapter(requireActivity().supportFragmentManager)
        viewPagerAdapter.addFragments(MyPostFragment(),"My Post")
        viewPagerAdapter.addFragments(MyReelsFragment(),"My Reels")
        binding.viewPager.adapter=viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        return binding.root
    }

    companion object {

    }

    override fun onStart() {
        super.onStart()

        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            Firebase.firestore.collection(USER_NODE).document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user: User? = document.toObject<User>()
                        if (user != null) {
                            binding.name.text = user.name
                            binding.bio.text = user.email
                            if (!user.image.isNullOrEmpty()) {
                                Picasso.get().load(user.image).into(binding.profileImage)
                            }
                        } else {
                            Toast.makeText(context, "User data is null", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "No such document", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error getting documents: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(context, "User is not logged in", Toast.LENGTH_LONG).show()
        }
    }
}