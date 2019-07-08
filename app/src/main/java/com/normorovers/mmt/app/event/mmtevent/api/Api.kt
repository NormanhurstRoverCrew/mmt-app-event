package com.normorovers.mmt.app.event.mmtevent.api

import android.app.Application
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.BaseCallback
import com.auth0.android.management.UsersAPIClient
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.doAsyncResult
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


class Api(private var application: Application) {
	private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
	private var baseUrl = firebaseRemoteConfig.getString("api_url")

	private lateinit var accessToken: String

	init {
		authenticate {
			this.accessToken = it
		}
	}

	class RetrofitDateSerializer : JsonSerializer<OffsetDateTime> {
		private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
		override fun serialize(srcDate: OffsetDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
			if (srcDate == null)
				return null

			val formatted = srcDate.format(formatter)
			return JsonPrimitive(formatted)
		}
	}

	private fun buildGsonConverterFactory(): GsonConverterFactory {
		val gsonBuilder = GsonBuilder()
		// Custom DATE Converter for Retrofit
		gsonBuilder.registerTypeAdapter(OffsetDateTime::class.java, RetrofitDateSerializer())
		return GsonConverterFactory.create(gsonBuilder.create())
	}

	fun retrofit(): Retrofit {
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

		return Retrofit.Builder()
				.baseUrl("$baseUrl/api/")
				.addConverterFactory(buildGsonConverterFactory())
				.client(httpClient)
				.build()
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

	fun getUser(): Future<UserProfile> {
		val account = Auth0(application)
		val usersClient = UsersAPIClient(account, accessToken)
		val authentication = AuthenticationAPIClient(account)
		return doAsyncResult {
			return@doAsyncResult authentication.userInfo(accessToken).execute()
		}
	}

	private fun bearer(accessToken: String): String {
		return "Bearer $accessToken"
	}
}