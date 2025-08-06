# LPG Delivery System 🚚🔥

A Spring Boot-based backend system to manage LPG cylinder orders, delivery, and dealer integration with role-based access control.

## ⚙️ Tech Stack

| Layer            | Technology                         |
|------------------|-------------------------------------|
| Backend          | Spring Boot (Java 17, 3.3.3)        |
| Logging          | SLF4J                               |
| Database         | MySQL                               |
| Containerization | Docker                              |
| Build Tool       | Gradle (`./gradlew build`)          |
| Auth             | JWT (Role-based)                    |

## 📦 Modules & Features

- **User Management**  
  - Roles: `ADMIN`, `USER`, `DELIVERY_PERSON`  
  - JWT-based login & access

- **Customer & Cylinder Management**  
  - Customers register & track orders  
  - Auto-assignment of available Domestic (14.5 kg) / Commercial (18.5 kg) cylinders

- **Order Management**  
  - Orders based on cylinder type & availability  
  - Admin can filter orders by name/phone  
  - Users can edit unassigned orders

- **Delivery Assignment**  
  - Admin manually assigns unassigned orders

- **Dealer & Pincode Integration**  
  - Customers choose dealers based on pincode

- **Feedback System**  
  - Feedback allowed post-delivery

- **Order Reporting**  
  - Admin can fetch orders grouped by customer, filtered by date range

## 🔐 Security

- JWT tokens for session management  
- Role-based API access

## 🐳 Docker

To build and run:

```bash
./gradlew build
docker build -t lpg-delivery-system .
docker run -p 8080:8080 lpg-delivery-system
