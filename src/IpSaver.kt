package com.battleship

import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import java.time.Instant
import java.time.format.DateTimeFormatter

data class IpEntry(val ip: String, val timestamp: String)

object IpSaver {

    private val ip_collection: MongoCollection<IpEntry>
    private val blockedAddresses: HashSet<String>

    init {
        val uri = MongoClientURI("mongodb://127.0.0.1:27017")
        val client = KMongo.createClient(uri)
        val database = client.getDatabase("SchiffeVersenken")
        ip_collection = database.getCollection<IpEntry>()
        blockedAddresses = hashSetOf("92.210.29.28")
    }

    fun isBlocked(ip:String) = ip in blockedAddresses

    fun addIp(address : String){
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        ip_collection.insertOne(IpEntry(address, timestamp))
    }

}