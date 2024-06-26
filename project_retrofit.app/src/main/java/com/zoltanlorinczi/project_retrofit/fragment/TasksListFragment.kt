package com.zoltanlorinczi.project_retrofit.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zoltanlorinczi.project_retorfit.R
import com.zoltanlorinczi.project_retorfit.databinding.FragmentTasksListBinding
import com.zoltanlorinczi.project_retrofit.adapter.TasksListAdapter
import com.zoltanlorinczi.project_retrofit.api.ThreeTrackerRepository
import com.zoltanlorinczi.project_retrofit.api.model.TaskResponse
import com.zoltanlorinczi.project_retrofit.viewmodel.TasksViewModel
import com.zoltanlorinczi.project_retrofit.viewmodel.TasksViewModelFactory

/**
 * Author:  Zoltan Lorinczi
 * Date:    12/2/2021
 */
class TasksListFragment : Fragment(R.layout.fragment_tasks_list), TasksListAdapter.OnItemClickListener,
    TasksListAdapter.OnItemLongClickListener {

    companion object {
        private val TAG: String = javaClass.simpleName
    }
    private lateinit var binding: FragmentTasksListBinding;
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TasksListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = TasksViewModelFactory(ThreeTrackerRepository())
        tasksViewModel = ViewModelProvider(requireActivity(), factory)[TasksViewModel::class.java]
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val navBar = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)

        navBar?.visibility = View.VISIBLE;


        val view = inflater.inflate(R.layout.fragment_tasks_list, container, false)
        binding = FragmentTasksListBinding.inflate(inflater);
        recyclerView = binding.recyclerView;
        setupRecyclerView()
        binding.preloader.visibility = View.VISIBLE;

        Handler().postDelayed({
            tasksViewModel.getTasks()
        },
        5000
        )


        tasksViewModel.products.observe(viewLifecycleOwner) {
            binding.preloader.visibility = View.GONE;
            adapter.setData(tasksViewModel.products.value as ArrayList<TaskResponse>)
            adapter.notifyDataSetChanged()
        }

        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.newTaskFragment);
        }

        return binding.root;
    }

    private fun setupRecyclerView() {
        adapter = TasksListAdapter(ArrayList(), requireActivity(), this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.addItemDecoration(
                DividerItemDecoration(
                        activity,
                        DividerItemDecoration.VERTICAL
                )
        )
        recyclerView.setHasFixedSize(true)
    }

    override fun onItemClick(position: Int) {
        tasksViewModel.ID = position;
        findNavController().navigate(R.id.taskFragment);
    }

    override fun onItemLongClick(position: Int) {
    }

    override fun onResume() {
        tasksViewModel.getTasks();
        super.onResume()
    }
}