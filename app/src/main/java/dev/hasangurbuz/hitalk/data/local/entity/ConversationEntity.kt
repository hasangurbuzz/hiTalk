package dev.hasangurbuz.hitalk.data.local.entity

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ConversationEntity() : RealmObject {
    @PrimaryKey
    var id: String? = null
    var lastMessageId: String? = null
    var participants: RealmList<String> = realmListOf()
}