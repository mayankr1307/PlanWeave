package android.project.planweave.activities

import android.os.Bundle
import android.project.planweave.databinding.ActivitySignUpBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.User
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    private var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.ivBack?.setOnClickListener { onBackPressed() }

        binding?.btnSignup?.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name: String = binding?.etName?.text.toString().trim{it <= ' '}
        val email: String = binding?.etEmail?.text.toString().trim{it <= ' '}
        val password: String = binding?.etPassword?.text.toString()

        if(validateForm(name, email, password)) {
            showProgressDialog("Please wait...")
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FireStoreClass().registerUser(this@SignUpActivity, user)
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    fun userRegisteredSuccess() {
        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        when {
            name.isEmpty() -> {
                showErrorSnackBar("Please enter your name.")
                return false
            }
            email.isEmpty() -> {
                showErrorSnackBar("Please enter your E-mail address.")
                return false
            }
            password.isEmpty() -> {
                showErrorSnackBar("Please enter your password.")
                return false
            }

            else -> {
                return true
            }
        }
    }
    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}