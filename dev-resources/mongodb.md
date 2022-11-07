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

Current layout of the data stored in MongoDB. Uses one collection: `endpoints`.

```json
{
  "_id": {
    "$oid": "636930dfebf10b58dab566b7"
  },
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
    "$numberLong": "1667838175"
  },
  "name": "tables",
  "data": [
    {
      "legs": {
        "$numberLong": "4"
      },
      "color": "blue",
      "height": "120cm",
      "width": "120cm",
      "length": "120cm",
      "_id": {
        "$oid": "636930dfebf10b58dab566b6"
      }
    },
    {
      "legs": {
        "$numberLong": "3"
      },
      "color": "brown",
      "height": "40cm",
      "width": "200cm",
      "length": "80cm",
      "_id": {
        "$oid": "63693262ebf10b58dab566b9"
      }
    }
  ]
}
```

Probably will change to two collections: `endpoints` and `data`.
