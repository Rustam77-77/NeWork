package ru.netology.nework.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.netology.nework.R
import ru.netology.nework.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
        // ВАЖНО: Устанавливаем Toolbar как ActionBar
        setSupportActionBar(binding.toolbar)

        // Проверяем, что ActionBar успешно установлен
        if (supportActionBar == null) {
            throw IllegalStateException("ActionBar not set - check your theme and toolbar setup")
        }

=======
        // Устанавливаем Toolbar как ActionBar
        setSupportActionBar(binding.toolbar)

>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Настраиваем AppBarConfiguration для верхних уровней навигации
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_posts,
                R.id.navigation_events,
                R.id.navigation_users
            )
        )

<<<<<<< HEAD
        // Теперь это будет работать, так как ActionBar установлен
=======
        // Настраиваем ActionBar с NavController
>>>>>>> cb2f32b5efd911f0149b6369bdbce6453490a399
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Настраиваем BottomNavigationView с NavController
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}