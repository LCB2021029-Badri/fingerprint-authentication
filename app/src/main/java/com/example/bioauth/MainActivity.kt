package com.example.bioauth

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.bioauth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var cancellationSignal: CancellationSignal? = null

    //while authentication (during the time of authentication)
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback get() =
        @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback(){
            //if auth failed
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                notifyUser("Authentication error: $errString :(")
            }
            //if auth success
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                notifyUser("Authentication Success :)")
                //what to do if success
                startActivity(Intent(this@MainActivity,SecondActivity::class.java))
            }
        }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //checking of biometeric eists in the system or not
        checkBiometricSupport()

        //setting thebutton\
        binding.btnAuthenticationb.setOnClickListener {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Secret")
                .setSubtitle("Authentication is required")
                .setDescription("This app uses fingerprint protection to keep your data secure")
                .setNegativeButton("cancel",this.mainExecutor,DialogInterface.OnClickListener { dialogInterface, i ->
                    notifyUser("Authentication cancelled")
                }).build()

            biometricPrompt.authenticate(getCancellationSignal(),mainExecutor,authenticationCallback)
        }

    }

    //checking boimetric support
    private fun checkBiometricSupport():Boolean{
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if(!keyguardManager.isKeyguardSecure){
            notifyUser("FingePrint authentication is not enabled")
            return false
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            return true
        }
        else{
            return true
        }
    }

    //on cancelled
    private fun getCancellationSignal() : CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    //just simple toast
    private fun notifyUser(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

}