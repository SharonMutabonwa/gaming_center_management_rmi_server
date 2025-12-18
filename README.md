# 🎮 Gaming Center Management System – Server Application

## 📌 Overview

This project is the **Server-side application** of the Gaming Center Management System. It is a **Java-based distributed backend** responsible for all **business logic, validations, database access, security, and notifications**.

The server communicates with multiple client applications using **Java RMI** and acts as the central authority of the system.

---

## 🎯 Responsibilities

* Authenticate users using OTP-based login
* Enforce all business rules and validations
* Manage bookings, customers, stations, tournaments, and transactions
* Handle database persistence via Hibernate ORM
* Send notifications asynchronously using ActiveMQ
* Generate business reports (PDF, Excel, CSV)

---

## 🏗️ Architecture Role

**Tier:** Business Tier (Server)

**Architecture:** Three-Tier Distributed Architecture

* Presentation Tier → Client (Swing)
* Business Tier → This Server
* Data Tier → MySQL Database

---

## 🖥️ Technology Stack

* Java SE
* Java RMI
* Hibernate ORM
* MySQL
* ActiveMQ
* JavaMail API
* iText (PDF Reports)
* Apache POI (Excel Reports)
* Commons CSV

---

## 📂 Project Structure

```
server/
│
├── service/
│   ├── RemoteService.java
│   └── RemoteServiceImpl.java
│
├── dao/
│   ├── GenericDAO.java
│   ├── CustomerDAO.java
│   ├── BookingDAO.java
│   └── TournamentDAO.java
│
├── model/               # Entity classes
│   ├── Customer.java
│   ├── Booking.java
│   ├── GamingStation.java
│   ├── Tournament.java
│   └── Transaction.java
│
├── util/
│   ├── HibernateUtil.java
│   ├── ValidationUtil.java
│   └── ReportFactory.java
│
├── messaging/
│   ├── MessageProducer.java
│   └── MessageConsumer.java
│
├── email/
│   └── EmailService.java
│
└── main/
    └── RMIServer.java
```

---

## 🔐 Authentication & Security

* Username & password validation
* OTP generation and verification
* Email delivery via ActiveMQ
* Session-based authorization

---

## 🔄 Client Communication (RMI)

* Protocol: **Java RMI**
* Default Port: **3500**

Example server method:

```java
public Booking createBooking(...) throws RemoteException {
    // validations
    // database operations
    // notifications
    return booking;
}
```

---

## 🧠 Business Logic Handled by Server

* Booking conflict prevention
* Membership expiry checks
* Age restriction enforcement
* Balance and payment validation
* Tournament capacity and deadline rules
* Automatic discount calculation

---

## 💾 Database

* Database: **MySQL**
* ORM: **Hibernate**
* Database Name: `gaming_center_management_system_db`
* Total Tables: 11

All CRUD operations are handled through the DAO layer.

---

## 📬 Messaging & Notifications

* Message Broker: **ActiveMQ** (Port 61616)
* OTP and notification queues
* Asynchronous email sending

Benefits:

* Non-blocking operations
* Retry support
* Loose coupling

---

## 📊 Report Generation

* PDF Reports using **iText**
* Excel Reports using **Apache POI**
* CSV Reports using **Commons CSV**

Reports are generated on the server and returned to the client as byte streams.

---

## ▶️ How to Run the Server

1. Start MySQL database
2. Start ActiveMQ broker
3. Configure database credentials in `HibernateUtil`
4. Run `RMIServer.java`
5. Ensure RMI registry is active on port 3500

---

## 🚀 Scalability & Extensibility

* Multiple clients supported simultaneously
* Centralized business logic
* Easy to add new features or validations
* Database and messaging layers are interchangeable

---
## 🔗 Client Applications
This server is consumed by the Gaming Center Swing Client.

Client Repository:
[https://github.com/<your-username>/gaming-center-client](https://github.com/SharonMutabonwa/gaming_center_management_rmi_client.git)


