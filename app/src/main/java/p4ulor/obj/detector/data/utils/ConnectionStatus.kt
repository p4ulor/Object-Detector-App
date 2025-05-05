package p4ulor.obj.detector.data.utils

/** Defines the status of network (internet or mobile) connection */
enum class ConnectionStatus {
    On,
    Off,
    Disconnected;

    val isEnabled get() = this == On
    val isDisconnected get() = this == Disconnected
}
