package com.dpfht.demofbasemvvm

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dpfht.demofbasemvvm.databinding.ActivityMainBinding
import com.dpfht.demofbasemvvm.framework.R as frameworkR
import com.dpfht.demofbasemvvm.navigation.R as navigationR
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  companion object {
    lateinit var instance: MainActivity
  }

  private lateinit var binding: ActivityMainBinding
  private lateinit var navController: NavController

  private var isAlreadyEnteredHome = false

  override fun onCreate(savedInstanceState: Bundle?) {
    instance = this
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val navHostFragment = supportFragmentManager.findFragmentById(frameworkR.id.nav_host_fragment) as NavHostFragment
    navController = navHostFragment.navController

    navController.addOnDestinationChangedListener { _, destination, _ ->
      when (destination.id) {
        navigationR.id.loginFragment -> {
          binding.bottomNav.visibility = View.GONE
          isAlreadyEnteredHome = false
        }
        navigationR.id.bookListFragment -> {
          if (!isAlreadyEnteredHome) {
            binding.bottomNav.setupWithNavController(navController)
            binding.bottomNav.visibility = View.VISIBLE
            isAlreadyEnteredHome = true
          }
        }
      }
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    return navController.navigateUp() || super.onSupportNavigateUp()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menu?.let {
      MenuCompat.setGroupDividerEnabled(menu, true)
      menuInflater.inflate(R.menu.main_menu, menu)
    }

    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.itm_generate_crash -> {
        generateCrash()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun generateCrash() {
    throw RuntimeException("Generated Crash")
  }
}
