package android.project.planweave.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityMainBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.User
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this@MainActivity)
        FireStoreClass().signInUser(this@MainActivity)

    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.appBarLayout?.tbMainActivity)
        binding?.appBarLayout?.tbMainActivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        binding?.appBarLayout?.tbMainActivity?.title = "Plan Weave"
        binding?.appBarLayout?.tbMainActivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else {
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else {
            doubleBackToExit()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_my_profile -> {
                startActivity(Intent(this@MainActivity, MyProfileActivity::class.java))
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@MainActivity, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)

        return true
    }

    fun updateNavigationUserDetails(user: User?) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)

        val myImageView = headerView.findViewById<CircleImageView>(R.id.nav_user_image)

        if (user != null) {
            Glide
                .with(this@MainActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(myImageView)
        }

        if (user != null) {
            headerView.findViewById<TextView>(R.id.tv_username).text = user.name
        }
    }
}