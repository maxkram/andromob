package ru.school21.tictactoe.data.remote

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import ru.school21.tictactoe.data.local.dao.CurrentUserDao
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val currentUserDao: CurrentUserDao
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        // signup/signin обрабатывают credentials сами
        if (path.startsWith("/auth/")) {
            return chain.proceed(request)
        }

        val user = try {
            currentUserDao.getCurrent().blockingGet()
        } catch (e: Exception) {
            null
        }

        if (user == null) return chain.proceed(request)

        val credentials = Base64.encodeToString(
            "${user.login}:${user.password}".toByteArray(),
            Base64.NO_WRAP
        )
        val newRequest = request.newBuilder()
            .header("Authorization", "Basic $credentials")
            .build()
        return chain.proceed(newRequest)
    }
}