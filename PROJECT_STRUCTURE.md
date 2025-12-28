# TheCodeCup - Coffee Shop App

## Cấu trúc thư mục (Clean Architecture + MVVM)

```
app/src/main/java/com/example/thecodecup/
│
├── MainActivity.kt                    # Entry point
├── TheCodeCupApplication.kt           # Application class (initialize ServiceLocator)
│
├── core/                              # Core utilities (non-UI, cross-layer)
│   ├── constants/
│   │   └── AppConstants.kt            # Pricing, loyalty constants
│   ├── result/
│   │   ├── AppResult.kt               # Success/Error/Loading wrapper
│   │   └── AppError.kt                # Error types
│   ├── utils/
│   │   ├── PriceFormatter.kt
│   │   ├── DateTimeUtils.kt
│   │   └── IdGenerator.kt
│   └── dispatcher/
│       └── DispatcherProvider.kt
│
├── di/                                # Dependency Injection
│   └── ServiceLocator.kt              # Simple DI container with DB initialization
│
├── domain/                            # Domain Layer (Business Logic)
│   ├── common/
│   │   ├── DomainResult.kt            # Success/Error sealed class
│   │   └── DomainException.kt         # Domain-level exceptions
│   │
│   ├── model/
│   │   ├── Coffee.kt                  # Coffee entity + CoffeeCategory
│   │   ├── CoffeeOptions.kt           # Shot, Temperature, Size, Ice enums
│   │   ├── CartItem.kt                # CartItem + Cart
│   │   ├── Order.kt                   # Order, OrderItem, OrderStatus
│   │   ├── User.kt                    # User entity with loyalty methods
│   │   └── Reward.kt                  # Reward, RewardHistory, RewardType
│   │
│   ├── repository/                    # Repository interfaces
│   │   ├── CoffeeRepository.kt
│   │   ├── CartRepository.kt
│   │   ├── OrderRepository.kt
│   │   ├── UserRepository.kt
│   │   └── RewardRepository.kt
│   │
│   └── usecase/                       # Use Cases
│       ├── cart/
│       │   ├── AddToCartUseCase.kt
│       │   ├── GetCartItemsUseCase.kt
│       │   ├── UpdateCartItemQuantityUseCase.kt
│       │   ├── RemoveFromCartUseCase.kt
│       │   ├── ClearCartUseCase.kt
│       │   └── CalculateCartTotalUseCase.kt
│       ├── coffee/
│       │   ├── GetCoffeeMenuUseCase.kt
│       │   ├── GetCoffeeByIdUseCase.kt
│       │   └── SearchCoffeesUseCase.kt
│       ├── order/
│       │   ├── PlaceOrderUseCase.kt       # Complex: Cart + Order + User
│       │   ├── GetOrderHistoryUseCase.kt
│       │   ├── GetOngoingOrdersUseCase.kt
│       │   ├── GetOrderByIdUseCase.kt
│       │   └── CancelOrderUseCase.kt
│       └── user/
│           ├── GetCurrentUserUseCase.kt
│           └── UpdateUserProfileUseCase.kt
│
├── data/                              # Data Layer
│   ├── local/                         # Local Data Source (Room)
│   │   ├── AppDatabase.kt             # Room Database
│   │   ├── CartLocalDataSource.kt
│   │   ├── dao/
│   │   │   ├── CoffeeDao.kt
│   │   │   └── CartDao.kt
│   │   └── entity/
│   │       ├── CoffeeEntity.kt
│   │       └── CartItemEntity.kt
│   │
│   ├── mapper/
│   │   └── CoffeeMappers.kt           # Entity <-> Domain mappers
│   │
│   ├── model/                         # Data models (if needed)
│   │
│   ├── remote/                        # Remote Data Source (future)
│   │
│   └── repository/                    # Repository implementations
│       ├── CoffeeRepositoryImpl.kt    # Uses CoffeeDao
│       ├── CartRepositoryImpl.kt      # Uses CartDao
│       ├── OrderRepositoryImpl.kt     # In-memory for now
│       ├── UserRepositoryImpl.kt      # In-memory for now
│       └── RewardRepositoryImpl.kt    # In-memory for now
│
└── presentation/                      # Presentation Layer (UI + MVVM)
    │
    ├── common/
    │   └── UiState.kt                 # Loading/Success/Error sealed interface
    │
    ├── components/                    # Reusable UI components
    │   ├── colors/
    │   │   └── AppColors.kt
    │   ├── topbar/
    │   │   ├── CommonTopBar.kt
    │   │   └── CenteredTopBar.kt
    │   ├── bottomnav/
    │   │   ├── AppBottomNavBar.kt
    │   │   └── BottomNavItem.kt
    │   ├── states/
    │   │   ├── LoadingScreen.kt
    │   │   ├── ErrorScreen.kt
    │   │   └── EmptyStateScreen.kt
    │   ├── buttons/
    │   │   └── PrimaryButton.kt
    │   ├── cards/
    │   │   ├── CoffeeItemCard.kt
    │   │   └── LoyaltyCardView.kt
    │   └── icons/
    │       └── CartIconWithBadge.kt
    │
    ├── navigation/
    │   └── AppNavGraph.kt
    │
    ├── home/
    │   ├── HomeScreen.kt
    │   ├── HomeViewModel.kt
    │   └── HomeUiState.kt
    │
    ├── detail/
    │   ├── DetailScreen.kt
    │   ├── DetailViewModel.kt
    │   └── DetailUiState.kt
    │
    ├── cart/
    │   ├── CartScreen.kt
    │   ├── CartViewModel.kt
    │   └── CartUiState.kt
    │
    ├── order/
    │   ├── OrderSuccessScreen.kt
    │   ├── MyOrderScreen.kt
    │   ├── MyOrdersViewModel.kt
    │   ├── OrderUiState.kt
    │   └── OrderDisplayItem.kt
    │
    ├── profile/
    │   ├── ProfileScreen.kt
    │   ├── ProfileViewModel.kt
    │   └── ProfileUiState.kt
    │
    └── rewards/
        ├── RewardsScreen.kt
        ├── RedeemScreen.kt
        ├── RewardsViewModel.kt
        └── RewardsUiState.kt
```

## Domain Layer

### DomainResult
```kotlin
sealed class DomainResult<out T> {
    data class Success<out T>(val data: T) : DomainResult<T>()
    data class Error(val exception: DomainException) : DomainResult<Nothing>()
}
```

### Domain Models
- **Coffee**: id, name, basePrice, imageRes, description, category
- **CoffeeOptions**: shot, temperature, size, ice + calculateExtraPrice()
- **CartItem**: id, coffee, options, quantity + unitPrice, totalPrice
- **Cart**: items + totalPrice, itemCount, isEmpty
- **Order**: id, items, totalPrice, status, address, createdAt
- **User**: id, name, phone, email, address, loyaltyStamps, rewardPoints
- **Reward**: id, coffeeName, validUntil, pointsRequired, isRedeemed

## Data Layer

### Room Database
```kotlin
@Database(
    entities = [CoffeeEntity::class, CartItemEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao
    abstract fun cartDao(): CartDao
}
```

### DAOs
- **CoffeeDao**: insertAll, getAllCoffees (Flow), getCoffeeById, searchCoffees
- **CartDao**: insert, update, delete, getAllItems (Flow), getItemById, clearCart

### Mappers
```kotlin
fun CoffeeEntity.toDomain(): Coffee
fun Coffee.toEntity(): CoffeeEntity
fun CartItemEntity.toDomainWithMinimalCoffee(): CartItem
fun CartItem.toEntity(): CartItemEntity
fun CoffeeOptions.toJsonString(): String
fun String.toCoffeeOptions(): CoffeeOptions
```

## Use Cases

### Cart Use Cases
- **AddToCartUseCase**: Validate quantity, add to cart via repository
- **GetCartItemsUseCase**: Observe cart as Flow<DomainResult<Cart>>
- **UpdateCartItemQuantityUseCase**: Update or remove if quantity <= 0
- **RemoveFromCartUseCase**: Remove item by ID
- **ClearCartUseCase**: Clear all items
- **CalculateCartTotalUseCase**: Calculate subtotal, delivery fee, total

### Coffee Use Cases
- **GetCoffeeMenuUseCase**: Get all coffees as Flow
- **GetCoffeeByIdUseCase**: Get single coffee by ID
- **SearchCoffeesUseCase**: Search coffees by name

### User Use Cases
- **GetCurrentUserUseCase**: Observe current local user as Flow<DomainResult<User>>
- **UpdateUserProfileUseCase**: Update user profile (name, phone, email, address) with validation

### Order Use Cases
- **PlaceOrderUseCase**: Complex use case that:
  1. Gets cart items (validates not empty)
  2. Creates Order with OrderItems
  3. Saves Order to DB
  4. Clears the Cart
  5. Calculates loyalty points (1 point per 1000 currency units)
  6. Updates User's loyalty points and stamps
- **GetOrderHistoryUseCase**: Get completed/cancelled orders sorted by date desc
- **GetOngoingOrdersUseCase**: Get PLACED/ONGOING orders
- **GetOrderByIdUseCase**: Get single order by ID
- **CancelOrderUseCase**: Cancel a PLACED or ONGOING order

## Dependency Injection

### ServiceLocator
```kotlin
object ServiceLocator {
    fun initialize(context: Context)  // Call in Application.onCreate()
    
    // DAOs
    fun provideCoffeeDao(): CoffeeDao
    fun provideCartDao(): CartDao
    
    // Repositories
    fun provideCoffeeRepository(): CoffeeRepository
    fun provideCartRepository(): CartRepository
    fun provideOrderRepository(): OrderRepository
    fun provideUserRepository(): UserRepository
    fun provideRewardRepository(): RewardRepository
    
    // Use Cases
    fun provideGetCoffeeMenuUseCase(): GetCoffeeMenuUseCase
    fun provideAddToCartUseCase(): AddToCartUseCase
    fun provideGetCartItemsUseCase(): GetCartItemsUseCase
    // ... other use cases
}
```

## Application Setup

### TheCodeCupApplication.kt
```kotlin
class TheCodeCupApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initialize(this)
    }
}
```

### AndroidManifest.xml
```xml
<application
    android:name=".TheCodeCupApplication"
    ...>
```

## Build Dependencies

### Room Database
```kotlin
implementation(libs.room.runtime)
implementation(libs.room.ktx)
ksp(libs.room.compiler)
```

### Gson (for JSON conversion)
```kotlin
implementation(libs.gson)
```

## Build & Run

1. Mở project trong Android Studio
2. Sync Gradle
3. Run app

## Notes

- Database được pre-populate với 18 loại coffee khi khởi tạo
- Cart items được lưu trong Room database để persist qua app restarts
- Orders, User, Rewards hiện sử dụng in-memory storage (có thể migrate sang Room sau)
- **ViewModels chỉ phụ thuộc vào UseCases**, không trực tiếp sử dụng Repositories
- Sử dụng `AppViewModelFactory` để inject UseCases vào ViewModels
- Tất cả ViewModel đều handle `DomainResult` (Success/Error) từ UseCases

## ViewModel Pattern

```kotlin
class CartViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    // ... other use cases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCart()
    }

    fun onEvent(event: CartUiEvent) {
        when (event) {
            is CartUiEvent.PlaceOrder -> placeOrder(event.note, event.address)
            // ...
        }
    }

    private fun placeOrder(note: String?, address: String?) {
        viewModelScope.launch {
            when (val result = placeOrderUseCase(note, address)) {
                is DomainResult.Success -> {
                    _uiState.update { it.copy(orderSuccess = true) }
                }
                is DomainResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.exception.message) }
                }
            }
        }
    }
}
```

