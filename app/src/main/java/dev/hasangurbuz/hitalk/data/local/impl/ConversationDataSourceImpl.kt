package dev.hasangurbuz.hitalk.data.local.impl

import android.util.Log
import dev.hasangurbuz.hitalk.data.local.ConversationDataSource
import dev.hasangurbuz.hitalk.data.local.entity.ConversationEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationDataSourceImpl
@Inject constructor(private val realmConfig: RealmConfiguration) : ConversationDataSource {

    override suspend fun updateAll(conversations: List<ConversationEntity>) {
         val realm = Realm.open(realmConfig)

        realm.write {
            for (conversation in conversations) {
                copyToRealm(conversation, UpdatePolicy.ALL)
            }

            realm.close()
        }

    }

    override suspend fun create(conversationId: String){
        val realm = Realm.open(realmConfig)

        realm.write {
            val entity = ConversationEntity()
            entity.id = conversationId
            copyToRealm(entity)
        }
        realm.close()
    }

    override suspend fun findById(conversationId: String): ConversationEntity {
        val realm = Realm.open(realmConfig)
        val found = realm.query<ConversationEntity>().find().first()

        realm.close()
        return found
    }

    override fun fetchall(): Flow<List<ConversationEntity>> {
        val realm = Realm.open(realmConfig)
        return realm.query<ConversationEntity>().find().asFlow().map {
            it.list.toList()
        }
    }


}