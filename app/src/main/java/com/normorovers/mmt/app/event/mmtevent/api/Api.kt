package com.normorovers.mmt.app.event.mmtevent.api

import android.app.Application
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.BaseCallback
import com.auth0.android.result.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Api(private val application: Application) {
    fun retrofit(authenticatedFunction: (Retrofit) -> Unit) {
        authenticate { accessToken ->

            val httpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

            httpClientBuilder.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .header("Authorization", bearer(accessToken))
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS)
            httpClientBuilder.readTimeout(1, TimeUnit.MINUTES)

            val httpClient = httpClientBuilder.build()

            val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.0.10:8082/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()

            authenticatedFunction(retrofit)
        }
    }

    private fun authenticate(authenticated: (accessToken: String) -> Unit) {
        val account = Auth0(application)
        val authentication = AuthenticationAPIClient(account)
        val storage = SharedPreferencesStorage(application)
        val credentialsManager = SecureCredentialsManager(application, authentication, storage)

        credentialsManager.getCredentials(object : BaseCallback<Credentials, CredentialsManagerException> {
            override fun onSuccess(credentials: Credentials) {
                val accToken = credentials.accessToken!!
                authenticated(accToken)
            }

            override fun onFailure(error: CredentialsManagerException) {
                throw ApiUnauthorized
            }
        })
    }

    private fun bearer(accessToken: String): String {
        return "Bearer $accessToken"
    }
}