package android.project.planweave.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityMyProfileBinding

class MyProfileActivity : BaseActivity() {

    private var binding: ActivityMyProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.tbProfileActivity)
        if(supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_white)
            supportActionBar!!.title = "My Profile"
        }

        binding?.tbProfileActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}