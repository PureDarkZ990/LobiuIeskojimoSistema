package com.example.lobiupaieskossistema

import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lobiupaieskossistema.adapters.FriendsAdapter
import com.example.lobiupaieskossistema.adapters.PendingRequestsAdapter
import com.example.lobiupaieskossistema.data.FriendshipData
import com.example.lobiupaieskossistema.data.UserData
import com.example.lobiupaieskossistema.models.FriendshipStatus
import com.example.lobiupaieskossistema.models.Friendship
import com.example.lobiupaieskossistema.models.User
import com.example.lobiupaieskossistema.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FriendshipActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var pendingRequestsRecyclerView: RecyclerView
    private lateinit var addFriendButton: Button
    private val currentUserId: Int by lazy { sessionManager.getUserId() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_friendship)

        sessionManager = SessionManager(this)
        initializeViews()

        // Initialize data
        FriendshipData.initialize(this)
        UserData.initialize(this)

        loadData()
    }

    private fun initializeViews() {
        friendsRecyclerView = findViewById(R.id.friendsRecyclerView)
        pendingRequestsRecyclerView = findViewById(R.id.pendingRequestsRecyclerView)
        addFriendButton = findViewById(R.id.addFriendButton)

        friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        pendingRequestsRecyclerView.layoutManager = LinearLayoutManager(this)

        addFriendButton.setOnClickListener {
            showAddFriendDialog()
        }
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val friends = loadFriendsData()
            val pendingRequests = loadPendingRequestsData()

            withContext(Dispatchers.Main) {
                updateFriendsList(friends)
                updatePendingRequestsList(pendingRequests)
            }
        }
    }

    private suspend fun loadFriendsData(): List<User> = withContext(Dispatchers.IO) {
        val friendships = FriendshipData.getFriendships(currentUserId)
            .filter { it.status == FriendshipStatus.ACCEPTED }

        friendships.mapNotNull { friendship ->
            val friendId = if (friendship.userId == currentUserId) friendship.friendId else friendship.userId
            UserData.get(friendId)
        }
    }

    private suspend fun loadPendingRequestsData(): List<User> = withContext(Dispatchers.IO) {
        val pendingRequests = FriendshipData.getPendingRequests(currentUserId)
        pendingRequests.mapNotNull { UserData.get(it.userId) }
    }

    private fun updateFriendsList(friends: List<User>) {
        friendsRecyclerView.adapter = FriendsAdapter(friends) { user, action ->
            when (action) {
                "remove" -> removeFriend(user)
                "block" -> blockFriend(user)
            }
        }
    }

    private fun updatePendingRequestsList(requesters: List<User>) {
        pendingRequestsRecyclerView.adapter = PendingRequestsAdapter(requesters) { user, action ->
            when (action) {
                "accept" -> acceptFriendRequest(user)
                "reject" -> rejectFriendRequest(user)
            }
        }
    }

    private fun showAddFriendDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_friend)

        val usernameInput = dialog.findViewById<EditText>(R.id.usernameInput)
        val addButton = dialog.findViewById<Button>(R.id.confirmAddFriendButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelAddFriendButton)

        addButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val username = usernameInput.text.toString()
                val user = UserData.getAll().find { it.username == username }

                withContext(Dispatchers.Main) {
                    when {
                        user == null -> Toast.makeText(this@FriendshipActivity, "User not found", Toast.LENGTH_SHORT).show()
                        user.id == currentUserId -> Toast.makeText(this@FriendshipActivity, "Cannot add yourself", Toast.LENGTH_SHORT).show()
                        FriendshipData.areFriends(currentUserId, user.id) ->
                            Toast.makeText(this@FriendshipActivity, "Already friends", Toast.LENGTH_SHORT).show()
                        else -> {
                            sendFriendRequest(user)
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

        cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun sendFriendRequest(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            val friendship = Friendship(
                userId = currentUserId,
                friendId = user.id,
                statusValue = FriendshipStatus.PENDING.value,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date())
            )
            FriendshipData.add(friendship)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@FriendshipActivity, "Friend request sent", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun acceptFriendRequest(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            val friendship = FriendshipData.getFriendships(currentUserId)
                .find { it.userId == user.id && it.friendId == currentUserId }

            friendship?.let {
                FriendshipData.acceptFriendRequest(it.id)
                loadData()
            }
        }
    }

    private fun rejectFriendRequest(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            val friendship = FriendshipData.getFriendships(currentUserId)
                .find { it.userId == user.id && it.friendId == currentUserId }

            friendship?.let {
                FriendshipData.rejectFriendRequest(it.id)
                loadData()
            }
        }
    }

    private fun removeFriend(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            val friendship = FriendshipData.getFriendships(currentUserId)
                .find { (it.userId == currentUserId && it.friendId == user.id) ||
                        (it.userId == user.id && it.friendId == currentUserId) }

            friendship?.let {
                FriendshipData.delete(it.id)
                loadData()
            }
        }
    }

    private fun blockFriend(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            val friendship = FriendshipData.getFriendships(currentUserId)
                .find { (it.userId == currentUserId && it.friendId == user.id) ||
                        (it.userId == user.id && it.friendId == currentUserId) }

            friendship?.let {
                FriendshipData.blockUser(it.id, currentUserId)
                loadData()
            }
        }
    }
}


