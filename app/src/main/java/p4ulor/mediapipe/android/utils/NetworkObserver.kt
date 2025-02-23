package p4ulor.mediapipe.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

/**
 * Allows receiving updates to the status of Wifi or Mobile Data connectivity via [hasConnection]
 * @param [context] is injected by Koin
 * todo, make so this doesnt run in background when minimizing app
 */
@Single
class NetworkObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val hasConnection = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                launch { send(true) }
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                launch { send(false) }
            }

            override fun onLost(network: Network) {
                launch { send(false) }
            }

            override fun onUnavailable() {
                launch { send(true) }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}
