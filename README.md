# Pharmacy Manager Android App

A comprehensive Android application for pharmacy management built with Java. This mobile app provides complete inventory management, order processing, customer management, and user authentication for pharmacy operations.

## Project Presentation Details

This project was presented as a **Soutenance de projet de fin d'étude** (Final Year Project Defense) at:
- **University:** Université Sidi Mohamed Ben Abdellah
- **School:** École Nationale des Sciences Appliquées

### Project Title
**Creation de Gestion de Pharmacy Laravel API + Java Android App**

### Presented by
- Yassine Faidi
- Hamza Mekouar

### Supervised by
- Mr.S Jamal RIFFI
- Mr.s LAKHRISSI YOUNES

## Repository

- **Android App**: [PharmacyManager](https://github.com/faididev/PharmacyManager)
- **Backend API**: [pharmacy-manager-api](https://github.com/faididev/pharmacy-manager-api)

## Features

### Authentication System
- **Secure Login/Logout** with token-based authentication
- **User Registration** with form validation
- **Session Management** with automatic token refresh
- **Password Security** with proper validation

### Product Management
- **Product List** with search and filtering
- **Add/Edit Products** with comprehensive forms
- **Product Details** with full information display
- **Category Management** for product organization
- **Inventory Tracking** with quantity management
- **Expiry Date Monitoring** for pharmaceutical products
- **SKU Management** with auto-generation
- **Price Management** with decimal precision

### Order Management
- **Order Creation** with multiple items
- **Order List** with status filtering
- **Order Details** with item breakdown
- **Order Status Tracking** (pending, completed, cancelled)


### Customer Management
- **Customer List** with search functionality
- **Add/Edit Customers** with contact information
- **Customer Details** with order history
- **Loyalty Points System** for customer retention

### Category Management
- **Category List** with organization
- **Add/Edit Categories** with descriptions
- **Product Association** for better organization

### User Interface
- **Material Design 3** components
- **Responsive Layouts** for different screen sizes
- **Dark/Light Theme** support
- **Intuitive Navigation** with bottom navigation
- **Real-time Data Updates** with pull-to-refresh
- **Loading States** and error handling
- **Form Validation** with user-friendly messages

## Technology Stack

- **Language**: Java
- **Platform**: Android (API level 24+)
- **UI Framework**: Android Views with Material Design
- **Networking**: Volley for HTTP requests
- **JSON Parsing**: Gson for data serialization
- **Navigation**: Android Navigation Component
- **Data Binding**: ViewBinding and DataBinding
- **Architecture**: MVVM (Model-View-ViewModel)
- **Build System**: Gradle with Kotlin DSL

## Prerequisites

- **Android Studio** (latest stable version)
- **Java Development Kit (JDK) 11** or higher
- **Android SDK** (API level 24+)
- **Gradle** (included with Android Studio)
- **Backend API** running (see [pharmacy-manager-api](https://github.com/faididev/pharmacy-manager-api))

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/faididev/PharmacyManager.git
cd PharmacyManager
```

### 2. Open in Android Studio
1. Launch Android Studio
2. Select "Open an existing project"
3. Navigate to the cloned directory
4. Click "OK" to open the project

### 3. Configure API Endpoint
Edit `app/src/main/java/com/example/pharmacymanager/data/remote/ApiConfig.java`:

```java
public static final String BASE_URL = "http://YOUR_API_IP:8000/api/v1/";
```

Replace `YOUR_API_IP` with your backend server's IP address.

### 4. Build and Run
1. Sync Gradle files (Android Studio will prompt you)
2. Connect an Android device or start an emulator
3. Click the "Run" button or press `Shift + F10`
4. The app will install and launch on your device

## Project Structure

```
app/src/main/java/com/example/pharmacymanager/
├── data/
│   ├── entities/          # Data models (Product, Order, Customer, etc.)
│   ├── local/            # Local storage (SessionManager)
│   ├── remote/           # API client and configuration
│   └── repositories/     # Data access layer
└── ui/
    ├── auth/             # Authentication screens
    │   ├── LoginActivity.java
    │   └── SignupActivity.java
    ├── category/         # Category management
    │   ├── AddCategoryFragment.java
    │   ├── EditCategoryFragment.java
    │   ├── ListCategoryFragment.java
    │   └── CategoryAdapter.java
    ├── customer/         # Customer management
    │   ├── AddCustomerFragment.java
    │   ├── EditCustomerFragment.java
    │   ├── ListCustomerFragment.java
    │   ├── ViewCustomerFragment.java
    │   └── CustomerAdapter.java
    ├── home/             # Dashboard
    │   └── HomeFragment.java
    ├── order/            # Order management
    │   ├── AddOrderFragment.java
    │   ├── EditOrderFragment.java
    │   ├── ListOrderFragment.java
    │   ├── ViewOrderFragment.java
    │   ├── OrderAdapter.java
    │   ├── ProductSelectionDialog.java
    │   └── QuantityInputDialog.java
    ├── product/          # Product management
    │   ├── AddProductFragment.java
    │   ├── EditProductFragment.java
    │   ├── ListProductFragment.java
    │   ├── ViewProductFragment.java
    │   └── ProductAdapter.java
    ├── MainActivity.java # Main activity with navigation
    └── SplashActivity.java # Splash screen
```

## Key Dependencies

### Core Android Libraries
- **AppCompat**: `androidx.appcompat:appcompat:1.7.1`
- **Material Design**: `com.google.android.material:material:1.12.0`
- **ConstraintLayout**: `androidx.constraintlayout:constraintlayout:2.2.1`
- **Activity**: `androidx.activity:activity:1.10.1`
- **Fragment**: `androidx.fragment:fragment:1.8.8`

### Networking & Data
- **Volley**: `com.android.volley:volley:1.2.1` - HTTP networking
- **Gson**: `com.google.code.gson:gson:2.11.0` - JSON parsing

### Architecture Components
- **Lifecycle**: `androidx.lifecycle:lifecycle-viewmodel:2.9.2`
- **LiveData**: `androidx.lifecycle:lifecycle-livedata:2.9.2`
- **Navigation**: `androidx.navigation:navigation-fragment-ktx:2.9.2`

### UI Components
- **SwipeRefreshLayout**: `androidx.swiperefreshlayout:swiperefreshlayout:1.1.0`
- **ViewBinding**: Enabled in build.gradle
- **DataBinding**: Enabled in build.gradle

## Configuration

### API Configuration
The app connects to a Laravel REST API backend. Configure the API endpoint in `ApiConfig.java`:

```java
public static final String BASE_URL = "http://192.168.1.100:8000/api/v1/";
```

### Network Security
The app is configured to allow HTTP traffic for development. For production:
1. Update `network_security_config.xml` to enforce HTTPS
2. Ensure your API server has SSL certificates
3. Update the BASE_URL to use HTTPS

### Build Configuration
- **Target SDK**: 36 (Android 14)
- **Minimum SDK**: 24 (Android 7.0)
- **Compile SDK**: 36
- **Java Version**: 11

## Usage

### Getting Started
1. **Launch the app** - You'll see the splash screen
2. **Login** - Use your credentials or register a new account
3. **Navigate** - Use the bottom navigation to access different features
4. **Manage Products** - Add, edit, and view your inventory
5. **Process Orders** - Create and manage customer orders
6. **Manage Customers** - Add and track customer information

### Main Features

#### Product Management
- **View Products**: Browse your inventory with search and filter options
- **Add Product**: Create new products with SKU, price, quantity, and expiry date
- **Edit Product**: Update product information and inventory levels
- **Category Management**: Organize products by categories

#### Order Processing
- **Create Order**: Select products and quantities for customer orders
- **Order History**: View all orders with status tracking
- **Order Details**: See complete order breakdown with totals
- **Status Updates**: Change order status (pending, completed, cancelled)

#### Customer Management
- **Customer List**: View all customers with search functionality
- **Add Customer**: Register new customers with contact information
- **Customer Details**: View customer profile and order history
- **Loyalty Points**: Track customer loyalty points

## Development

### Building the Project
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

### Code Structure
The app follows MVVM architecture:
- **Models**: Data entities in `data/entities/`
- **Views**: Activities and Fragments in `ui/`
- **ViewModels**: Business logic in repositories
- **Data Layer**: API calls and local storage

### Key Classes

#### Data Models
- `Product.java` - Product entity with JSON parsing
- `Order.java` - Order entity with items
- `Customer.java` - Customer entity
- `Category.java` - Category entity
- `User.java` - User entity

#### API Integration
- `ApiClient.java` - HTTP client using Volley
- `ApiConfig.java` - API configuration and URL building
- `RequestQueueSingleton.java` - Singleton for request queue

#### Repositories
- `ProductRepository.java` - Product data operations
- `OrderRepository.java` - Order data operations
- `CustomerRepository.java` - Customer data operations
- `AuthRepository.java` - Authentication operations

## Testing

### Unit Tests
- **Location**: `src/test/java/`
- **Coverage**: Business logic and data parsing
- **Framework**: JUnit 4

### Instrumented Tests
- **Location**: `src/androidTest/java/`
- **Coverage**: UI interactions and API integration
- **Framework**: Espresso

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```


#### API Connection Issues
- **Check API URL**: Ensure BASE_URL is correct in ApiConfig.java
- **Network Permissions**: Verify INTERNET permission in AndroidManifest.xml
- **API Server**: Ensure backend API is running and accessible

#### Build Issues
- **Gradle Sync**: Try "Sync Project with Gradle Files"
- **Clean Build**: Use "Build > Clean Project" then rebuild
- **SDK Issues**: Check Android SDK installation and API levels

#### Runtime Issues
- **JSON Parsing**: Check API response format matches entity classes
- **Memory Issues**: Monitor app memory usage and optimize images
- **Navigation**: Ensure proper fragment navigation setup

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Android coding standards
- Write tests for new features
- Update documentation for API changes
- Use meaningful commit messages
- Ensure all tests pass before submitting PR

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Create an issue in the repository
- Check the backend API documentation
- Review the Android documentation

---

**Note**: This Android app is designed to work with the Laravel API backend. Make sure the backend API is running and accessible before using the app. For production deployment, ensure proper security measures and API endpoint configuration.