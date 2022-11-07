# Structure of data

There are multiple structure the stored data can take on. This is further complicated by MongoDBs liberal approach to storage.

## Version 1

```js
const total = {
	"id": "61967a648c1b2d65e15b9713",
	"keys": [573721163728, 211637212341],
	"endpoints": {
		"users": {
			"created" : 1637212341,
			"versions": {
				"1637214708": {
					"methods": ["get", "get-id", "post", "put"],
					"data": [{}]
				}
			}
		}
	}
}
```

## Version 2

```js
// Version #2
const endpoints = {
	// id of endpoint
	"56f35f83c59949e4b86d909fdcecbbb0": {
	"name": "users",
	"created" : 1637212341,
	"keys": [573721163728, 211637212341],
	}
}

const versions = {
	// id of endpoint; "this endpoint has these versions"
	"be5a8a042ac04aa48063db9adc2b8b75": [
	{
		"id": "bb4ec32ab9504dcea539842abde6b881",
		"created": "1637214708",
		"methods": ["get", "get-id", "post", "put"],
	}
	]
}

const data = {
	// id of a version
	"bb4ec32ab9504dcea539842abde6b881": [{}, {}]
}

const user = {
	"endpoints": ["56f35f83c59949e4b86d909fdcecbbb0"]
}
```

## Version 3

```js
// Version #3
const into = {
	"user": "61967a648c1b2d65e15b9713",
	"endpoint": "users",
	"timestamp": "1637214708",
	"data": {}
}
```
