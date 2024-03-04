package android.project.planweave.activities

import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityMyProfileBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.User
import android.widget.ImageView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class MyProfileActivity : BaseActivity() {

    private var binding: ActivityMyProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        FireStoreClass().loadUserData(this)
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

    fun setUserDataInUI(user: User) {

        val myImageView: CircleImageView? = binding?.ivUserImage

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(myImageView!!)

        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if(user.mobile != 0L) {
            binding?.etMobile?.setText(user.mobile.toString())
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}