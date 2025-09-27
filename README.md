# Pharmacy Manager System

A comprehensive pharmacy management system consisting of an Android mobile application and a Laravel REST API backend. This system provides complete inventory management, order processing, customer management, and user authentication for pharmacy operations.

## 🏗️ System Architecture

### Components
- **Android App** (`PharmacyManager/`) - Native Android application built with Java
- **Laravel API** (`pharmacy-manager-api/`) - RESTful API backend built with Laravel 12
- **Database** - SQLite (default) with support for MySQL, PostgreSQL, and MariaDB

### Key Features
- 🔐 **User Authentication** - Secure login/registration with Laravel Sanctum
- 📦 **Product Management** - Complete inventory tracking with categories, SKUs, and expiry dates
- 👥 **Customer Management** - Customer profiles with loyalty points system
- 🛒 **Order Processing** - Full order lifecycle management with item tracking
- 📊 **Real-time Updates** - Live data synchronization between app and API
- 🔍 **Search & Filtering** - Advanced search capabilities across all entities
- 📱 **Modern UI** - Material Design components with responsive layouts

## 📋 Prerequisites

### For Android Development
- **Android Studio** (latest stable version)
- **Java Development Kit (JDK) 11** or higher
- **Android SDK** (API level 24+)
- **Gradle** (included with Android Studio)

### For API Development
- **PHP 8.2** or higher
- **Composer** (PHP dependency manager)
- **Node.js 18+** and **npm** (for frontend assets)
- **Database** (SQLite, MySQL, PostgreSQL, or MariaDB)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd <repository-name>
```

### 2. API Setup

#### Navigate to API directory
```bash
cd pharmacy-manager-api
```

#### Install PHP dependencies
```bash
composer install
```

#### Install Node.js dependencies
```bash
npm install
```

#### Environment Configuration
```bash
# Copy environment file
cp .env.example .env

# Generate application key
php artisan key:generate
```

#### Database Setup
```bash
# Create database file (for SQLite)
touch database/database.sqlite

# Run migrations
php artisan migrate

# Seed database with sample data (optional)
php artisan db:seed
```

#### Start the API server
```bash
# Development server
php artisan serve

# Or with queue processing and logging
composer run dev
```

The API will be available at `http://localhost:8000`

### 3. Android App Setup

#### Navigate to Android project
```bash
cd ../PharmacyManager
```

#### Configure API Endpoint
Edit `app/src/main/java/com/example/pharmacymanager/data/remote/ApiConfig.java`:

```java
public static final String BASE_URL = "http://YOUR_LOCAL_IP:8000/api/v1/";
```

Replace `YOUR_LOCAL_IP` with your computer's IP address (e.g., `192.168.1.100`).

#### Build and Run
1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on device/emulator

## 🔧 Configuration

### API Configuration

#### Database Configuration
The API supports multiple database systems. Configure in `.env`:

```env
# SQLite (default)
DB_CONNECTION=sqlite
DB_DATABASE=/path/to/database.sqlite

# MySQL
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=pharmacy_manager
DB_USERNAME=root
DB_PASSWORD=your_password

# PostgreSQL
DB_CONNECTION=pgsql
DB_HOST=127.0.0.1
DB_PORT=5432
DB_DATABASE=pharmacy_manager
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

#### CORS Configuration
For development, ensure CORS is properly configured to allow requests from the Android app.

### Android Configuration

#### Network Security
The app is configured to allow HTTP traffic for development. For production, update `network_security_config.xml` to enforce HTTPS.

#### API Endpoint
Update the `BASE_URL` in `ApiConfig.java` to match your API server address.

## 📊 Database Schema

### Core Tables

#### Users
- `id` - Primary key
- `uuid` - Unique identifier
- `name` - User's full name
- `email` - Unique email address
- `phone` - Contact number
- `address` - Physical address
- `password` - Hashed password
- `email_verified_at` - Email verification timestamp
- `last_login_at` - Last login timestamp
- `created_at`, `updated_at` - Timestamps
- `deleted_at` - Soft delete timestamp

#### Categories
- `id` - Primary key
- `name` - Category name (indexed)
- `description` - Optional description
- `created_at`, `updated_at` - Timestamps

#### Products
- `id` - Primary key
- `uuid` - Unique identifier
- `sku` - Stock Keeping Unit (unique, indexed)
- `name` - Product name (indexed)
- `description` - Product description
- `price` - Unit price (decimal 10,2)
- `quantity` - Available quantity
- `total` - Total value
- `manufacture_date` - Manufacturing date
- `expiry_date` - Expiration date
- `category_id` - Foreign key to categories
- `created_at`, `updated_at` - Timestamps
- `deleted_at` - Soft delete timestamp

#### Customers
- `id` - Primary key
- `user_id` - Foreign key to users
- `loyalty_points` - Customer loyalty points
- `created_at`, `updated_at` - Timestamps

#### Orders
- `id` - Primary key
- `customer_id` - Foreign key to customers
- `order_date` - Order placement date
- `total_amount` - Total order value
- `status` - Order status (pending, completed, cancelled)
- `created_at`, `updated_at` - Timestamps

#### Order Items
- `id` - Primary key
- `order_id` - Foreign key to orders
- `product_id` - Foreign key to products
- `quantity` - Item quantity
- `price` - Item price at time of order
- `created_at`, `updated_at` - Timestamps

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/user` - Get current user
- `POST /api/auth/logout` - User logout

### Products
- `GET /api/v1/products` - List products (with pagination, search, filtering)
- `POST /api/v1/products` - Create product
- `GET /api/v1/products/{id}` - Get product details
- `PUT /api/v1/products/{id}` - Update product
- `DELETE /api/v1/products/{id}` - Delete product

### Categories
- `GET /api/v1/categories` - List categories
- `POST /api/v1/categories` - Create category
- `GET /api/v1/categories/{id}` - Get category details
- `PUT /api/v1/categories/{id}` - Update category
- `DELETE /api/v1/categories/{id}` - Delete category

### Orders
- `GET /api/v1/orders` - List orders (with filtering by status, customer, date)
- `POST /api/v1/orders` - Create order
- `GET /api/v1/orders/{id}` - Get order details
- `PUT /api/v1/orders/{id}` - Update order
- `DELETE /api/v1/orders/{id}` - Delete order

### Customers
- `GET /api/v1/customers` - List customers
- `POST /api/v1/customers` - Create customer
- `GET /api/v1/customers/{id}` - Get customer details
- `PUT /api/v1/customers/{id}` - Update customer
- `DELETE /api/v1/customers/{id}` - Delete customer
- `GET /api/v1/customers/user/{userId}` - Get customers by user ID

## 🛠️ Development

### API Development

#### Running Tests
```bash
# Run all tests
php artisan test

# Run specific test suite
php artisan test --testsuite=Feature
```

#### Code Quality
```bash
# Run Laravel Pint (code formatting)
./vendor/bin/pint

# Run static analysis
composer run-script static-analysis
```

#### Database Management
```bash
# Create new migration
php artisan make:migration create_table_name

# Rollback migrations
php artisan migrate:rollback

# Reset database
php artisan migrate:fresh --seed
```

### Android Development

#### Project Structure
```
app/src/main/java/com/example/pharmacymanager/
├── data/
│   ├── entities/          # Data models
│   ├── local/            # Local storage (SessionManager)
│   ├── remote/           # API client and configuration
│   └── repositories/     # Data access layer
└── ui/
    ├── auth/             # Authentication screens
    ├── category/         # Category management
    ├── customer/         # Customer management
    ├── home/             # Dashboard
    ├── order/            # Order management
    ├── product/          # Product management
    ├── MainActivity.java # Main activity
    └── SplashActivity.java # Splash screen
```

#### Key Dependencies
- **Volley** - HTTP networking
- **Gson** - JSON parsing
- **Material Design Components** - UI components
- **Navigation Component** - Screen navigation
- **ViewBinding** - Type-safe view references
- **DataBinding** - Data binding support

## 🚀 Deployment

### API Deployment

#### Production Environment
1. Set `APP_ENV=production` in `.env`
2. Configure production database
3. Set up web server (Apache/Nginx)
4. Configure SSL certificates
5. Set up process manager (Supervisor/PM2)

#### Docker Deployment
```bash
# Build Docker image
docker build -t pharmacy-manager-api .

# Run container
docker run -p 8000:8000 pharmacy-manager-api
```

### Android Deployment

#### Release Build
1. Generate signed APK/AAB
2. Configure ProGuard for code obfuscation
3. Update API endpoint to production URL
4. Test on various devices and screen sizes

## 📱 Features Overview

### Authentication System
- Secure user registration and login
- Token-based authentication with Laravel Sanctum
- Session management with automatic token refresh
- Password hashing and validation

### Product Management
- Complete inventory tracking
- SKU generation and management
- Category-based organization
- Expiry date monitoring
- Price and quantity management
- Search and filtering capabilities

### Order Processing
- Multi-item order creation
- Real-time total calculation
- Order status tracking
- Customer order history
- Order modification and cancellation

### Customer Management
- Customer profile creation
- Loyalty points system
- Order history tracking
- Contact information management

### User Interface
- Material Design 3 components
- Responsive layouts for different screen sizes
- Dark/Light theme support
- Intuitive navigation
- Real-time data updates

## 🔒 Security Features

- **Authentication** - Laravel Sanctum for API authentication
- **Authorization** - Role-based access control
- **Data Validation** - Comprehensive input validation
- **SQL Injection Protection** - Eloquent ORM with parameterized queries
- **XSS Protection** - Output escaping and validation
- **CSRF Protection** - Laravel's built-in CSRF protection
- **Rate Limiting** - API rate limiting for security
- **Secure Headers** - Security headers configuration

## 🧪 Testing

### API Testing
- Unit tests for models and services
- Feature tests for API endpoints
- Database testing with factories and seeders
- Postman collection for manual testing

### Android Testing
- Unit tests for business logic
- Instrumented tests for UI components
- Integration tests for API communication

## 📚 Documentation

### API Documentation
- Swagger/OpenAPI documentation available at `/api/documentation`
- Postman collection included in the repository
- Comprehensive endpoint documentation with examples

### Code Documentation
- Inline code comments
- JavaDoc for Android classes
- PHPDoc for API methods
- README files for each major component

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the API documentation at `/api/documentation`

## 🔄 Version History

- **v1.0.0** - Initial release with core functionality
- **v1.1.0** - Added loyalty points system
- **v1.2.0** - Enhanced UI with Material Design 3
- **v1.3.0** - Added advanced search and filtering

---

**Note**: This system is designed for educational and small business use. For production deployment in large-scale environments, additional security measures, performance optimizations, and scalability considerations should be implemented.
