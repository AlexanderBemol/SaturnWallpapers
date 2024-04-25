package com.amontdevs.saturnwallpapers.model

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID

class SaturnPhoto(
    var id: RealmUUID = RealmUUID.random(),
    var date: RealmInstant,
    var title: String,
    var description: String,
    var authors: String,
    var mediaType: String,
    var regularPath: String,
    var highDefinitionPath: String,
    var videoUrl: String,
    var isFavorite: Boolean
) : RealmObject {
    constructor(): this(
        RealmUUID.random(),
        RealmInstant.now(),
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        false)
}
