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
    version = 7,
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
                // ESPRESSO Category
                CoffeeEntity(1, "Americano", 35000.0, "Classic espresso with hot water", "coffee_americano", "ESPRESSO", 4.8, 245),
                CoffeeEntity(4, "Espresso", 30000.0, "Strong concentrated coffee", "coffee_espresso", "ESPRESSO", 4.6, 156),

                // LATTE Category
                CoffeeEntity(2, "Cappuccino", 45000.0, "Espresso with steamed milk foam", "coffee_capuchino", "LATTE", 4.9, 312),
                CoffeeEntity(3, "Latte", 45000.0, "Espresso with lots of steamed milk", "coffee_latte", "LATTE", 4.7, 189),
                CoffeeEntity(5, "Mocha", 55000.0, "Espresso with chocolate and steamed milk", "coffee_mocha", "LATTE", 4.8, 278),

                // PHIN (Vietnamese Traditional) Category
                CoffeeEntity(6, "Cà phê Đen", 25000.0, "Vietnamese black coffee", "coffee_dendavietnam", "PHIN", 4.5, 98),
                CoffeeEntity(7, "Cà phê Sữa", 29000.0, "Vietnamese coffee with condensed milk", "coffee_suadavietnam", "PHIN", 4.7, 167),
                CoffeeEntity(8, "Bạc Sỉu", 29000.0, "Vietnamese white coffee", "coffee_bacsiu", "PHIN", 4.4, 87),

                // TEA Category
                CoffeeEntity(9, "Trà Đào", 39000.0, "Refreshing peach tea", "tea_tradao", "TEA", 4.6, 134),
                CoffeeEntity(10, "Trà Đào Cam Sả", 45000.0, "Peach tea with orange and lemongrass", "tea_tradaocamsa", "TEA", 4.8, 201),
                CoffeeEntity(11, "Trà Vải", 39000.0, "Sweet lychee tea", "tea_travai", "TEA", 4.5, 112),
                CoffeeEntity(12, "Matcha Latte", 55000.0, "Green tea with steamed milk", "tea_matchalatte", "TEA", 4.9, 289),

                // FREEZE Category
                CoffeeEntity(13, "Freeze Trà Xanh", 55000.0, "Blended green tea frappe", "freeze_traxanh", "FREEZE", 4.7, 198),
                CoffeeEntity(14, "Freeze Chocolate", 55000.0, "Blended chocolate frappe", "freeze_chocolate", "FREEZE", 4.8, 223),

                // COFFEE (General) Category
                CoffeeEntity(15, "Cold Brew", 45000.0, "Slow-brewed cold coffee", "coffee_coldbrew", "COFFEE", 4.6, 145),
                CoffeeEntity(16, "Caramel Macchiato", 55000.0, "Espresso with vanilla and caramel", "caramelmacchiato", "COFFEE", 4.9, 267),

                // CAKE (Bánh ngọt) Category
                CoffeeEntity(17, "Tiramisu Socola", 45000.0, "Bánh tiramisu vị socola đậm đà", "cake_tiramisu_socola", "CAKE", 4.9, 186),
                CoffeeEntity(18, "Mousse", 40000.0, "Bánh mousse mềm mịn tan trong miệng", "cake_mousse", "CAKE", 4.7, 142),
                CoffeeEntity(19, "Cupcake", 35000.0, "Bánh cupcake nhỏ xinh với kem bơ", "cake_cupcake", "CAKE", 4.6, 98),
                CoffeeEntity(20, "Pudding", 30000.0, "Bánh pudding caramel mềm mịn", "cake_pudding", "CAKE", 4.8, 167),
                CoffeeEntity(21, "Combo Bánh Mì", 55000.0, "Combo bánh mì nướng với bơ tỏi", "cake_combobanhmi", "CAKE", 4.5, 124)
            )
            coffeeDao.insertAll(initialCoffees)
        }
    }
}

