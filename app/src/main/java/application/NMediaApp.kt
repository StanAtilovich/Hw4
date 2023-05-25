package application

import android.app.Application
import auth.AppAuth

class NMediaApp: Application() {
    override fun onCreate(){
        super.onCreate()
        AppAuth.init(this)
    }
}