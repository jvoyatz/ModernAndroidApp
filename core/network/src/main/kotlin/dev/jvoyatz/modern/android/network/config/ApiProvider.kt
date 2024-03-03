package dev.jvoyatz.modern.android.network.config

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

/**
 * The contract used to create the implementation of the Api Endpoints
 * found in the service interface
 */
interface ApiProvider {
    fun <T> getApi(clazz: Class<T>): T
}

/**
 * This class is responsible for the construction of the http engine
 * used in this app.
 *
 * It constructs the engine the http client [Retrofit], which is used to create the instances of the interfaces
 * used to connect with certain endpoints in a remote server.
 *
 * It also created an instance of [OkHttpClient] used by [Retrofit] to create http sockets as well as
 * an instance of the mechanism used to deserialize the payload received into Java/Kotlin objets
 */
internal object ApiProviderImpl : ApiProvider {
    private const val TIMEOUT = 10L
    private const val URL = ""

    private val loggingInterceptor by lazy(mode = LazyThreadSafetyMode.NONE) {
        HttpLoggingInterceptor {
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            readTimeout(TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(TIMEOUT, TimeUnit.SECONDS)

            addInterceptor(loggingInterceptor)
        }.build()
    }


    private val retrofit: Retrofit by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Retrofit.Builder().apply {
            baseUrl(URL)
            addConverterFactory(MoshiConverterFactory.create(moshi))
            client(okHttpClient)
        }.build()
    }

    override fun <T> getApi(clazz: Class<T>): T = retrofit.create(clazz)

    @Deprecated("will not be used for now", replaceWith = ReplaceWith("getApi(clazz: Class<T>)"))
    inline fun <reified T> getApi(): T = retrofit.create<T>()
}