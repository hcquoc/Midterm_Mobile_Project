package com.example.thecodecup.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.thecodecup.data.local.dao.CartDao
import com.example.thecodecup.data.local.dao.CoffeeDao
import com.example.thecodecup.data.local.dao.OrderDao
import com.example.thecodecup.data.local.dao.UserDao
import com.example.thecodecup.data.local.entity.CartItemEntity
import com.example.thecodecup.data.local.entity.CoffeeEntity
import com.example.thecodecup.data.local.entity.OrderEntity
import com.example.thecodecup.data.local.entity.OrderItemEntity
import com.example.thecodecup.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database for The Code Cup app
 */
@Database(
    entities = [
        CoffeeEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        UserEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coffeeDao(): CoffeeDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "the_code_cup_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get database instance (Singleton pattern)
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
        }

        /**
         * Callback to pre-populate database with initial data
         */
        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.coffeeDao())
                    }
                }
            }
        }

        /**
         * Pre-populate coffee menu with ratings
         * Prices in VND (Vietnamese Dong) - based on Vietnam market 2024-2025
         */
        private suspend fun populateInitialData(coffeeDao: CoffeeDao) {
            val initialCoffees = listOf(
                // COFFEE Category - Giá tham khảo thị trường VN
                CoffeeEntity(1, "Americano", 35000.0, "Classic espresso with hot water", "coffee_americano", "COFFEE", 4.8, 245),
                CoffeeEntity(2, "Cappuccino", 45000.0, "Espresso with steamed milk foam", "coffee_capuchino", "COFFEE", 4.9, 312),
                CoffeeEntity(3, "Latte", 45000.0, "Espresso with lots of steamed milk", "coffee_latte", "COFFEE", 4.7, 189),
                CoffeeEntity(4, "Espresso", 30000.0, "Strong concentrated coffee", "coffee_espresso", "COFFEE", 4.6, 156),
                CoffeeEntity(5, "Mocha", 55000.0, "Espresso with chocolate and steamed milk", "coffee_mocha", "COFFEE", 4.8, 278),
                CoffeeEntity(6, "Cà phê Đen", 25000.0, "Vietnamese black coffee", "coffee_dendavietnam", "COFFEE", 4.5, 98),
                CoffeeEntity(7, "Cà phê Sữa", 29000.0, "Vietnamese coffee with condensed milk", "coffee_suadavietnam", "COFFEE", 4.7, 167),
                CoffeeEntity(8, "Bạc Sỉu", 29000.0, "Vietnamese white coffee", "coffee_bacsiu", "COFFEE", 4.4, 87),

                // TEA Category
                CoffeeEntity(9, "Trà Đào", 39000.0, "Refreshing peach tea", "tea_tradao", "TEA", 4.6, 134),
                CoffeeEntity(10, "Trà Đào Cam Sả", 45000.0, "Peach tea with orange and lemongrass", "tea_tradaocamsa", "TEA", 4.8, 201),
                CoffeeEntity(11, "Trà Vải", 39000.0, "Sweet lychee tea", "tea_travai", "TEA", 4.5, 112),
                CoffeeEntity(12, "Matcha Latte", 55000.0, "Green tea with steamed milk", "tea_matchalatte", "TEA", 4.9, 289)
            )
            coffeeDao.insertAll(initialCoffees)
        }
    }
}

