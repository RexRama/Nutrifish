package com.org.capstone.nutrifish.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.org.capstone.nutrifish.R
import com.org.capstone.nutrifish.adapter.AllStoriesAdapter
import com.org.capstone.nutrifish.adapter.LoadingAdapter
import com.org.capstone.nutrifish.data.local.entity.FishEntity
import com.org.capstone.nutrifish.data.remote.model.UserModel
import com.org.capstone.nutrifish.data.remote.response.ListStoryItem
import com.org.capstone.nutrifish.databinding.FragmentProfileBinding
import com.org.capstone.nutrifish.utils.SettingPreferences
import com.org.capstone.nutrifish.utils.Utils
import com.org.capstone.nutrifish.utils.ViewModelFactory
import com.org.capstone.nutrifish.utils.dataStore


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var myStoryAdapter: AllStoriesAdapter
    private lateinit var userModel: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        setUI()
        setStory()

        setViewModel()
        return view
    }

    private fun setUI() {
        with(requireActivity()) {
            findViewById<TextView>(R.id.page_title).visibility = View.GONE
            "Profile".also { findViewById<TextView>(R.id.top_title).text = it }
            findViewById<FloatingActionButton>(R.id.bt_scan).visibility = View.VISIBLE
            findViewById<FloatingActionButton>(R.id.fab_postRecipe).visibility = View.VISIBLE
            findViewById<BottomNavigationView>(R.id.bottom_navbar).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.bt_back).visibility = View.GONE
        }
    }

    private fun setViewModel() {
        val dataStore = SettingPreferences.getInstance(requireContext().dataStore)
        val viewModelFactory = ViewModelFactory(requireContext().applicationContext, dataStore)
        profileViewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        profileViewModel.fetchMyStory().observe(viewLifecycleOwner) { pagingData ->
            myStoryAdapter.submitData(lifecycle, pagingData)
        }

        profileViewModel.getUser().observe(viewLifecycleOwner){ user ->
            userModel = user
            setView(userModel)
        }
    }


    private fun setStory() {
        myStoryAdapter = AllStoriesAdapter()
        binding.postRecipeRv.apply {
            layoutManager = LinearLayoutManager(requireContext().applicationContext)
            adapter = myStoryAdapter.withLoadStateFooter(
                footer = LoadingAdapter { myStoryAdapter.retry() }
            )
        }

        myStoryAdapter.addLoadStateListener { loadState ->
            binding.pbStories.isVisible = loadState.source.refresh is LoadState.Loading
            handleEmptyState(loadState)
        }

        myStoryAdapter.setOnItemClickCallback(object : Utils.OnItemClickCallback {
            override fun onFishClicked(data: FishEntity) {
                //Do nothing
            }

            override fun onPostClicked(data: ListStoryItem) {
                val showBack = requireActivity().findViewById<ImageButton>(R.id.bt_back)
                showBack.visibility = View.VISIBLE
                val bundle = Bundle().apply {
                    putParcelable(RECIPE_ITEM, data)
                }
                findNavController().navigate(
                    R.id.action_navigation_profile_to_navigation_detailPost, bundle
                )
            }

        })

    }

    private fun handleEmptyState(loadState: CombinedLoadStates) {
        val loading = loadState.refresh is LoadState.Loading
        val isEmpty = myStoryAdapter.itemCount == 0

        Log.d(
            "ProfileFragment",
            "handleEmptyState - isLoading: $loading, itemCount: ${myStoryAdapter.itemCount}"
        )

        Log.d(
            "ProfileFragment",
            "handleEmptyState - isEmpty: $isEmpty, itemCount: ${myStoryAdapter.itemCount}"
        )

        binding.postRecipeRv.isVisible = !isEmpty
        binding.emptyPost.visibility = if (isEmpty && !loading) View.VISIBLE else View.GONE


    }


    private fun setView(userModel: UserModel) {
        requireActivity().runOnUiThread {
            val user = userModel
            val username = user.username
            binding.tvProfileName.text = user.name
            if (username != null) {
                binding.tvProfileUsername.text =
                    if (username.contains("@")) "@" + username.substringBefore("@") else "@$username"

            }
            if (user.isGoogle) {
                Glide.with(requireContext().applicationContext)
                    .load(user.photoUrl)
                    .into(binding.cvImageProfile)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RECIPE_ITEM = "recipe_item"
    }

}