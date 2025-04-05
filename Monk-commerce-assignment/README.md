# Coupon Management API for E-commerce

This project implements a RESTful API for managing and applying different types of discount coupons for an e-commerce platform.

## Features

- CRUD operations for coupons
- Support for different coupon types:
  - Cart-wise
  - Product-wise
  - Buy X Get Y (BxGy)
- Fetch applicable coupons for a cart
- Apply a coupon to a cart
- Bonus: Expiration dates for coupons

## Technologies Used

- Spring Boot 3.1.4
- Java 17
- MySQL Database
- Maven
- Lombok
- Spring Data JPA

## API Endpoints

- `POST /coupons`: Create a new coupon
- `GET /coupons`: Retrieve all coupons
- `GET /coupons/{id}`: Retrieve a specific coupon by its ID
- `PUT /coupons/{id}`: Update a specific coupon by its ID
- `DELETE /coupons/{id}`: Delete a specific coupon by its ID
- `POST /applicable-coupons`: Fetch all applicable coupons for a given cart
- `POST /apply-coupon/{id}`: Apply a specific coupon to the cart

## Implemented Cases

### 1. Cart-wise Coupons
- **Percentage discount on entire cart**: Apply a discount percentage to the entire cart value if it exceeds a threshold.
- **Maximum discount cap**: Limit the maximum discount amount that can be applied.
- **Even distribution of discount**: Proportionally distribute the discount among all cart items based on their value.

### 2. Product-wise Coupons
- **Discount on specific products**: Apply a discount percentage to specific products in the cart.
- **Multiple quantity handling**: Apply discount to all units of the product in the cart.

### 3. BxGy Coupons (Buy X Get Y)
- **Buy from a set, get from another set**: Buy a specified number of products from one array and get specified products from another array for free.
- **Repetition limit**: Apply the BxGy offer multiple times up to a specified limit.
- **Free product handling**: Apply discount to products that are being offered for free.

## Unimplemented Cases

1. **Coupon Stacking**: The current implementation doesn't handle applying multiple coupons to the same cart simultaneously.

2. **Complex Conditions**: 
   - Time-based coupon validity (e.g., only valid on weekends)
   - User-specific coupons (new users, loyal customers, etc.)
   - Category-based coupons rather than specific products

3. **Coupon Prioritization**: No mechanism to determine which coupon to apply if multiple are applicable to optimize savings.

4. **Partial Application**: 
   - For BxGy coupons, if there are not enough "get" products in the cart, the system doesn't allow adding more items to make the coupon applicable.

5. **Coupon Codes Validation**: 
   - No validation for unique coupon codes
   - No coupon code generation mechanism

6. **Inventory Integration**: The system doesn't check if free products are in stock.

## Limitations

1. **Performance**: The calculation of applicable coupons could become inefficient for large carts with many products.

2. **Database Persistence**: Data is stored in MySQL, which provides persistence but requires a running MySQL server.

3. **No Authentication/Authorization**: No user authentication or role-based access control for coupon management.

4. **Limited Error Handling**: Basic error handling for common scenarios, but not comprehensive.

5. **No Pagination**: Retrieving all coupons doesn't support pagination for a large number of coupons.

## Assumptions

1. **Product Information**: The system assumes that product information (ID, price) is correctly provided in the cart items.

2. **Price Calculation**: All price calculations are performed on the server-side, assuming the client provides accurate quantities.

3. **Single Coupon Application**: Only one coupon can be applied to a cart at a time.

4. **Coupon Eligibility**: A coupon is considered applicable even if it offers a very small discount.

5. **BxGy Implementation**: For BxGy coupons:
   - Products in the "buy" array are interchangeable
   - Products in the "get" array are interchangeable
   - If there are not enough products in the "get" array, the coupon still applies to as many as possible

## Database Setup

Before running the application, make sure to:

1. Install MySQL Server if not already installed
2. Create a database named `system`
3. Update the database configuration in `application.properties` if needed

The default configuration uses:
- Database URL: `jdbc:mysql://localhost:3306/system`
- Username: `root`
- Password: `pranav2001`

## Running the Application

1. Clone the repository
2. Build the project: `mvn clean install`
3. Run the application: `mvn spring-boot:run`
4. The API will be accessible at `http://localhost:8005`

## Testing the API

### Create a Cart-wise Coupon 