package eu.kanade.tachiyomi.di

import eu.kanade.domain.domainModule
import eu.kanade.tachiyomi.network.NetworkHelper
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
        }
    }

    single {
        XML {
            defaultPolicy {
                ignoreUnknownChildren()
            }
            autoPolymorphic = true
            xmlDeclMode = XmlDeclMode.Charset
            indent = 2
            xmlVersion = XmlVersion.XML10
        }
    }

    single { kotlinx.serialization.protobuf.ProtoBuf }

    single { NetworkHelper(androidApplication(), get()) }
}

val koinModules = listOf(
    appModule,
    dataModule,
    domainModule,
    preferenceModule,
    screenModelModule,
)
