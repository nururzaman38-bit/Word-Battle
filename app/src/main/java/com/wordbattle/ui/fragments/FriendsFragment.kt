package com.wordbattle.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.FragmentFriendsBinding
import com.wordbattle.ui.adapters.FriendAdapter

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPrefs: UserPrefsManager
    private lateinit var adapter: FriendAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPrefs = UserPrefsManager(requireContext())
        setupFriendsList()
        setupSearch()
        setupAddFriend()
    }

    private fun setupFriendsList() {
        // Sample data - in real app, load from Firebase
        val sampleFriends = listOf(
            FriendData("1", "John", true, 1250, 0xFFFF4E4E.toLong()),
            FriendData("2", "Sarah", true, 980, 0xFF00C9A7.toLong()),
            FriendData("3", "Mike", false, 870, 0xFF4C6FFF.toLong()),
            FriendData("4", "Emma", true, 720, 0xFFFFC93C.toLong())
        )

        adapter = FriendAdapter(sampleFriends, onInviteClick = { friend ->
            inviteFriend(friend)
        })

        binding.rvFriends.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FriendsFragment.adapter
        }

        if (sampleFriends.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvFriends.visibility = View.GONE
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
            }
        })
    }

    private fun setupAddFriend() {
        binding.btnAddFriend.setOnClickListener {
            // Show add friend dialog
        }
    }

    private fun inviteFriend(friend: FriendData) {
        if (!friend.isOnline) {
            com.wordbattle.utils.Utils.showToast(
                requireContext(),
                "Friend is offline",
                com.wordbattle.utils.ToastType.WARNING
            )
            return
        }

        com.wordbattle.utils.Utils.showToast(
            requireContext(),
            "Invite sent to ${friend.name}!",
            com.wordbattle.utils.ToastType.SUCCESS
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class FriendData(
        val id: String,
        val name: String,
        val isOnline: Boolean,
        val score: Int,
        val avatarColor: Long
    )
}
