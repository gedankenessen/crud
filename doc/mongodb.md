# MongoDB

## Commands

Commands taken from ["Install MongoDB Community Edition on macOS"](https://www.mongodb.com/docs/v6.0/tutorial/install-mongodb-on-os-x/).

### run-mongo
```shell
brew services start mongodb-community@6.0
```

### stop-mongo
```shell
brew services stop mongodb-community@6.0
```

### run-mongo-locally
```shell
mongod --config /opt/homebrew/etc/mongod.conf --fork
```

### connect-mongo
```shell
mongosh
```

## Data layout

Currently uses two collections:
- `users` for `user` data
- `endpoints` which hold endpoint metadata and the actually submitted data

### endpoints

```json
{
  "userId": {
    "$oid": "63691793518fa064ce036c0c"
  },
  "methods": [
    "GET",
    "PUT",
    "POST",
    "DELETE"
  ],
  "timestamp": {
    "$numberLong": "1667921315"
  },
  "name": "focus",
  "data": {
    "636a75a36a263c5cff4da190": {
      "_id": {
        "$oid": "636a75a36a263c5cff4da190"
      },
      "data": {
        "x": {
          "$numberLong": "18"
        },
        "y": {
          "$numberLong": "24"
        }
      }
    }
  }
}
```

### users

```
{
  "_id": {
	"$oid": "63691793518fa064ce036c0c"
  },
  "name": "Marlon",
  "membership": "pro"
}
```
