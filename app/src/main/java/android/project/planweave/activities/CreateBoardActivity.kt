package android.project.planweave.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.project.planweave.R
import android.project.planweave.databinding.ActivityCreateBoardBinding
import android.project.planweave.firebase.FireStoreClass
import android.project.planweave.models.Board
import android.project.planweave.utils.Constants
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var binding: ActivityCreateBoardBinding? = null

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var mBoardImageURL: String = ""
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserName: String


    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
        if(intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        binding?.ivBoardImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@CreateBoardActivity,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser()
            } else {
                requestPermission()
            }
        }

        binding?.btnCreate?.setOnClickListener {
            if(mSelectedImageFileUri != null) {
                uploadBoardImage()
            }else {
                showProgressDialog("Please wait...")
                createBoard()
            }
        }
    }

    private fun createBoard() {
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())

        var board = Board(
            binding?.etBoardName?.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FireStoreClass().createBoard(this@CreateBoardActivity, board)
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))

    }

    private fun uploadBoardImage() {
        showProgressDialog("Please wait...")

        if(mSelectedImageFileUri != null) {
            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child("BOARD_IMAGE" +
                        System.currentTimeMillis()
                        + "." +
                        getFileExtension(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i(
                    "Board Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i(
                        "Downloadable Image URL",
                        uri.toString()
                    )
                    mBoardImageURL = uri.toString()

                    createBoard()
                }
            }.addOnFailureListener {
                    exception ->
                Toast.makeText(
                    this@CreateBoardActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
        }
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data

            val myImageView: CircleImageView? = binding?.ivBoardImage
            try {
                Glide
                    .with(this@CreateBoardActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(myImageView!!)
            }catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isReadPermissionGranted = ContextCompat.checkSelfPermission(
                this@CreateBoardActivity,
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

    private fun showImageChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.tbCreateBoardActivity)
        if(supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_white)
            supportActionBar!!.title = "Create Board"
        }

        binding?.tbCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}