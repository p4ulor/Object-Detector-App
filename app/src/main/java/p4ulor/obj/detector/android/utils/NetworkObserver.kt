package p4ulor.obj.detector.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.annotation.Single
import p4ulor.obj.detector.i

/**
 * Allows receiving updates of the status of Wifi or Mobile Data connectivity via [hasConnection]
 * @param [context] is injected by Koin. [trySendBlocking] is not used since none of these operations
 * are highly repetitive in a short amount of time and we don't want it to block
 */
@Single
class NetworkObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val hasConnection = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onUnavailable() {
                trySend(false)
            }
        }

        // Alternative to not using this is to just use registerDefaultNetworkCallback but we cool
        val networkTypesToObserve = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkTypesToObserve, callback)

        awaitClose {
            i("NetworkObserver closed hasConnection flow")
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}
