package android.project.planweave.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private var binding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sign_in)

        setupToolBar()
    }
    private fun setupToolBar() {
        setSupportActionBar(binding?.tbSignin)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.tbSignin?.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}