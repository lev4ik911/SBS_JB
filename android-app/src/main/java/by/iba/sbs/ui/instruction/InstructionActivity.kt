package by.iba.sbs.ui.instruction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import by.iba.sbs.R

class InstructionActivity : AppCompatActivity() {
    lateinit var  navController:NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instruction_activity)
        navController = findNavController(R.id.fragment_navigation_instruction)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_instruction_view,
//                R.id.navigation_instruction_edit
//            )
//        )

      //  setupActionBarWithNavController(navController, appBarConfiguration)
    }
    fun callInstructionEditor(){
        navController.navigate(R.id.navigation_instruction_edit)
    }

}
