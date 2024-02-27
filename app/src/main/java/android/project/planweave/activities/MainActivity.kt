package android.project.planweave.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityMainBinding
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this@MainActivity)
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
                Toast.makeText(
                    this@MainActivity,
                    "My Profile",
                    Toast.LENGTH_SHORT
                ).show()
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
}