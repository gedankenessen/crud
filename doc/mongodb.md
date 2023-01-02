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
  "_id": {
	"$oid": "63a34ef3cf7e797569e7dbec"
  },
  "userId": {
	"$oid": "6392357a6a845062bbc40597"
  },
  "name": "tasks",
  "timestamp": {
	"$numberLong": "1671646963"
  },
  "methods": [],
  "data": {
	"63a34ef3cf7e797569e7dbeb": {
	  "prio": "low",
	  "name": "Change socks",
	  "deadline": 7
	}
  }
}
```

### users

```
{
  "_id": {
	"$oid": "6392357a6a845062bbc40597"
  },
  "email": "marlon@gedankenessen.de",
  "password": "bcrypt+sha512$ba390f6f02f9db2f22b...",
  "salt": "[B@6201fcad",
  "name": "Marlon",
  "membership": "free",
  "status": "unconfirmed"
}
```
