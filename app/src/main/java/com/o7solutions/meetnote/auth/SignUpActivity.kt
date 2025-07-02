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
import com.o7solutions.meetnote.MainActivity
import com.o7solutions.meetnote.R
import com.o7solutions.meetnote.auth.LoginActivity
import com.o7solutions.meetnote.constants.Constant
import com.o7solutions.meetnote.data_classes.Users
import com.o7solutions.meetnote.databinding.ActivityLoginBinding
import com.o7solutions.meetnote.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivitySignUpBinding
    private var _isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


//        signoutbtn.setonClicklistener {
//        auth.signOut()


//    }



        binding.apply {

            loginBTN.setOnClickListener {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            createAccountBTN.setOnClickListener {


                showProgressBar()
                binding.pgBar.visibility = View.VISIBLE
                if (emailET.text!!.isEmpty()) {
                    binding.pgBar.visibility = View.GONE

                    emailET.error = "Please enter email"
                } else if (passET.text!!.isEmpty()) {
                    binding.pgBar.visibility = View.GONE

                    passET.error = "Please enter password"
                } else if (nameET.text!!.isEmpty()) {
                    binding.pgBar.visibility = View.GONE

                    nameET.error = "Please enter name"
                } else if (designationET.text!!.isEmpty()) {
                    binding.pgBar.visibility = View.GONE

                    designationET.error = "Please enter designation"
                } else {
                    val email = emailET.text.toString().trim()
                    val pass = passET.text.toString().trim()
                    val name = nameET.text.toString().trim()
                    val designation = designationET.text.toString().trim()

                    auth.createUserWithEmailAndPassword(email, pass).addOnFailureListener { e ->
                        binding.pgBar.visibility = View.GONE

                        Toast.makeText(
                            this@SignUpActivity,
                            "Unable to create account->${e}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                        .addOnSuccessListener {
                            addUser(name, email, pass, designation)
                        }
                        .addOnFailureListener { e ->
                            showProgressBar()
                            Toast.makeText(
                                this@SignUpActivity,
                                "Account creation Failed -> ${e}",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                }
            }

        }
    }


    fun addUser(name: String, email: String, password: String, designation: String) {
        var id = auth.currentUser!!.uid

        val user = Users(name = name, email = email, designation = designation)
        db.collection(Constant.userCol).document(id).set(user)
            .addOnSuccessListener {
                showProgressBar()
                Toast.makeText(
                    this@SignUpActivity,
                    "Account creation Successful!",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }

    }

    private fun showProgressBar() {

        _isLoading = !_isLoading
        if (_isLoading) {
            binding.pgBar.visibility = View.VISIBLE
        } else {
            binding.pgBar.visibility = View.GONE

        }

    }
}