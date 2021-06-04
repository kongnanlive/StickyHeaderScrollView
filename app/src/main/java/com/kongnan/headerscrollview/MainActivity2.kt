package com.kongnan.headerscrollview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kongnan.headerscrollview.ui.main.PageViewModel
import com.kongnan.headerscrollview.ui.main.SectionsPagerAdapter

class MainActivity2 : AppCompatActivity() {

    private val pageViewModel: PageViewModel by viewModels()

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = sectionsPagerAdapter.getPageTitle(position)
        }.attach()
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            findViewById<View>(R.id.remove_view)?.let {
                (it.parent as ViewGroup).removeView(it)
            }
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout).also {
            swipeRefreshLayout = it
            swipeRefreshLayout.setOnRefreshListener {
                pageViewModel.isRefreshing.value = true
            }
        }
        pageViewModel.isRefreshing.observe(this) {
            if (it == false) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}