package p4ulor.mediapipe

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

/**
 * Dependencies will be managed with annotations and processed with [KSP](https://kotlinlang.org/docs/ksp-quickstart.html#add-a-processor)
 * Which makes up a simpler setup
 * https://insert-koin.io/docs/setup/annotations
 * https://insert-koin.io/docs/reference/koin-annotations/start/
 * https://insert-koin.io/docs/quickstart/android-annotations/
 * https://insert-koin.io/docs/quickstart/android-compose/
 * https://github.com/InsertKoinIO/koin-getting-started/tree/main/android-annotations
 */
@Module
@ComponentScan("p4ulor.mediapipe")
class DependencyInjectionScanner