import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit


object TokenManager {
    private const val REFRESH_TOKEN_KEY = "refresh_token"
    private const val ACCESS_TOKEN_KEY = "refresh_token"


    fun saveRefreshToken(context: Context, refreshToken: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit {
            putString(REFRESH_TOKEN_KEY, refreshToken)
        }
    }

    fun getRefreshToken(context: Context): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    fun saveAccessToken(context: Context, refreshToken: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit {
            putString(ACCESS_TOKEN_KEY, refreshToken)
        }
    }

    fun getAccessToken(context: Context): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    fun clearTokens(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit {
            remove(REFRESH_TOKEN_KEY)
            remove(ACCESS_TOKEN_KEY)
        }
    }

}