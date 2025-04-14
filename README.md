![Bizmart company logo for application icon](/Assets/bizmart_logo.png "Bizmart Company Logo")

# Bizmart Supply Co.
I will develop an application for Bizmart Supply Co for the purpose of conducting business online and in-store. In addition, an inventory system will be implemented that will organize products so that thousands of items can be easily sorted through and managed.

## Detailed Description
The program will provide a POS system for employees to make in-store sales, and a separate UI that allows customers to place orders online. Managers will have their own page for business management, but will also be able to access the POS system. 

Prior to proposal submission, item categories and product information (such as pricing and max quantity) must be decided on. Research will be done into the product offerings and organization of businesses targeting the same demographic to help determine the most effective product lineup and organization. Wireframe designs for the apps main interfaces will also be created. After the proposal is accepted, this project will require a server for storing products, customer data, and employee data in a database. Four main application windows need to be created: the login view, customer view, employee view (POS system), and manager view. Other windows will be added as needed. This project is predicted to take 3 months to be completed. 

Due to time constraints, many common features used by other retail stores won't be available. For example, managers won't be able to add new products or alter existing products in the database (beyond adding inventory). They also won't be able to change employee and customer data beyond pay rate and contact information. For that same reason, customers will not be able to change their account information. Payment retrieval will not be implemented because it is beyond the scope of this project, and will require strict security measures. Lastly, the POS system won't be ready to implement in a store setting, because features such as barcode scanning can't be programmed without the hardware. 

### Inventory and Inventory Control
The inventory will consist of standard office supplies such as pens, paper, shipping materials, ink/toner, electronics, et c. Categories were decided based on research and my own experience as a customer and retail employee. The categories organize products in a way that is intuitive for customers and easy for the company to maintain. 

Each item's inventory is limited based on several factors:
* Expiration Date (If Applicable)
  * Products that have expiration dates, such as printer ink, will have a lower limit to minimize wasted product.
* Price
  * High dollar items, like electronics and furniture, will have a low limit (usually below 10) because they often take longer to sell.
  * Cheaper items are allowed much higher limit.
* Product Lifetime
  * Some products only need to be bought once, such as staplers or scissors. These items will have lower limit because each customer will likely only buy one.
  * Items that need frequent replacement, such as pens, will be allowed a high limit to keep up with demand.
* Demand
  * Products that are high in demand will be allowed a high limit. This is often dependent on brand or price. 

Customers will be able to purchase items from the following categories and subcategories:
* Shipping
  * Envelopes
  * Packing Material
  * Boxes
* Ink & Toner
  * Ink
  * Toner
* Paper
  * Printing
  * Filler & Graph
  * Notebooks & Pads
* Forms
  * Tax
  * Invoice
  * Legal 
* Planners
* Organization
  * File Folders
  * Storage Bin
  * Desk Organizers
* Electronics
  * Printers & Scanners
  * Computers
  * Computer Accessories
  * Software
  * Security
  * Networking
* Presentation
  * Binders
  * Portfolios
  * Pocket Folders
* Retail
* Desktop Supplies
  * Writing
  * Binding
  * Misc 
* Furniture
  * Chairs
  * Desks 

#### Inventory Example Data
Field | Data
------|------
Item Name | Pentel Ballpoint Pens - 5 Pack
Item Description | Pack of 5
Category | Writing
Retail Price| 6.19
Cost | 1.50
Quantity|83
Restock Threshold | 100
Image | [Image binary]
Discontinued | false

### Project Introduction
#### Project Goals
This project aims to provide Bizmart Supply Co an application to manage data and conduct business both online and in-person. Currently, Bizmart lacks a digital system for selling their wares. In this day and age, it is of utmost importance for a business to have an online store-front and a desktop POS system in their stores. 

When the user signs in (or registers a new customer account), they are presented with a different view depending on the access level associated with their account. There will be three access levels: customer, employee, and manager. The customer view will allow users to search for items by keyword or category. The user will be presented with thumbnails of related items, which they can click on for more information, or add to a cart. They can then checkout and place an order. The employee view is a POS system to process transactions, and then print a receipt. Items are added to the transaction by SKU number. The manager view will allow managers to generate HTML sales reports, manage customer and employee data, create promo codes, and manage product inventory. The application's UI has been designed to be user-friendly. If the user inputs invalid data, a message appears that describes why the input was incorrect.

#### Features
- All views have an internet connection indicator.
- Customers can search for a product by category or keyword.
- The register page has password requirement indicators.

#### Development Status
Project Completed

#### Below are wireframes for the main interfaces of the program
##### Login View
![Login page wireframe example](/Assets/LoginView.png "Bizmart Login Page")

##### Customer View
![Customer page wireframe example](/Assets/CustomerView.png "Bizmart Customer Page")

##### Item View
![Item details page wireframe example](/Assets/ItemView.png "Bizmart Item Detail Page")

##### Cart View
![Cart page wireframe example](/Assets/CartView.png "Bizmart Cart Page")

##### Employee View
![Employee page wireframe example](/Assets/EmployeeView.png "Bizmart Employee Page")

##### Manager View
![Manager page wireframe example](/Assets/ManagerView.png "Bizmart Manager Page")

### Development Plan - Time Table
#### Below is the development time table and planned benchmarks/milestones to accomplish this project by the due date.
Date | Items | Description
-----|-------------|--------------
9/23/24 | Project Proposal submitted | Proposal for consideration.
10/07/24 | Login View | Account login & registration for all users.
11/08/24 | Customer View | Pages to view & order products.
12/01/24 | Manager View | View to perform business related tasks.
12/09/24 | Polish/Final Submission | Project completion.

### Development Progress
#### Login View
The login view has been submitted for review as of October 21. Unforseen issues caused it to take longer to produce than predicted. Namely, the database had to be recreated in a different system because of connection issues. I also misjudged how long it would take to implement the logic needed to validate data when creating accounts and logging in. 
At this stage, users can sign in to an account, create a new customer account, or reset their password. Upon logging in, a popup message shows which type of account the user has. To reset an accounts password, the user must enter their username and answer three security questions. The account registration form ensures all data entered meets certain requirements, then adds the account to the database. 
Next, the customer view will be developed. The database table for storing products will be created, and the user will be able to use the interface to access those products.

### Customer View
The customer view has been submitted for review as of November 25. Users can search through items from the database by category and/or keyword(s). An account isn't required to browse products, but only signed-in users can add items to a cart and place an order. When users search through the inventory, thumbnails for each item are added to a scroll view so customers can quickly browse products.  The user can click a thumbnail to see more information on the product, and from there they can also add it to their cart. When an order is successfully placed, the quantity of each item ordrered is subtracted from the database. 
Next, the manager view will be developed, as well as the POS system for employees.

### Development Environment

Type | Description
-----|-------------
Language | Java
Development Environment | Visual Studio 2022 Community Edition
SQL Server Type/Dialect | Microsoft SQL (TSTC Server)
Target Environment | Windows 10
Target Business/Industry | Retail
Help System | Context Sensitive PDF Help Files
Report Methods | HTML Reports
Project Version Control | Git and Course assigned GitHub Repository

### Getting Started/Requirements/Prerequisites/Dependencies
- Download the program [here](https://drive.google.com/file/d/1UC5lmoP4dxk2B_mpzFlKIfyIKOUJRSlD/view?usp=sharing).
- Requires a stable internet connection.
- Double click the downloaded file to begin installation.
- Once the icon appears on your desktop, double click it to run the program.

### Demonstration Videos
- [Project Proposal](https://drive.google.com/file/d/1gz4FY8OUtMYNUOlXjmXgg8VSxetJHYWk/view?usp=sharing)
- [Logon View](https://drive.google.com/file/d/1_mYcD6zPqCsm0SnYlvtQR5_BupFckble/view?usp=sharing)
- [Customer View](https://drive.google.com/file/d/1wfNx2Ifo_hw--gDlNI49skH_RxVGlJoT/view?usp=sharing)
- [Manager View](https://drive.google.com/file/d/1u372Nc7nqqqVlRRx9ob7b4my5KySBYjq/view?usp=sharing)
- [Final Client Demonstration Video](https://drive.google.com/file/d/1Ptf4r1pHfCh8ndpRxdM2OcI3UhnL5WDk/view?usp=sharing)

### Contact
Contact | Information
--------|------
Name | Lynsey Wells
Email | lynseynwells@gmail.com
