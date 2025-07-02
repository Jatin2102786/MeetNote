package com.o7solutions.meetnote.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.o7solutions.meetnote.MainActivity
import com.o7solutions.meetnote.R
import com.o7solutions.meetnote.databinding.ActivityLoginBinding
import com.o7solutions.meetnote.databinding.FragmentViewMeetBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.apply {
            val email = emailET.text.toString().trim()
            val password = passET.text.toString().trim()

            auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener {
//                    when user successfully login
                }
                .addOnFailureListener { e->

                }

        }

        binding.apply {

            createAccount.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }

            loginBTN.setOnClickListener {


                binding.pgBar.visibility = View.VISIBLE
                if (emailET.text!!.isEmpty()) {
                    binding.pgBar.visibility = View.GONE

                    emailET.error = "Please enter email"
                } else if (passET.text!!.isEmpty()) {
                    binding.pgBar.visibility = View.GONE

                    passET.error = "Please enter password"
                } else {
                    val email = emailET.text.toString().trim()
                    val pass = passET.text.toString().trim()

                    auth.signInWithEmailAndPassword(email, pass).addOnFailureListener { e ->
                        binding.pgBar.visibility = View.GONE

                        Toast.makeText(
                            this@LoginActivity,
                            "Unable to login->${e}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                        .addOnSuccessListener {
                            binding.pgBar.visibility = View.GONE
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                }
            }

        }
    }
}