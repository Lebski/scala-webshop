# scala-webshop
A webshop with Scala 2.12 &amp; Play 2.7.1

## Authentication
**POST** `/auth/admin`

Adds an inital admin account. Only executable once. 

```json
{
	"password": "SECUREPASS",
	"email": "email@admin.de",
	"firstName": "Admin",
	"lastName": "Admin",
	"language": "scala"
}
```

*Response:*
```json

{
 "userId": "c781a92e-8c6b-48c9-9f81-c1d175168e20",
 "message": "Admin created",
 "success": true
}
```

**POST** `/auth`

Claim JWT (to include in header) (user and admin)

You can use the token in your header `(AUTHORIZATION -> "eyJhbGciOiJIUzI1NiIsIn...")`

```json
{
 "password": "SECUREPASS",
 "userId": "b36b7e26-d314-4f50-a8f2-bb7815b601fb"
}
```

*Response:*
```json
{
 "JWT": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJTY2FsYVdlYlNob3AiLCJzdWIiOiJiMzZiN2UyNi1kMzE0LTRmNTAtYThmMi1iYjc4MTViNjAxZmIiLCJleHAiOjE1NTYzODI4NjQ4NTd9.TpQa7gcFCq3KwXJ8TDnMUXUsw56IxD77fqARYRvDWqI",
 "status": "Successful"
}

```
## User

**GET** `/user`

List all users (only admin)

*Response:*
```json
[
    {
        "id": "87b1fae6-fd1d-4cc8-ba58-fb3fcac2555b",
        "firstName": "UserFirstName",
        "lastName": "UserLastName",
        "language": "scala"
    },
    {
        "id": "c4b1bac5-e9f6-4249-9cca-e787049c67c9",
        "firstName": "Admin",
        "lastName": "Admin",
        "language": "scala"
    }
]
```
**POST** `/user`

Add a user (only admin)

```json
{
	"password": "SECUREPASS",
	"email": "email@user.de",
	"firstName": "UserFirstName",
	"lastName": "UserLastName",
	"language": "scala"
}
```

*Response:*
```json
{
 "userId": "c4b1bac5-e9f6-4249-9cca-e787049c67c9",
 "message": "User created"
}
```
### User with id

**GET** `/user/#userId`

Get a specific user (admin or user)

*Response:*
```json
{
    "id": "87b1fae6-fd1d-4cc8-ba58-fb3fcac2555b",
    "user": {
        "id": "87b1fae6-fd1d-4cc8-ba58-fb3fcac2555b",
        "firstName": "UserFirstName",
        "lastName": "UserLastName",
        "language": "German"
    },
    "address": {
        "countryCode": "none",
        "state": "none",
        "city": "none",
        "street": "none",
        "number": 0
    },
    "bankAccount": {
        "bankName": "none",
        "iban": "none",
        "bic": "none"
    },
    "email": "email@user.de"
}
```
**PUT** `/user/#userId`

Update user (admin or user)

```json
{
	"password": "SECUREPASS",
	"email": "email@user.de",
	"firstName": "Changed",
	"lastName": "Changed",
	"language": "java"
}
```

*Response:*
```json
{
	"user": {
		"id": "a341116d - 5586 - 422 d - aed7 - ec73745cc185",
		"user": {
			"id": "a341116d-5586-422d-aed7-ec73745cc185",
			"firstName": "Changed",
			"lastName": "Changed",
			"language": "java"
		},
		"address": {
			"countryCode": "none",
			"state": "none",
			"city": "none",
			"street": "none",
			"number": 0
		},
		"bankAccount": {
			"bankName": "none",
			"iban": "none",
			"bic": "none"
		},
		"email": "email@email.de"
	},
	"message": "User updated"
}
```

**DELETE** `/user/#userId`

Delete user (admin or user)

*Response:*
```json
{
 "user": "a341116d-5586-422d-aed7-ec73745cc185",
 "message": "User deleted"
}
```

## Store / Warehouse
**GET** `/warehouse`

Get all Items from warehouse (only admin)
```json
{
    "73cab054-e60e-4a25-9492-afc64b77d7e7": {
        "id": "73cab054-e60e-4a25-9492-afc64b77d7e7",
        "price": 333,
        "description": "Milk"
    },
    "e1558ad5-5f1c-425c-90ca-e21c8b9020d3": {
        "id": "e1558ad5-5f1c-425c-90ca-e21c8b9020d3",
        "price": 333,
        "description": "Milk"
    }
}
```
**POST** `/warehouse`

Add, modify and delte items from the warehouse (only admin)

```json
{
	"info": "hello",
	"updates": [{		
            "updateOperation": "add",
            "price": 333,
            "description": "Milk"
        },
        {		
            "updateOperation": "update",
            "id": "fd915f11-0964-447d-8d19-5d9005a1d801",
            "price": 333,
            "description": "Stones"
        },
        {		
            "updateOperation": "delete",
            "id": "fd915f11-0964-447d-8d19-5d9005a1d801"
        }]
}
```

*Response:*
```json
{
 "added": "1",
 "updated": "1",
 "deleted": "1"
}
```
### Single Items

**GET** `/warehouse/#itemId`

Get item with *itemId* from warehouse (only admin)

*Response:*
```json
{
 "id": "aa1fa717-4a26-424f-b504-63e0acfec92b",
 "price": "333,000000",
 "description": "Milk",
 "stock": "0"
}
```
**PUT** `/warehouse/#itemId`

Update item with *itemId* from warehouse (only admin)
```json
{		
"updateOperation": "add",
"price": 333,
"description": "Water",
}

```

*Response:*
```json
{
 "id": "aa1fa717-4a26-424f-b504-63e0acfec92b",
 "price": "333,000000",
 "description": "Water",
 "stock": "0"
}
```
**DELETE** `/warehouse/#itemId`

Delete item with *itemId* from warehouse (only admin)
```json
{
 "deleted": "aa1fa717-4a26-424f-b504-63e0acfec92b"
}
```
### Stock
**POST** `/warehouse/stock`

Increase or decrease the amount of items in stock
```json
{
 "info": "hello",
 "updates": [{		
     "updateOperation": "increase",
     "id": "c89e902e-7f51-4e7c-93f5-89cdbe570c3b",
     "amount": 20
 },
 {		
      "updateOperation": "decrease",
      "id": "c89e902e-7f51-4e7c-93f5-89cdbe570c3b",
      "amount": 10
  }]
}

```

*Response:*
```json
{
 "increased": "1",
 "decreased": "0"
}
```
**GET** `/warehouse/stock`

Get entire stock
```json
[
    [
        "6410ac63-6641-409f-b442-0c85d36b3fc9",
        "Bananas",
        10
    ],
    [
        "5df50517-7b52-4035-b34f-92fa7fc3b7e8",
        "Bananas",
        0
    ]
]


```
## Shopping cart

**GET** `/users/#userId/cart`

Get items in cart (user or admin)

*Response:*
```json
{
    "0b0262fb-74ea-4d45-a979-0ed513ced0f2": 1
}
```
**POST** `/users/#userId/cart`

Add, remove or discard Elements from ShoppingCart (user or admin)
```json
{
	"info": "hello",
	"updates": [{
		"productId": "ac15f734-c773-4b02-918e-08bac3163d05",
		"updateOperation": "add",
		"quantity": 3
	},
	{
		"productId": "ac15f734-c773-4b02-918e-08bac3163d05",
		"updateOperation": "remove",
		"quantity": 3
	},
	{
		"productId": "ac15f734-c773-4b02-918e-08bac3163d05",
		"updateOperation": "discard",
		"quantity": 3
	}]
}
```
*Response:*
```json
{
    "0b0262fb-74ea-4d45-a979-0ed513ced0f2": 1,
    "0ed513ce-a979-4d45-74ea-0ed513ced0f2": 1
}
```
**DELETE** `/users/#userId/cart`

Empty the whole ShoppingCart (user or admin)

*Response:*
```json
{}
```

**GET** `/users/#userId/cart/calcprice`

Calculate the price of a users's shopping cart (user or admin)

*Response*:
```json
{
 "price": "50000,000000",
}
 
```

**POST** `/users/#userId/cart/checkout`

Checkout user's shopping cart (user or admin)

*Response:*
```json
{
 "info": "Checkout successful",
 "successful": "true"
}
```
## Orders



**GET** `/orders`

List all orders (only admin)

*Response*:
```json
[
    {
        "id": "250e0058-4a4f-4418-8d8c-54a93151ed90",
        "user": {
            "id": "267ca2ca-f4c2-47e8-9443-bdb57a37a0cb",
            "firstName": "UserFirst",
            "lastName": "UserName",
            "language": "scala"
        },
        "items": {
            "3770ea1c-489f-467d-8b56-2a4d4232b33a": 1
        }
    }, 
    {
        "id": "5efe38a4-e304-4530-8d6e-31d5bdb056fa",
        "user": {
            "id": "267ca2ca-f4c2-47e8-9443-bdb57a37a0cb",
            "firstName": "UserFirst",
            "lastName": "UserName",
            "language": "scala"
         },
        "items": {
            "3770ea1c-489f-467d-8b56-2a4d4232b33a": 2
        }
    }
]
```

**GET** `/orders/#orderId`

Get order by id (only admin)

*Response:*
```json
{
 "id": "5efe38a4-e304-4530-8d6e-31d5bdb056fa",
 "user": {
     "id": "267ca2ca-f4c2-47e8-9443-bdb57a37a0cb",
     "firstName": "UserFirst",
     "lastName": "UserName",
     "language": "scala"
  },
 "items": {
     "3770ea1c-489f-467d-8b56-2a4d4232b33a": 2
 }
}
```

**DELETE** `/orders/#orderId`

Delete order by id (only admin)

*Response:*
```json
{
 "deleted": "250e0058-4a4f-4418-8d8c-54a93151ed90"
}
```