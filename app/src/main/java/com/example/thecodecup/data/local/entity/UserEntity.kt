// ...existing code...
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    // ...existing code...
    val loyaltyStamps: Int = 0,
    val loyaltyPoints: Int = 0
)

