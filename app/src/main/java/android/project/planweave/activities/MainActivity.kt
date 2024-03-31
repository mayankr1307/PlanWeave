package android.project.planweave.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.adapters.BoardItemsAdapter
import android.project.planweave.databinding.ActivityMainBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.Board
import android.project.planweave.models.User
import android.project.planweave.utils.Constants
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null

    private lateinit var mUserName: String

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this@MainActivity)
        FireStoreClass().loadUserData(this@MainActivity, true)

        binding?.appBarLayout?.fabCreateBoard?.setOnClickListener {
            val intent = Intent(this@MainActivity,
                CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    fun populateBoarDListToUI(boardsList: ArrayList<Board>) {
        hideProgressDialog()

        if(boardsList.size > 0) {
            binding?.appBarLayout?.mainContent?.rvBoardList?.visibility = View.VISIBLE
            binding?.appBarLayout?.mainContent?.tvNoBoardsAvailable?.visibility = View.GONE

            binding?.appBarLayout?.mainContent?.rvBoardList?.layoutManager = LinearLayoutManager(this@MainActivity)
            binding?.appBarLayout?.mainContent?.rvBoardList?.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this@MainActivity, boardsList)
            binding?.appBarLayout?.mainContent?.rvBoardList?.adapter = adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }else {
            binding?.appBarLayout?.mainContent?.rvBoardList?.visibility = View.GONE
            binding?.appBarLayout?.mainContent?.tvNoBoardsAvailable?.visibility = View.VISIBLE
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FireStoreClass().loadUserData(this@MainActivity)
        } else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE) {
            FireStoreClass().getBoardsList(this@MainActivity)
        }else {
            Log.e("Cancelled", "Cancelled")
        }

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this@MainActivity, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
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

    fun updateNavigationUserDetails(user: User?, readBoardsList: Boolean) {
        mUserName = user!!.name
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

        if(readBoardsList) {
            showProgressDialog("Please wait...")
            FireStoreClass().getBoardsList(this@MainActivity)
        }
    }
}