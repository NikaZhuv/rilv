package com.example.rilv

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.rilv.databinding.ActivityMainBinding
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.navOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    navController.navigate(
                        R.id.homeFragment,
                        null,
                        navOptions {
                            popUpTo(R.id.nav_graph_xml) { inclusive = false }
                            launchSingleTop = true
                        }
                    )
                    true
                }

                R.id.trendsFragment -> {
                    navController.navigate(
                        R.id.trendsFragment,
                        null,
                        navOptions {
                            popUpTo(R.id.nav_graph_xml) { inclusive = false }
                            launchSingleTop = true
                        }
                    )
                    true
                }

                R.id.watchlistsFragment -> {
                    navController.navigate(
                        R.id.watchlistsFragment,
                        null,
                        navOptions {
                            // очистить вложенный стек
                            popUpTo(R.id.watchlistsFragment) { inclusive = true }
                            launchSingleTop = true
                        }
                    )
                    true
                }

                R.id.profileFragment -> {
                    navController.navigate(
                        R.id.profileFragment,
                        null,
                        navOptions {
                            popUpTo(R.id.nav_graph_xml) { inclusive = true }
                            launchSingleTop = true
                        }
                    )
                    true
                }

                else -> false
            }
        }


        // іконки статусбара
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        // Insets для всього кореня
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, sb.top, 0, 0)
            WindowInsetsCompat.CONSUMED
        }
    }

}