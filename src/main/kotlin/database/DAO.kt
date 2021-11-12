package database

import entity.Account
import entity.Exchange
import org.sql2o.Sql2o


/**
 * created by Reslan
 * lεροrεm-cοδε
 * 2019-12-06
 */

object DAO {
    private val sql2o = Sql2o("jdbc:sqlite:registry.db", null, null)

    fun init() {
        sql2o.open().use {
            it.createQuery("CREATE TABLE IF NOT EXISTS accounts (id INTEGER PRIMARY KEY, name TEXT NOT NULL, balance REAL DEFAULT 0.0)")
                .executeUpdate()
            it.createQuery("CREATE TABLE IF NOT EXISTS exchanges (id INTEGER PRIMARY KEY, senderId INTEGER NOT NULL, receiverId INTEGER NOT NULL, amount REAL NOT NULL CHECK(amount > 0), reason TEXT)")
                .executeUpdate()
        }
    }

    fun getAccount(id: Int): Account? {
        val result = sql2o.open().use {
            it.createQuery("SELECT * FROM accounts WHERE id = :id")
                .addParameter("id", id)
                .executeAndFetch(Account::class.java)
        }
        return if (!result.isNullOrEmpty()) {
            println("result found : ${result[0]}")
            result[0]
        } else {
            null
        }
    }

    fun getAccount(name: String): Account? {
        val result = sql2o.open().use {
            it.createQuery("SELECT * FROM accounts WHERE name = :name")
                .addParameter("name", name)
                .executeAndFetch(Account::class.java)
        }
        return if (!result.isNullOrEmpty()) {
            println("result found : ${result[0]}")
            result[0]
        } else {
            null
        }
    }

    fun addExchange(receiverId: Int, exchange: Exchange) {
        sql2o.open().use {
            it.createQuery("INSERT INTO exchanges (senderId, receiverId, amount, reason) VALUES (:senderId, :receiverId, :amount, :reason)")
                .addParameter("senderId", exchange.senderId)
                .addParameter("receiverId", receiverId)
                .addParameter("amount", exchange.amount)
                .addParameter("reason", exchange.reason)
                .executeUpdate()
        }
    }

    fun updateAccount(account: Account) {
        sql2o.open().use {
            it.createQuery("UPDATE accounts SET balance = :balance WHERE id = :id")
                .addParameter("balance", account.balance)
                .addParameter("id", account.id)
                .executeUpdate()
        }
    }
}