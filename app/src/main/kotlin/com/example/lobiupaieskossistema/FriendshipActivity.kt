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
import com.example.lobiupaieskossistema.adapters.BlockedUsersAdapter
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
    private lateinit var blockedUsersButton: Button
    private lateinit var sortByPointsButton: Button
    private var currentFriends: MutableList<User> = mutableListOf()
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
        blockedUsersButton = findViewById(R.id.blockedUsersButton)
        sortByPointsButton = findViewById(R.id.sortByPointsButton)

        friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        pendingRequestsRecyclerView.layoutManager = LinearLayoutManager(this)

        addFriendButton.setOnClickListener {
            showAddFriendDialog()
        }
        blockedUsersButton.setOnClickListener {
            showBlockedUsers()
        }

        sortByPointsButton = findViewById(R.id.sortByPointsButton)
        sortByPointsButton.setOnClickListener {
            showSortDialog()
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
            val friendId =
                if (friendship.userId == currentUserId) friendship.friendId else friendship.userId
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
                        user == null -> Toast.makeText(
                            this@FriendshipActivity,
                            "User not found",
                            Toast.LENGTH_SHORT
                        ).show()

                        user.id == currentUserId -> Toast.makeText(
                            this@FriendshipActivity,
                            "Cannot add yourself",
                            Toast.LENGTH_SHORT
                        ).show()

                        FriendshipData.areFriends(currentUserId, user.id) ->
                            Toast.makeText(
                                this@FriendshipActivity,
                                "Already friends",
                                Toast.LENGTH_SHORT
                            ).show()

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
                Toast.makeText(this@FriendshipActivity, "Friend request sent", Toast.LENGTH_SHORT)
                    .show()
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
                .find {
                    (it.userId == currentUserId && it.friendId == user.id) ||
                            (it.userId == user.id && it.friendId == currentUserId)
                }

            friendship?.let {
                FriendshipData.delete(it.id)
                loadData()
            }
        }
    }

    private fun blockFriend(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            val friendship = FriendshipData.getFriendships(currentUserId)
                .find {
                    (it.userId == currentUserId && it.friendId == user.id) ||
                            (it.userId == user.id && it.friendId == currentUserId)
                }

            friendship?.let {
                FriendshipData.blockUser(it.id, currentUserId)
                loadData()
            }
        }
    }

    private fun showBlockedUsers() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_blocked_users)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.blockedUsersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val blockedFriendships = FriendshipData.getFriendships(currentUserId)
            .filter { it.status == FriendshipStatus.BLOCKED }

        val blockedUsers = blockedFriendships.map { friendship ->
            val blockedUserId = if (friendship.userId == currentUserId) friendship.friendId else friendship.userId
            UserData.get(blockedUserId)!!
        }

        recyclerView.adapter = BlockedUsersAdapter(blockedUsers) { user ->
            val friendship = FriendshipData.getFriendships(currentUserId)
                .find { (it.userId == currentUserId && it.friendId == user.id) ||
                        (it.userId == user.id && it.friendId == currentUserId) }

            friendship?.let {
                FriendshipData.unblockUser(it.id)
                dialog.dismiss()
                loadFriends()
            }
        }

        dialog.show()
    }

    private fun showSortDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_sort_friends)

        val sortByPoints = dialog.findViewById<CheckBox>(R.id.sortByPoints)
        val sortByName = dialog.findViewById<CheckBox>(R.id.sortByName)
        val sortByDate = dialog.findViewById<CheckBox>(R.id.sortByDate)
        val applyButton = dialog.findViewById<Button>(R.id.applySortButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelSortButton)

        applyButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val sortCriteria = mutableListOf<String>()
                if (sortByPoints.isChecked) sortCriteria.add("points")
                if (sortByName.isChecked) sortCriteria.add("name")
                if (sortByDate.isChecked) sortCriteria.add("date")

                // Pirma įkeliam draugus
                val friendships = FriendshipData.getFriendships(currentUserId)
                    .filter { it.status == FriendshipStatus.ACCEPTED }

                currentFriends = friendships.mapNotNull { friendship ->
                    val friendId = if (friendship.userId == currentUserId) friendship.friendId else friendship.userId
                    UserData.get(friendId)
                }.toMutableList()

                // Tada rūšiuojam
                sortFriends(sortCriteria)

                withContext(Dispatchers.Main) {
                    friendsRecyclerView.adapter = FriendsAdapter(currentFriends) { user, action ->
                        when (action) {
                            "remove" -> removeFriend(user)
                            "block" -> blockFriend(user)
                        }
                    }
                    dialog.dismiss()
                }
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sortFriends(criteria: List<String>) {
        if (criteria.isEmpty()) return

        for (i in 0 until currentFriends.size - 1) {
            for (j in 0 until currentFriends.size - i - 1) {
                if (shouldSwap(currentFriends[j], currentFriends[j + 1], criteria)) {
                    val temp = currentFriends[j]
                    currentFriends[j] = currentFriends[j + 1]
                    currentFriends[j + 1] = temp
                }
            }
        }
    }

    private fun shouldSwap(user1: User, user2: User, criteria: List<String>): Boolean {
        for (criterion in criteria) {
            when (criterion) {
                "points" -> {
                    val points1 = user1.points ?: 0
                    val points2 = user2.points ?: 0
                    if (points1 != points2) {
                        return points1 < points2
                    }
                }
                "name" -> {
                    val nameComparison = user1.username.compareTo(user2.username)
                    if (nameComparison != 0) {
                        return nameComparison > 0
                    }
                }
                "date" -> {
                    val friendship1 = FriendshipData.getFriendships(currentUserId)
                        .find { it.userId == user1.id || it.friendId == user1.id }
                    val friendship2 = FriendshipData.getFriendships(currentUserId)
                        .find { it.userId == user2.id || it.friendId == user2.id }

                    if (friendship1 != null && friendship2 != null) {
                        try {
                            val date1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .parse(friendship1.createdAt)
                            val date2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .parse(friendship2.createdAt)

                            if (date1 != null && date2 != null && date1 != date2) {
                                return date1.before(date2)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        return false
    }


    private fun loadFriends() {
        val friendships = FriendshipData.getFriendships(currentUserId)
            .filter { it.status == FriendshipStatus.ACCEPTED }

        currentFriends = friendships.map { friendship ->
            val friendId = if (friendship.userId == currentUserId) friendship.friendId else friendship.userId
            UserData.get(friendId)!!
        }.toMutableList()

        friendsRecyclerView.adapter = FriendsAdapter(currentFriends) { user, action ->
            when (action) {
                "remove" -> removeFriend(user)
                "block" -> blockFriend(user)
            }
        }
    }
}


