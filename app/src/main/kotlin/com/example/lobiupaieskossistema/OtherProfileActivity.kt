import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.database.RoleTable
import com.example.lobiupaieskossistema.database.UserTable
import com.example.lobiupaieskossistema.utils.SessionManager

class OtherProfileActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var deleteAccountButton: Button
    private lateinit var sessionManager: SessionManager
    private var userIdToDelete: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.other_profile)

        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        userIdToDelete = intent.getIntExtra("USER_ID", -1)
        if (userIdToDelete == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserProfile(userIdToDelete)

        // Check if the current user is an admin
        if (sessionManager.getRole().toString() == RoleTable.ADMIN_ROLE) {
            deleteAccountButton.visibility = View.VISIBLE
            deleteAccountButton.setOnClickListener {
                showDeleteAccountConfirmationDialog()
            }
        } else {
            deleteAccountButton.visibility = View.GONE
        }
    }

    private fun loadUserProfile(userId: Int) {
        val db = databaseHelper.readableDatabase
        // Query to get user details
        val cursor = db.query(
            UserTable.TABLE_NAME,
            arrayOf("username", "bio", "email"),
            "id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            // Populate the UI with the user details
            findViewById<TextView>(R.id.otherUserName).text = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            findViewById<TextView>(R.id.otherBio).text = cursor.getString(cursor.getColumnIndexOrThrow("bio"))
            findViewById<TextView>(R.id.otherEmail).text = cursor.getString(cursor.getColumnIndexOrThrow("email"))
        }

        cursor.close()
        db.close()
    }

    private fun showDeleteAccountConfirmationDialog() {
        val confirmationDialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.deletion_confirmation, null)
        confirmationDialog.setContentView(dialogView)
        confirmationDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val confirmButton = dialogView.findViewById<Button>(R.id.confirmDeletionButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        confirmButton.setOnClickListener {
            deleteAccount()
            confirmationDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            confirmationDialog.dismiss()
        }

        confirmationDialog.show()
    }

    private fun deleteAccount() {
        val db = databaseHelper.writableDatabase
        db.delete(UserTable.TABLE_NAME, "${UserTable.ID} = ?", arrayOf(userIdToDelete.toString()))
        db.close()

        Toast.makeText(this, "User account deleted successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}
