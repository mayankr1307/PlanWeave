package android.project.planweave.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivitySignInBinding
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private var binding: ActivitySignInBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()

        binding?.ivBack?.setOnClickListener { onBackPressed() }
        binding?.btnSignin?.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun signInRegisteredUser() {
        val email: String = binding?.etEmail?.text.toString()
        val password: String = binding?.etPassword?.text.toString()

        if(validateForm(email, password)) {
            showProgressDialog("Please Wait...")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@SignInActivity) { task ->
                    hideProgressDialog()
                    if(task.isSuccessful) {
                        val user = auth.currentUser
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    }else {
                        Toast.makeText(
                            this@SignInActivity,
                            "Authentication Failed: ${task.exception}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        when {
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