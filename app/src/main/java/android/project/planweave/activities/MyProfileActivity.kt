package android.project.planweave.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityMyProfileBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.User
import android.project.planweave.utils.Constants
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var binding: ActivityMyProfileBinding? = null

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false

    private var mProfileImageURL: String = ""
    private lateinit var mUserDetails: User

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var mSelectedImageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        FireStoreClass().loadUserData(this)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.
        RequestMultiplePermissions()) { permissions ->
          isReadPermissionGranted = permissions[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              Manifest.permission.READ_MEDIA_IMAGES
          } else {
              TODO("VERSION.SDK_INT < TIRAMISU")
          }]?: isReadPermissionGranted
        }

        binding?.ivProfileUserImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@MyProfileActivity,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser()
            } else {
                requestPermission()
            }
        }

        binding?.btnUpdate?.setOnClickListener {
            if(mSelectedImageFileUri != null) {
                uploadUserImage()
            }else {
                showProgressDialog("Please wait...")
                updateUserProfileData()
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isReadPermissionGranted = ContextCompat.checkSelfPermission(
                this@MyProfileActivity,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        }

        val permissionRequest: MutableList<String> = ArrayList()

        if(!isReadPermissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }

        if(permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()
        var anyChanges = false

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChanges = true
        }

        if(binding?.etName?.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = binding?.etName?.text.toString()
            anyChanges = true
        }

        if(binding?.etMobile?.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString().toLong()
            anyChanges = true
        }

        if(anyChanges)  FireStoreClass().updateUserProfileData(this@MyProfileActivity,userHashMap)
    }

    private fun uploadUserImage() {
        showProgressDialog("Please wait...")

        if(mSelectedImageFileUri != null) {
            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child("USER_IMAGE" +
                        System.currentTimeMillis()
                        + "." +
                        getFileExtension(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i(
                        "Downloadable Image URL",
                        uri.toString()
                    )
                    mProfileImageURL = uri.toString()

                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))

    }
    private fun showImageChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data

            val myImageView: CircleImageView? = binding?.ivProfileUserImage
            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(myImageView!!)
            }catch (e: IOException) {
                e.printStackTrace()
            }

        }
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

        mUserDetails = user

        val myImageView: CircleImageView? = binding?.ivProfileUserImage

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

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}